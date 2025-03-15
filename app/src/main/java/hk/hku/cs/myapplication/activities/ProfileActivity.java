package hk.hku.cs.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private TextView userNameTextView;
    private TextView userBioTextView;
    private String currentName = "John Doe"; // 默认姓名
    private String currentBio = "Hello, I'm a student at HKU!"; // 默认简介
    private BottomNavigationView bottomNavigationView;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 初始化视图
        userNameTextView = findViewById(R.id.userNameTextView);
        userBioTextView = findViewById(R.id.userBioTextView);

        editButton = findViewById(R.id.editProfileButton);

        // 设置当前信息
        userNameTextView.setText(currentName);
        userBioTextView.setText(currentBio);

        // 编辑个人资料按钮点击事件

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("currentName", currentName);
            intent.putExtra("currentBio", currentBio);
            startActivity(intent);
        });


        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(NavigationUtils.getNavListener(this));

        // 根据当前 Activity 设置选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
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