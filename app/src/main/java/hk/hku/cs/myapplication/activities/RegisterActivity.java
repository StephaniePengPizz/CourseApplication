package hk.hku.cs.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import hk.hku.cs.myapplication.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> navigateToLogin());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    private void attemptRegister() {

        Log.d("11", "nono");

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Please enter username");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter valid email");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password requires at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次输入的密码不一致");
            return;
        }
        registerUser(username, password, email);

        //Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();

        // 保存用户信息到SharedPreferences
        //SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        //SharedPreferences.Editor editor = prefs.edit();
        //editor.putString("userName", username);
        //editor.putString("email", email);
        //editor.putBoolean("isLoggedIn", true);
        //editor.apply();

        // 跳转到主界面
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    private void registerUser(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);
        Call<Void> call = RetrofitClient.getInstance().register(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            String errorBody = response.errorBody().string();
                            Toast.makeText(RegisterActivity.this,
                                    "错误: " + response.code() + " - " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(RegisterActivity.this,
                                    "响应解析错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                runOnUiThread(() -> {
                    String errorMsg = "请求失败: ";
                    if (t instanceof SocketTimeoutException) {
                        errorMsg += "连接超时";
                    } else if (t instanceof UnknownHostException) {
                        errorMsg += "域名解析失败";
                    } else {
                        errorMsg += t.getMessage();
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}