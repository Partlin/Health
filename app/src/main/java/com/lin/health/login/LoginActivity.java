package com.lin.health.login;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.lin.health.R;
import com.lin.health.database.UserService;
import com.lin.health.temp.TemperatureSensorsActivity;

public class LoginActivity extends Activity {
    EditText username;
    EditText password;
    Button login,register;
    CheckBox CBpass;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        username = (EditText) findViewById(R.id.usernameET);
        password = (EditText) findViewById(R.id.passwordET);
        login = (Button) findViewById(R.id.logBT);
        register = (Button) findViewById(R.id.register);
        CBpass = (CheckBox) findViewById(R.id.savePasswordCB);
        password.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (sp.getBoolean("checkboxBoolean", false)) {
            username.setText(sp.getString("username", null));
            password.setText(sp.getString("password", null));
            CBpass.setChecked(true);

        }

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = username.getText().toString();
                String pass = password.getText().toString();
                Log.i("TAG", name + "_" + pass);
                if (name.trim().equals("")) {
                    Toast.makeText(LoginActivity.this,
                            "请您输入用户名！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.trim().equals("")) {
                    Toast.makeText(LoginActivity.this,
                            "请您输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean CheckBoxLogin = CBpass.isChecked();
                if (CheckBoxLogin) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", name);
                    editor.putString("password", pass);
                    editor.putBoolean("checkboxBoolean", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", null);
                    editor.putString("password", null);
                    editor.putBoolean("checkboxBoolean", false);
                    editor.commit();
                }


                UserService uService = new UserService(LoginActivity.this);
                boolean flag = uService.login(name, pass);
                if (flag) {
                    Log.i("TAG", "登录成功");
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, TemperatureSensorsActivity.class);
                    startActivity(intent);
                } else {
                    Log.i("TAG", "登录失败");
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}