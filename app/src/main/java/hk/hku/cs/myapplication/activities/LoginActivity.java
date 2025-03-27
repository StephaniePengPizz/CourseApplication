package hk.hku.cs.myapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hk.hku.cs.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button loginButton;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvRegister = findViewById(R.id.tvRegister);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.loginButton);
        tvRegister = findViewById(R.id.tvRegister);

        loginButton.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 简单的验证
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("请输入有效的邮箱地址");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("请输入密码");
            return;
        }

        // 模拟登录成功
        //SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        //boolean isRegistered = prefs.contains("email"); // 检查用户是否已注册

        //if (isRegistered) {
        //    String savedEmail = prefs.getString("email", "");
         //   String savedPassword = "123456"; // 模拟密码

        //    if (email.equals(savedEmail) && password.equals(savedPassword)) {
        //        prefs.edit().putBoolean("isLoggedIn", true).apply();
        //        startActivity(new Intent(this, MainActivity.class));
        //        finish();
        //    } else {
        //        Toast.makeText(this, "邮箱或密码错误", Toast.LENGTH_SHORT).show();
        //    }
        //} else {
        //    Toast.makeText(this, "用户未注册，请先注册", Toast.LENGTH_SHORT).show();
       // }
    }
}