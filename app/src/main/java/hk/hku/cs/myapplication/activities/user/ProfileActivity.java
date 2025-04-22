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

import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hk.hku.cs.myapplication.models.Course;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.activities.auth.LoginActivity;
import hk.hku.cs.myapplication.models.UserInfoResponse;
import hk.hku.cs.myapplication.network.RetrofitClient;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import hk.hku.cs.myapplication.models.CourseMyListResponse;


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

    // tableView
    private TableLayout tableLayout;
    private Button switchButton;
    private List<Course> courseList;


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

        // tableView
        tableLayout = findViewById(R.id.tableLayout);
        courseList = new ArrayList<>();

        loadMyCoursesFromBackend();

        // 设置表格布局
        updateTableLayout();



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

    private void updateTableLayout() {
        tableLayout.removeAllViews(); // 清空旧表格

        // 1. 按“星期+时间”组织课程映射
        Map<String, Map<String, Course>> coursesByDayAndTime = new HashMap<>();
        SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.US);
        SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);

        for (Course course : courseList) {
            if (course.getSchedules() != null) {
                for (Course.Schedule schedule : course.getSchedules()) {
                    String rawDay = schedule.getDayOfWeek().trim();       // e.g. "Monday"
                    String day = convertDayToShort(rawDay);               // e.g. "Mon"

                    try {
                        Date start = sdf24.parse(schedule.getStartTime());
                        Date end = sdf24.parse(schedule.getEndTime());

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(start);

                        while (!cal.getTime().after(end)) {
                            String timeSlot = sdf12.format(cal.getTime()); // e.g. "09:00 AM"

                            if (!coursesByDayAndTime.containsKey(day)) {
                                coursesByDayAndTime.put(day, new HashMap<>());
                            }
                            coursesByDayAndTime.get(day).put(timeSlot, course);

                            cal.add(Calendar.HOUR_OF_DAY, 1); // 每节课按小时跨格子
                        }

                    } catch (Exception e) {
                        Log.e("ScheduleError", "时间解析失败: " + e.getMessage());
                    }
                }
            }
        }

        // 2. 固定时间和星期几的顺序（注意：时间要和后端数据一致）
        String[] times = {
                "09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM",
                "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
                "06:00 PM", "07:00 PM"
        };
        String[] days = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};

        // 3. 添加表头（第一行：Time | Mon | Tue | ...）
        TableRow headerRow = new TableRow(this);
        addTextViewToRow(headerRow, "Time"); // true = 加粗
        for (String day : days) {
            addTextViewToRow(headerRow, day);
        }
        tableLayout.addView(headerRow);

        // 4. 添加课程内容行
        for (String time : times) {
            TableRow row = new TableRow(this);
            addTextViewToRow(row, time); // 左边时间列加粗

            for (String day : days) {
                if (coursesByDayAndTime.containsKey(day) &&
                        coursesByDayAndTime.get(day).containsKey(time)) {

                    Course course = coursesByDayAndTime.get(day).get(time);
                    String display = course.getCourseName() + "\n@" + course.getPrimaryLocation();
                    addTextViewToRow(row, display);
                } else {
                    addTextViewToRow(row, ""); // 空单元格
                }
            }

            tableLayout.addView(row);
        }
    }


    private void addTextViewToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(12);

        // 相同列宽
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        textView.setLayoutParams(layoutParams);

        row.addView(textView);
    }

    private void loadMyCoursesFromBackend() {
        Call<CourseMyListResponse> call = RetrofitClient.getInstance().getMyCourses();
        call.enqueue(new Callback<CourseMyListResponse>() {
            @Override
            public void onResponse(Call<CourseMyListResponse> call, Response<CourseMyListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseMyListResponse courseResponse = response.body();
                    if (courseResponse.getCode() == 200) {
                        courseList = courseResponse.getData();  // 填充 courseList

                        Log.d("CourseDebug", "课程数量: " + courseList.size());
                        if (!courseList.isEmpty()) {
                            Log.d("FirstCourse", courseList.get(0).getCourseName());
                        }

                        updateTableLayout();  // 渲染课程表
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "课程加载失败: " + courseResponse.getMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "响应失败: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseMyListResponse> call, Throwable t) {
                Log.e("COURSE_API", "课程请求失败", t);
                Toast.makeText(ProfileActivity.this,
                        "网络异常: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertDayToShort(String fullDay) {
        switch (fullDay.toLowerCase()) {
            case "monday": return "Mon";
            case "tuesday": return "Tue";
            case "wednesday": return "Wed";
            case "thursday": return "Thur";
            case "friday": return "Fri";
            case "saturday": return "Sat";
            case "sunday": return "Sun";
            default: return fullDay;
        }
    }

    private String convertTimeTo12Hour(String time24) {
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);
            return sdf12.format(sdf24.parse(time24));
        } catch (Exception e) {
            return time24;
        }
    }


}