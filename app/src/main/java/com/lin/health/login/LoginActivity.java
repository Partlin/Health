package com.lin.health.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.lin.health.R;
import com.lin.health.database.UserDbAdapter;

public class LoginActivity extends Activity {

    AutoCompleteTextView cardNumAuto;
    EditText passwordET;
    Button logBT;
    Button regBT;

    CheckBox savePasswordCB;
    SharedPreferences sp;
    String cardNumStr;
    String passwordStr;
    private UserDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardNumAuto = (AutoCompleteTextView) findViewById(R.id.cardNumAuto);
        passwordET = (EditText) findViewById(R.id.passwordET);
        logBT = (Button) findViewById(R.id.logBT);
        regBT = (Button) findViewById(R.id.resBT);


        sp = this.getSharedPreferences("passwordFile", MODE_PRIVATE);
        savePasswordCB = (CheckBox) findViewById(R.id.savePasswordCB);
        savePasswordCB.setChecked(true);// 默认为记住密码
        cardNumAuto.setThreshold(1);// 输入1个字母就开始自动提示
        passwordET.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        // 隐藏密码为InputType.TYPE_TEXT_VARIATION_PASSWORD，也就是0x81
        // 显示密码为InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD，也就是0x91

        cardNumAuto.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String[] allUserName = new String[sp.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
                allUserName = sp.getAll().keySet().toArray(new String[0]);
                // sp.getAll()返回一张hash map
                // keySet()得到的是a set of the keys.
                // hash map是由key-value组成的

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        allUserName);

                cardNumAuto.setAdapter(adapter);// 设置数据适配器

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordET.setText(sp.getString(cardNumAuto.getText()
                        .toString(), ""));// 自动输入密码

            }
        });

        // 登陆
        logBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cardNumStr = cardNumAuto.getText().toString();
                passwordStr = passwordET.getText().toString();
                if((cardNumStr == null||cardNumStr.equalsIgnoreCase("")) || (passwordStr == null||passwordStr.equalsIgnoreCase(""))){
                    Toast.makeText(LoginActivity.this, "The user name and password are necessary.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Cursor cursor = mDbHelper.getDiary(cardNumStr);

                    if(!cursor.moveToFirst()){
                        Toast.makeText(LoginActivity.this, "The user name doesn't exist.",
                                Toast.LENGTH_SHORT).show();
                    }else if (!passwordStr.equals(cursor.getString(2))) {
                        Toast.makeText(LoginActivity.this, "The password is wrong, please enter again.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (savePasswordCB.isChecked()) {// 登陆成功才保存密码
                            sp.edit().putString(cardNumStr, passwordStr).commit();
                        }
                        Toast.makeText(LoginActivity.this, "Login successfully. Wait for getting the information of the user...",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("username", cardNumStr);
                        intent.setClass(LoginActivity.this, com.lin.health.MainActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });

        mDbHelper = new UserDbAdapter(this);
        mDbHelper.open();

        regBT.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });



    };



}