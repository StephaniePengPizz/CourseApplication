package hk.hku.cs.myapplication.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import hk.hku.cs.myapplication.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editNameEditText;
    private EditText editBioEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 初始化视图
        editNameEditText = findViewById(R.id.editNameEditText);
        editBioEditText = findViewById(R.id.editBioEditText);
        Button saveProfileButton = findViewById(R.id.saveProfileButton);

        // 从 Intent 中获取当前用户信息
        Intent intent = getIntent();
        String currentName = intent.getStringExtra("currentName");
        String currentBio = intent.getStringExtra("currentBio");

        // 设置当前信息到输入框
        editNameEditText.setText(currentName);
        editBioEditText.setText(currentBio);

        // 保存按钮点击事件
        saveProfileButton.setOnClickListener(v -> saveProfile());


    }

    private void saveProfile() {
        Log.d("EditProfileActivity", "Save Profile button clicked");
        // 获取用户输入
        String newName = editNameEditText.getText().toString();
        String newBio = editBioEditText.getText().toString();

        Log.d("EditProfileActivity", "New Name: " + newName);
        Log.d("EditProfileActivity", "New Bio: " + newBio);

        // 返回更新后的信息到 ProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("newName", newName);
        resultIntent.putExtra("newBio", newBio);
        setResult(RESULT_OK, resultIntent);

        // 关闭当前界面
        finish();
    }
}