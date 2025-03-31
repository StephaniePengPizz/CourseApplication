package hk.hku.cs.myapplication.activities.auth;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.activities.course.MainActivity;
import hk.hku.cs.myapplication.models.LoginRequest;
import hk.hku.cs.myapplication.models.LoginResponse;
import hk.hku.cs.myapplication.models.User;
import hk.hku.cs.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button loginButton;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetrofitClient.initialize(this);
        setContentView(R.layout.activity_login);

        tvRegister = findViewById(R.id.tvRegister);
        etUsername = findViewById(R.id.etUsername);
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
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 简单的验证
        if (username.isEmpty()) {
            etUsername.setError("Please Enter Username");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please Enter Password");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login in...");
        progressDialog.show();

        LoginRequest request = new LoginRequest(username, password);
        Call<LoginResponse> call = RetrofitClient.getInstance().login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Gson gson = new Gson();
                    String responseJson = gson.toJson(loginResponse);
                    Log.d("LoginDebug", "响应数据: " + responseJson);
                    if (loginResponse.isSuccess()) {
                        // 保存登录状态和token
                        saveLoginData(loginResponse);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败: 用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,
                        "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
    private void saveLoginData(LoginResponse response) {
        if (response == null || response.getData() == null) {
            return; // Or handle the error appropriately
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // 保存基本登录状态
        editor.putBoolean("isLoggedIn", true);

        // 保存token信息
        editor.putString("authToken", response.getData().getAccessToken());
        editor.putString("tokenExpireTime", response.getData().getAccessTokenExpireTime());
        editor.putString("sessionUuid", response.getData().getSessionUuid());

        // 保存user信息
        if (response.getData().getUser() != null) {
            User user = response.getData().getUser();
            editor.putString("username", user.getName());
            editor.putString("email", user.getEmail());
            editor.putInt("userId", user.getId());
        }

        editor.apply();

    }
}