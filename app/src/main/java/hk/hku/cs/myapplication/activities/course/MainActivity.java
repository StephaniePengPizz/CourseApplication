package hk.hku.cs.myapplication.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.CourseAdapter;
import hk.hku.cs.myapplication.models.AddCourseRequest;
import hk.hku.cs.myapplication.models.AddScheduleRequest;
import hk.hku.cs.myapplication.models.ApiResponse;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.ForumManager;
import hk.hku.cs.myapplication.network.RetrofitClient;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button switchButton;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>(); // 初始化列表;
    private String selectedTime = ""; // 保存用户选择的时间
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        switchButton = findViewById(R.id.switchButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化默认课程表
        //courseList = new ArrayList<>();
        //courseList.add(new Course("Math", "09:00 AM", "Room 101", "Monday"));
        //courseList.add(new Course("Science", "10:00 AM", "Room 102", "Tuesday"));
        //courseList.add(new Course("English", "01:00 PM", "Room 104", "Thursday"));
        //courseList.add(new Course("Physics", "02:00 PM", "Room 105", "Friday"));
        loadMyCoursesFromBackend();

        // 设置适配器
        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);

        // 切换按钮点击事件
        switchButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TableActivity.class);
            startActivity(intent);
        });

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));

        // 根据当前 Activity 设置选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_my_course);
    }

    private void loadMyCoursesFromBackend() {
    }
    private void showAddCourseDialog() {
        // 创建对话框视图
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_course, null);

        // 获取输入框和按钮
        EditText courseCodeEditText = dialogView.findViewById(R.id.courseCodeEditText);
        EditText courseNameEditText = dialogView.findViewById(R.id.courseNameEditText);
        EditText startTimeEditText = dialogView.findViewById(R.id.startTimeEditText);
        EditText endTimeEditText = dialogView.findViewById(R.id.endTimeEditText);
        EditText courseLocationEditText = dialogView.findViewById(R.id.courseLocationEditText);
        Spinner daySpinner = dialogView.findViewById(R.id.daySpinner);
        Spinner semesterSpinner = dialogView.findViewById(R.id.semesterSpinner);

        // 设置星期几下拉列表
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        // 设置学期下拉列表
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.semester_options,
                android.R.layout.simple_spinner_item
        );
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        // 创建对话框
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Course")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, which) -> {
                    // 创建课程对象
                    Course course = new Course();
                    course.setCourseCode(courseCodeEditText.getText().toString().trim());
                    course.setCourseName(courseNameEditText.getText().toString().trim());
                    course.setSemester(semesterSpinner.getSelectedItem().toString());

                    // 创建课程时间表
                    Course.Schedule schedule = new Course.Schedule();
                    schedule.setDayOfWeek(daySpinner.getSelectedItem().toString());
                    schedule.setStartTime(formatTime(startTimeEditText.getText().toString().trim()));
                    schedule.setEndTime(formatTime(endTimeEditText.getText().toString().trim()));
                    schedule.setLocation(courseLocationEditText.getText().toString().trim());

                    // 添加到课程
                    List<Course.Schedule> schedules = new ArrayList<>();
                    schedules.add(schedule);
                    course.getSchedules(schedules);

                    addCourseToBackend(course);//论坛也在这个里面
                    ForumManager.getOrCreateForum(course.getCourseName());// 自动创建论坛
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
    private String formatTime(String time) {
        // 假设用户输入的是简单的HH:mm格式
        // 如果需要转换为ISO格式，可以在这里处理
        // 例如: "09:00" -> "T09:00:00"
        return "T" + time + ":00";
    }

    private void addCourseToBackend(Course course) {

        // 1. 首先添加课程基本信息
        AddCourseRequest courseRequest = new AddCourseRequest(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseDescription(),
                course.getSemester()
        );

        Call<ApiResponse<Course>> courseCall = RetrofitClient.getInstance()
                .createCourse(courseRequest);

        courseCall.enqueue(new Callback<ApiResponse<Course>>() {
            @Override
            public void onResponse(Call<ApiResponse<Course>> call, Response<ApiResponse<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Course> apiResponse = response.body();

                    if (apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                        // 课程添加成功，获取返回的课程ID
                        Course createdCourse = apiResponse.getData();
                        int courseId = createdCourse.getId();

                        // 2. 添加课程时间表
                        addCourseScheduleToBackend(courseId, course.getSchedules().get(0));
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Failed to add course: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Course>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCourseScheduleToBackend(int courseId, Course.Schedule schedule) {
        // 格式化日期为YYYY-MM-DD (这里需要根据实际需求实现)
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        // 创建时间表请求
        AddScheduleRequest scheduleRequest = new AddScheduleRequest(
                courseId,
                currentDate,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLocation()
        );

        Call<ApiResponse<Course.Schedule>> scheduleCall = RetrofitClient.getInstance()
                .addCourseSchedule(scheduleRequest);

        scheduleCall.enqueue(new Callback<ApiResponse<Course.Schedule>>() {
            @Override
            public void onResponse(Call<ApiResponse<Course.Schedule>> call,
                                   Response<ApiResponse<Course.Schedule>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Course.Schedule> apiResponse = response.body();

                    if (apiResponse.getCode() == 200) {
                        // 课程和时间表都添加成功
                        loadMyCoursesFromBackend(); // 刷新课程列表
                        Toast.makeText(MainActivity.this,
                                "Course added successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Failed to add schedule: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Failed to add schedule",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Course.Schedule>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}