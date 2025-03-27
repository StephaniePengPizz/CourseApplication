package hk.hku.cs.myapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class ProfileActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_BIO = "userBio";
    private TextView userNameTextView;
    private TextView userBioTextView;
    private String currentName = "John Doe"; // 默认姓名
    private String currentBio = "Hello, I'm a student at HKU!"; // 默认简介
    private BottomNavigationView bottomNavigationView;
    private Button editButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 初始化视图
        userNameTextView = findViewById(R.id.userNameTextView);
        userBioTextView = findViewById(R.id.userBioTextView);

        editButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        checkLoginStatus();

        // 编辑个人资料按钮点击事件

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("currentName", currentName);
            intent.putExtra("currentBio", currentBio);
            startActivity(intent);
        });
        logoutButton.setOnClickListener(v -> {
            // 清除登录状态
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_IS_LOGGED_IN, false)
                    .apply();

            // 重置为默认值
            currentName = "John Doe";
            currentBio = "Hello, I'm a student at HKU!";
            userNameTextView.setText(currentName);
            userBioTextView.setText(currentBio);

            // 跳转到登录页面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        // 根据当前 Activity 设置选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }
    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // 加载保存的用户数据
            currentName = prefs.getString(KEY_USER_NAME, currentName);
            currentBio = prefs.getString(KEY_USER_BIO, currentBio);

            userNameTextView.setText(currentName);
            userBioTextView.setText(currentBio);
            logoutButton.setVisibility(View.VISIBLE);

        }
        else{
            Log.d("profile", "nononononono");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ProfileActivity", "onActivityResult called");
        Log.d("ProfileActivity", "Request Code: " + requestCode);
        Log.d("ProfileActivity", "Result Code: " + resultCode);
        // 处理 EditProfileActivity 返回的结果
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // 获取更新后的信息
            currentName = data.getStringExtra("newName");
            currentBio = data.getStringExtra("newBio");

            // 更新界面
            userNameTextView.setText(currentName);
            userBioTextView.setText(currentBio);
        }
    }
}