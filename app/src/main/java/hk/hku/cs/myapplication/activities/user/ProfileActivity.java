package hk.hku.cs.myapplication.activities.user;

import static android.view.Gravity.apply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.activities.auth.LoginActivity;
import hk.hku.cs.myapplication.models.UserInfoResponse;
import hk.hku.cs.myapplication.network.RetrofitClient;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_BIO = "userBio";

    private static final String KEY_USER_SCHOOL = "userSchool";
    private static final String KEY_USER_MAJOR = "userMajor";
    private static final String KEY_USER_REGISTERYEAR = "userRegisterYear";

    private TextView userNameTextView;
    private TextView userBioTextView;
    private TextView userEmailTextView;

    private TextView userSchoolTextView;
    private TextView userMajorTextView;
    private TextView userRegisterYearTextView;

    private String currentName = "John Doe"; // 默认姓名
    private String currentBio = "Hello, I'm a student at HKU!"; // 默认简介

    private String currentSchool = "Department of Engineering"; // 学院
    private String currentMajor = "Computer Science"; // 默认专业
    private String currentRegisterYear = "2024"; // 默认注册年份

    private BottomNavigationView bottomNavigationView;
    private Button editButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 初始化视图
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userBioTextView = findViewById(R.id.userBioTextView);

        userSchoolTextView = findViewById(R.id.userSchoolContentTextView);
        userMajorTextView = findViewById(R.id.userMajorContentTextView);
        userRegisterYearTextView = findViewById(R.id.userRegisterYearContentTextView);

        editButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        checkLoginStatus();
        loadMyInfoFromBackend();
        // 编辑个人资料按钮点击事件

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("currentName", currentName);
            intent.putExtra("currentBio", currentBio);
            intent.putExtra("currentSchool", currentSchool);
            intent.putExtra("currentMajor", currentMajor);
            intent.putExtra("currentRegisterYear", currentRegisterYear);
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

            currentSchool = "Engineering";
            currentMajor = "Computer Science";
            currentRegisterYear = "2024";

            userNameTextView.setText(currentName);
            userBioTextView.setText(currentBio);

            userSchoolTextView.setText(currentSchool);
            userMajorTextView.setText(currentMajor);
            userRegisterYearTextView.setText(currentRegisterYear);

            // 跳转到登录页面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }
    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // 加载保存的用户数据
            currentName = prefs.getString(KEY_USER_NAME, currentName);
            currentBio = prefs.getString(KEY_USER_BIO, currentBio);

            currentSchool = prefs.getString(KEY_USER_SCHOOL, currentSchool);
            currentMajor = prefs.getString(KEY_USER_MAJOR, currentMajor);
            currentRegisterYear = prefs.getString(KEY_USER_REGISTERYEAR, currentRegisterYear);

            userNameTextView.setText(currentName);
            userBioTextView.setText(currentBio);

            userSchoolTextView.setText(currentSchool);
            userMajorTextView.setText(currentMajor);
            userRegisterYearTextView.setText(currentRegisterYear);

            logoutButton.setVisibility(View.VISIBLE);

        }
        else{
            Log.d("profile", "not login in");
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

            currentSchool = data.getStringExtra("newSchool");
            currentMajor = data.getStringExtra("newMajor");
            currentRegisterYear = data.getStringExtra("newRegisterYear");

            // 更新界面
            userNameTextView.setText(currentName);
            userBioTextView.setText(currentBio);

            userSchoolTextView.setText(currentSchool);
            userMajorTextView.setText(currentMajor);
            userRegisterYearTextView.setText(currentRegisterYear);
        }
    }
    private void loadMyInfoFromBackend() {
        Call<UserInfoResponse> call = RetrofitClient.getInstance().getMyInfo();
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    UserInfoResponse userResponse = response.body();
                    if (userResponse.getCode() == 200) {
                        UserInfoResponse.Data userData = userResponse.getData();

                        // 更新UI
                        userNameTextView.setText(userData.getUsername());
                        userEmailTextView.setText(userData.getEmail());

                        userSchoolTextView.setText(userData.getSchool());
                        userMajorTextView.setText(userData.getMajor());
                        userRegisterYearTextView.setText(userData.getRegisterYear());

                        // 保存到SharedPreferences
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putString(KEY_USER_NAME, userData.getUsername())
                                .putString(KEY_USER_EMAIL, userData.getEmail())
                                .putString(KEY_USER_SCHOOL, userData.getSchool())
                                .putString(KEY_USER_MAJOR, userData.getMajor())
                                .putString(KEY_USER_REGISTERYEAR, userData.getRegisterYear())
                                .apply();
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "Error: " + userResponse.getMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Failed to load user info",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Network error", t);
                Toast.makeText(ProfileActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}