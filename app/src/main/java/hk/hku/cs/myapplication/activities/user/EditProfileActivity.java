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

    private EditText editEmailEditText;
    private EditText editSchoolEditText;
    private EditText editMajorEditText;
    private EditText editRegisterYearEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 初始化视图
        editNameEditText = findViewById(R.id.editNameEditText);
        editBioEditText = findViewById(R.id.editBioEditText);

        editEmailEditText = findViewById(R.id.editEmailEditText);
        editSchoolEditText = findViewById(R.id.editSchoolEditText);
        editMajorEditText = findViewById(R.id.editMajorEditText);
        editRegisterYearEditText = findViewById(R.id.editRegisterYearEditText);

        Button saveProfileButton = findViewById(R.id.saveProfileButton);

        // 从 Intent 中获取当前用户信息
        Intent intent = getIntent();
        String currentName = intent.getStringExtra("currentName");
        String currentBio = intent.getStringExtra("currentBio");

        String currentEmail = intent.getStringExtra("currentEmail");
        String currentSchool = intent.getStringExtra("currentSchool");
        String currentMajor = intent.getStringExtra("currentMajor");
        String currentRegisterYear = intent.getStringExtra("currentRegisterYear");

        // 设置当前信息到输入框
        editNameEditText.setText(currentName);
        editBioEditText.setText(currentBio);

        editEmailEditText.setText(currentEmail);
        editSchoolEditText.setText(currentSchool);
        editMajorEditText.setText(currentMajor);
        editRegisterYearEditText.setText(currentRegisterYear);

        // 保存按钮点击事件
        saveProfileButton.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        Log.d("EditProfileActivity", "Save Profile button clicked");
        // 获取用户输入
        String newName = editNameEditText.getText().toString();
        String newBio = editBioEditText.getText().toString();

        String newEmail = editEmailEditText.getText().toString();
        String newSchool = editSchoolEditText.getText().toString();
        String newMajor = editMajorEditText.getText().toString();
        String newRegisterYear = editRegisterYearEditText.getText().toString();

        Log.d("EditProfileActivity", "New Name: " + newName);
        Log.d("EditProfileActivity", "New Bio: " + newBio);

        Log.d("EditProfileActivity", "New Email: " + newEmail);
        Log.d("EditProfileActivity", "New School: " + newSchool);
        Log.d("EditProfileActivity", "New Major: " + newMajor);
        Log.d("EditProfileActivity", "New RegisterYear: " + newRegisterYear);

        // 返回更新后的信息到 ProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("newName", newName);
        resultIntent.putExtra("newBio", newBio);

        resultIntent.putExtra("newEmail", newEmail);
        resultIntent.putExtra("newSchool", newSchool);
        resultIntent.putExtra("newMajor", newMajor);
        resultIntent.putExtra("newRegisterYear", newRegisterYear);

        setResult(RESULT_OK, resultIntent);

        // 关闭当前界面
        finish();
    }
}