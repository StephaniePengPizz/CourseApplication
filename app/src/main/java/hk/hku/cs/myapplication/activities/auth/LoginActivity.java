package hk.hku.cs.myapplication.activities.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import hk.hku.cs.myapplication.MyApplication;
import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.activities.course.PoolCourseActivity;
import hk.hku.cs.myapplication.models.user.LoginRequest;
import hk.hku.cs.myapplication.models.user.LoginResponse;
import hk.hku.cs.myapplication.models.user.User;
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

        loginButton.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Please enter username");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter password");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login...");
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
                    Log.d("LoginDebug", "response: " + gson.toJson(loginResponse));

                    if (loginResponse.isSuccess()) {
                        saveLoginData(loginResponse);
                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, PoolCourseActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to Login: username or password errors", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginData(LoginResponse response) {
        if (response == null || response.getData() == null) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isLoggedIn", true);
        editor.putString("authToken", response.getData().getAccessToken());
        editor.putString("tokenExpireTime", response.getData().getAccessTokenExpireTime());
        editor.putString("sessionUuid", response.getData().getSessionUuid());

        if (response.getData().getUser() != null) {
            User user = response.getData().getUser();
            // ✅ 改用 email 保存
            editor.putString("username", user.getEmail());
            editor.putString("email", user.getEmail());
            editor.putInt("userId", user.getId());

            Log.d("LoginDebug", "user.getEmail() = " + user.getEmail());

            MyApplication.setUsername(user.getEmail()); // ✅ 改成用email保存
        } else {
            Log.e("LoginDebug", "user is null");
        }

        editor.apply();
    }
}
