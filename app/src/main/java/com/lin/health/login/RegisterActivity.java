package com.lin.health.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lin.health.MainActivity;
import com.lin.health.R;
import com.lin.health.database.UserService;
import com.lin.health.temp.TemperatureSensorsActivity;

public class RegisterActivity extends Activity {
    EditText username;
    EditText password;
    EditText age;
    EditText conpasswordET;
    RadioGroup sex;
    Button register;
    Button cancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        findViews();
        password.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        conpasswordET.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name=username.getText().toString().trim();
                String pass=password.getText().toString().trim();
                String conPassword=conpasswordET.getText().toString().trim();
                String agestr=age.getText().toString().trim();
                String sexstr=((RadioButton)RegisterActivity.this.findViewById(sex.getCheckedRadioButtonId())).getText().toString();
                Log.i("TAG",name+"_"+pass+"_"+agestr+"_"+sexstr);

//                User user=new User();
//                user.setUsername(name);
//                user.setPassword(pass);
//                user.setAge(Integer.parseInt(agestr));
//                user.setSex(sexstr);

                    if (name.trim().equals("")) {
                        Toast.makeText(RegisterActivity.this,
                                "请您输入用户名！", Toast.LENGTH_SHORT).show();
                    } else if (pass.trim().equals("")) {
                        Toast.makeText(RegisterActivity.this,
                                "请您输入密码！", Toast.LENGTH_SHORT).show();
                    } else if (conPassword.trim().equals("")) {
                        Toast.makeText(RegisterActivity.this,
                                "请您再次输入密码！", Toast.LENGTH_SHORT).show();
                    } else if (agestr.trim().equals("")) {
                        Toast.makeText(RegisterActivity.this,
                                "请您输入年龄！", Toast.LENGTH_SHORT).show();
                    } else if (!pass.equals(conPassword)) {
                        Toast.makeText(RegisterActivity.this, "密码不一致，请重新输入",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        UserService uService = new UserService(RegisterActivity.this);
                        boolean flag = uService.register(name, pass, agestr, sexstr);
                        if (flag) {
                            Log.i("TAG", "注册成功");
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, TemperatureSensorsActivity.class);
                            startActivity(intent);
                        } else {
                            Log.i("TAG", "注册失败");
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                        }
                    }

                }




        });
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });
    }
    private void findViews() {
        username=(EditText) findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        conpasswordET= (EditText) findViewById(R.id.conpasswordET);
        age=(EditText) findViewById(R.id.ageRegister);
        sex=(RadioGroup) findViewById(R.id.sexRegister);
        register=(Button) findViewById(R.id.commit);
        cancel= (Button) findViewById(R.id.canBT);
    }

}


