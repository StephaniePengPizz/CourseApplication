package hk.hku.cs.myapplication.activities.calendar;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.time.LocalDate;
import java.time.LocalTime;
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
        currentDate = LocalDate.now();

        monthLabel = findViewById(R.id.monthLabel);
        calendarGrid = findViewById(R.id.calendarGrid);
        calendarGrid.setColumnCount(7);
        calendarGrid.setRowCount(7);

        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button addScheduleButton = findViewById(R.id.addScheduleButton);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_calendar);

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

        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("yyyy年 MM月")));

        String[] weekdays = {"日", "一", "二", "三", "四", "五", "六"};
        for (String weekday : weekdays) {
            TextView label = createTextView(weekday);
            addViewToGrid(label);
        }

        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int monthLength = currentDate.lengthOfMonth();
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;

        // 上月日期
        LocalDate lastMonth = firstDay.minusMonths(1);
        int lastMonthDays = lastMonth.lengthOfMonth();
        for (int i = 0; i < firstDayOfWeek; i++) {
            int day = lastMonthDays - firstDayOfWeek + i + 1;
            Button dayButton = createDayButton(String.valueOf(day));
            dayButton.setAlpha(0.3f);
            addButtonToGrid(dayButton);
        }

        // 当月日期
        for (int i = 1; i <= monthLength; i++) {
            LocalDate date = currentDate.withDayOfMonth(i);
            Button dayButton = createDayButton(String.valueOf(i));

            if (schedules.containsKey(date) && !Objects.requireNonNull(schedules.get(date)).isEmpty()) {
                List<Schedule> dateSchedules = schedules.get(date);
                Schedule.Priority highestPriority = getHighestPriority(dateSchedules);
                int color;
                switch (highestPriority) {
                    case HIGH:
                        color = getResources().getColor(android.R.color.holo_red_light);
                        break;
                    case MEDIUM:
                        color = getResources().getColor(android.R.color.holo_orange_light);
                        break;
                    default:
                        color = getResources().getColor(android.R.color.holo_green_light);
                }
                dayButton.setBackgroundColor(color);
                dayButton.setText(String.format("%d\n(%d)", i, dateSchedules.size()));
            }

            dayButton.setOnClickListener(v -> showSchedules(date));
            addButtonToGrid(dayButton);
        }

        // 下月日期
        int remainingCells = 42 - (firstDayOfWeek + monthLength);
        for (int i = 1; i <= remainingCells; i++) {
            Button dayButton = createDayButton(String.valueOf(i));
            dayButton.setAlpha(0.3f);
            addButtonToGrid(dayButton);
        }
    }

    private Schedule.Priority getHighestPriority(List<Schedule> dateSchedules) {
        Schedule.Priority highest = Schedule.Priority.LOW;
        for (Schedule schedule : dateSchedules) {
            if (schedule.getPriority().ordinal() > highest.ordinal()) {
                highest = schedule.getPriority();
            }
        }
        return highest;
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

    private void addViewToGrid(View view) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        view.setLayoutParams(params);
        calendarGrid.addView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);

        NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
        EditText scheduleInput = dialogView.findViewById(R.id.scheduleInput);
        EditText categoryInput = dialogView.findViewById(R.id.categoryInput);
        Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
        CheckBox timeCheckBox = dialogView.findViewById(R.id.timeCheckBox);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        // 设置优先级下拉框
        ArrayAdapter<Schedule.Priority> priorityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Schedule.Priority.values());
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);
        prioritySpinner.setSelection(1); // 默认中优先级

        // 设置日期选择器
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(currentDate.lengthOfMonth());
        dayPicker.setValue(currentDate.getDayOfMonth());

        // 时间选择器默认隐藏
        timePicker.setVisibility(View.GONE);
        timeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                timePicker.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        builder.setView(dialogView)
                .setTitle("添加日程")
                .setPositiveButton("添加", (dialog, which) -> {
                    String schedule = scheduleInput.getText().toString().trim();
                    if (!schedule.isEmpty()) {
                        LocalDate scheduleDate = currentDate.withDayOfMonth(dayPicker.getValue());
                        LocalTime scheduleTime = null;
                        if (timeCheckBox.isChecked()) {
                            scheduleTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                        }
                        Schedule newSchedule = new Schedule(
                                scheduleDate,
                                scheduleTime,
                                schedule,
                                (Schedule.Priority) prioritySpinner.getSelectedItem(),
                                categoryInput.getText().toString().trim()
                        );
                        schedules.computeIfAbsent(scheduleDate, k -> new ArrayList<>())
                                .add(newSchedule);
                        updateCalendar();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSchedules(LocalDate date) {
        List<Schedule> dateSchedules = schedules.getOrDefault(date, new ArrayList<>());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (dateSchedules.isEmpty()) {
            builder.setTitle(date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")))
                    .setMessage("暂无日程")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        // 创建日程列表视图
        LinearLayout scheduleList = new LinearLayout(this);
        scheduleList.setOrientation(LinearLayout.VERTICAL);
        scheduleList.setPadding(32, 16, 32, 16);

        for (int i = 0; i < dateSchedules.size(); i++) {
            Schedule schedule = dateSchedules.get(i);
            TextView scheduleView = new TextView(this);
            scheduleView.setPadding(0, 8, 0, 8);

            StringBuilder sb = new StringBuilder();
            sb.append(i + 1).append(". ");
            if (schedule.getTime() != null) {
                sb.append(schedule.getTime().format(DateTimeFormatter.ofPattern("HH:mm"))).append(" ");
            }
            if (!schedule.getCategory().isEmpty()) {
                sb.append("[").append(schedule.getCategory()).append("] ");
            }
            sb.append(schedule.getContent());
            sb.append("\n优先级: ").append(schedule.getPriority().toString());

            scheduleView.setText(sb.toString());
            scheduleList.addView(scheduleView);
        }

        builder.setTitle(date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + "的日程")
                .setView(scheduleList)
                .setPositiveButton("确定", null)
                .setNeutralButton("删除日程", (dialog, which) -> {
                    showDeleteScheduleDialog(date, dateSchedules);
                })
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDeleteScheduleDialog(LocalDate date, List<Schedule> dateSchedules) {
        String[] items = new String[dateSchedules.size()];
        for (int i = 0; i < dateSchedules.size(); i++) {
            items[i] = dateSchedules.get(i).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择要删除的日程")
                .setItems(items, (dialog, which) -> {
                    dateSchedules.remove(which);
                    if (dateSchedules.isEmpty()) {
                        schedules.remove(date);
                    }
                    updateCalendar();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}