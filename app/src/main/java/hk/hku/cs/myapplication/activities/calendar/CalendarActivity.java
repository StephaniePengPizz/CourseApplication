package hk.hku.cs.myapplication.activities.calendar;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class CalendarActivity extends AppCompatActivity {
    private LocalDate currentDate;
    private TextView monthLabel;
    private GridLayout calendarGrid;
    private Map<LocalDate, List<Schedule>> schedules;
    private BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        schedules = new HashMap<>();
        // 使用提供的当前时间初始化
        currentDate = LocalDate.parse("2025-04-03");

        // 初始化视图并设置 GridLayout 的列数和行数
        monthLabel = findViewById(R.id.monthLabel);
        calendarGrid = findViewById(R.id.calendarGrid);
        calendarGrid.setColumnCount(7); // 设置7列
        calendarGrid.setRowCount(7); // 设置7行（1行表头 + 最多6行日期）

        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button addScheduleButton = findViewById(R.id.addScheduleButton);

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));

        // 设置当前选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_calendar);

        // 设置点击事件
        prevButton.setOnClickListener(v -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        addScheduleButton.setOnClickListener(v -> showAddScheduleDialog());

        updateCalendar();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCalendar() {
        calendarGrid.removeAllViews();

        // 设置月份标签
        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("yyyy年 MM月")));

        // 添加星期标签
        String[] weekdays = {"日", "一", "二", "三", "四", "五", "六"};
        for (String weekday : weekdays) {
            TextView label = createTextView(weekday);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            label.setLayoutParams(params);
            calendarGrid.addView(label);
        }

        // 获取当月第一天
        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int monthLength = currentDate.lengthOfMonth();

        // 获取第一天是星期几 (0 = 星期日, 1 = 星期一, ..., 6 = 星期六)
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;

        // 获取上个月的天数
        LocalDate lastMonth = firstDay.minusMonths(1);
        int lastMonthDays = lastMonth.lengthOfMonth();

        // 添加上个月的日期（灰色显示）
        for (int i = 0; i < firstDayOfWeek; i++) {
            int day = lastMonthDays - firstDayOfWeek + i + 1;
            Button dayButton = createDayButton(String.valueOf(day));
            dayButton.setAlpha(0.3f);
            addButtonToGrid(dayButton);
        }

        // 添加当月日期
        for (int i = 1; i <= monthLength; i++) {
            LocalDate date = currentDate.withDayOfMonth(i);
            Button dayButton = createDayButton(String.valueOf(i));

            // 如果有日程，设置背景颜色
            if (schedules.containsKey(date) && !Objects.requireNonNull(schedules.get(date)).isEmpty()) {
                dayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            }

            dayButton.setOnClickListener(v -> showSchedules(date));
            addButtonToGrid(dayButton);
        }

        // 添加下个月的日期（灰色显示）
        int remainingCells = 42 - (firstDayOfWeek + monthLength); // 6 行 * 7 列 = 42 个格子
        for (int i = 1; i <= remainingCells; i++) {
            Button dayButton = createDayButton(String.valueOf(i));
            dayButton.setAlpha(0.3f);
            addButtonToGrid(dayButton);
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private Button createDayButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setPadding(4, 4, 4, 4);
        return button;
    }

    private void addButtonToGrid(Button button) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        button.setLayoutParams(params);
        calendarGrid.addView(button);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);

        NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
        EditText scheduleInput = dialogView.findViewById(R.id.scheduleInput);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(currentDate.lengthOfMonth());
        dayPicker.setValue(currentDate.getDayOfMonth());

        builder.setView(dialogView)
                .setTitle("添加日程")
                .setPositiveButton("添加", (dialog, which) -> {
                    String schedule = scheduleInput.getText().toString().trim();
                    if (!schedule.isEmpty()) {
                        LocalDate scheduleDate = currentDate.withDayOfMonth(dayPicker.getValue());
                        schedules.computeIfAbsent(scheduleDate, k -> new ArrayList<>())
                                .add(new Schedule(scheduleDate, schedule));
                        updateCalendar();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSchedules(LocalDate date) {
        List<Schedule> dateSchedules = schedules.getOrDefault(date, new ArrayList<>());
        StringBuilder sb = new StringBuilder();
        sb.append(date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("的日程：\n\n");

        assert dateSchedules != null;
        if (dateSchedules.isEmpty()) {
            sb.append("暂无日程");
        } else {
            for (int i = 0; i < dateSchedules.size(); i++) {
                sb.append(i + 1).append(". ").append(dateSchedules.get(i).getContent()).append("\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("日程列表")
                .setMessage(sb.toString())
                .setPositiveButton("确定", null)
                .show();
    }
}