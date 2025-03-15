package hk.hku.cs.myapplication.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.CourseAdapter;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button switchButton;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
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
        courseList = new ArrayList<>();
        courseList.add(new Course("Math", "09:00 AM", "Room 101", "Monday"));
        courseList.add(new Course("Science", "10:00 AM", "Room 102", "Tuesday"));
        courseList.add(new Course("English", "01:00 PM", "Room 104", "Thursday"));
        courseList.add(new Course("Physics", "02:00 PM", "Room 105", "Friday"));

        // 设置适配器
        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);

        // 切换按钮点击事件
        switchButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TableActivity.class);
            startActivity(intent);
        });

        for (Course course : courseList) {
            Log.d("CourseData", "Name: " + course.getCourseName() + ", Time: " + course.getCourseTime());
        }

        // 添加课程按钮点击事件
        findViewById(R.id.addCourseButton).setOnClickListener(v -> showAddCourseDialog());

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));

        // 根据当前 Activity 设置选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_course);
    }


    private void showAddCourseDialog() {
        // 创建对话框视图
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_course, null);

        // 获取输入框和按钮
        EditText courseNameEditText = dialogView.findViewById(R.id.courseNameEditText);
        Button selectTimeButton = dialogView.findViewById(R.id.selectTimeButton);
        TextView selectedTimeTextView = dialogView.findViewById(R.id.selectedTimeTextView);
        EditText courseLocationEditText = dialogView.findViewById(R.id.courseLocationEditText);
        Spinner daySpinner = dialogView.findViewById(R.id.daySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        // 时间选择按钮点击事件
        selectTimeButton.setOnClickListener(v -> showTimePicker(selectedTimeTextView));

        // 创建对话框
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Course")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, which) -> {
                    // 获取用户输入
                    String courseName = courseNameEditText.getText().toString();
                    String courseTime = selectedTime;
                    String courseLocation = courseLocationEditText.getText().toString();
                    String day = daySpinner.getSelectedItem().toString();

                    // 添加新课程
                    if (!courseName.isEmpty() && !courseTime.isEmpty() && !courseLocation.isEmpty()) {
                        courseList.add(new Course(courseName, courseTime, courseLocation, day));
                        courseAdapter.notifyDataSetChanged(); // 通知适配器数据已更新
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void showTimePicker(TextView selectedTimeTextView) {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 创建 TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    // 格式化选择的时间
                    selectedTime = String.format("%02d:%02d %s",
                            hourOfDay > 12 ? hourOfDay - 12 : hourOfDay,
                            minute1,
                            hourOfDay >= 12 ? "PM" : "AM");

                    // 更新显示的已选择时间
                    selectedTimeTextView.setText("Selected Time: " + selectedTime);
                },
                hour,
                minute,
                false // 是否使用 24 小时制
        );

        timePickerDialog.show();
    }
}