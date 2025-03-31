package hk.hku.cs.myapplication.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class TableActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Button switchButton;
    private List<Course> courseList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        // 初始化视图
        tableLayout = findViewById(R.id.tableLayout);
        switchButton = findViewById(R.id.switchButton);

        // 初始化默认课程表
        courseList = new ArrayList<>();
        //courseList.add(new Course("11", "Math", "09:00 AM", "Room 101", "Mon", 1));
        //courseList.add(new Course("21","Science", "10:00 AM", "Room 102", "Tue", 1));
        //courseList.add(new Course("31", "English", "01:00 PM", "Room 104", "Thur", 1));
        //courseList.add(new Course("41", "Physics", "02:00 PM", "Room 105", "Fri", 1));

        // 设置表格布局
        updateTableLayout();

        // 切换按钮点击事件
        switchButton.setOnClickListener(v -> {
            Intent intent = new Intent(TableActivity.this, MyCourseActivity.class);
            startActivity(intent);
        });

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));

    }

    private void updateTableLayout() {
        tableLayout.removeAllViews(); // 清空表格

        // 按天分组
        Map<String, Map<String, Course>> coursesByDayAndTime = new HashMap<>();
        for (Course course : courseList) {
            String day = course.getSchedules().get(course.getId()).getDayOfWeek();
            String time = course.getPrimaryScheduleTime();
            if (!coursesByDayAndTime.containsKey(day)) {
                coursesByDayAndTime.put(day, new HashMap<>());
            }
            coursesByDayAndTime.get(day).put(time, course);
        }

        // 定义时间和星期几的顺序
        String[] times = {"09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM", "02:00 PM"};
        String[] days = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};

        // 添加表头（星期几）
        TableRow headerRow = new TableRow(this);
        addTextViewToRow(headerRow, "Time"); // 第一列为时间
        for (String day : days) {
            addTextViewToRow(headerRow, day);
        }
        tableLayout.addView(headerRow);

        // 添加课程数据
        for (String time : times) {
            TableRow row = new TableRow(this);
            addTextViewToRow(row, time); // 第一列为时间

            for (String day : days) {
                if (coursesByDayAndTime.containsKey(day) && coursesByDayAndTime.get(day).containsKey(time)) {
                    Course course = coursesByDayAndTime.get(day).get(time);
                    addTextViewToRow(row, course.getCourseName() + "\n" + course.getPrimaryLocation());
                } else {
                    addTextViewToRow(row, ""); // 空白单元格
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
}