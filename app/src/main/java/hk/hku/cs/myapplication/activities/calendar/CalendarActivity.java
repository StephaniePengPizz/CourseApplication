package hk.hku.cs.myapplication.activities.calendar;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import hk.hku.cs.myapplication.models.calendar.CalendarSchedule;
import hk.hku.cs.myapplication.utils.HolidayManager;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
public class CalendarActivity extends AppCompatActivity {
    private LocalDate currentDate;
    private TextView monthLabel;
    private GridLayout calendarGrid;
    private Map<LocalDate, List<CalendarSchedule>> schedules;
    private BottomNavigationView bottomNavigationView;
    private static final String PREFS_NAME = "CalendarPreferences";
    private static final String SCHEDULES_KEY = "saved_schedules";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        schedules = new HashMap<>();
        loadSchedules();
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

        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM")));

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
            LocalDate date = lastMonth.withDayOfMonth(day);
            Button dayButton = createDayButton(String.valueOf(day), date);

            // 设置日期和节日文本
            StringBuilder buttonText = new StringBuilder();
            buttonText.append(day);  // 日期数字

            String holiday = HolidayManager.getHoliday(date);
            if (holiday != null) {
                buttonText.append("\n").append(holiday);  // 节日
            }

            dayButton.setText(buttonText.toString());
            dayButton.setAlpha(0.3f);
            addButtonToGrid(dayButton);
        }

        // 当月日期
        for (int i = 1; i <= monthLength; i++) {
            LocalDate date = currentDate.withDayOfMonth(i);
            Button dayButton = createDayButton(String.valueOf(i), date);

            // 设置日期和节日文本
            StringBuilder buttonText = new StringBuilder();
            buttonText.append(i);  // 日期数字

            String holiday = HolidayManager.getHoliday(date);
            if (holiday != null) {
                buttonText.append("\n").append(holiday);  // 节日
            }

            // 如果有日程，添加日程数量提示
            if (schedules.containsKey(date) && !Objects.requireNonNull(schedules.get(date)).isEmpty()) {
                List<CalendarSchedule> dateSchedules = schedules.get(date);
                CalendarSchedule.Priority highestPriority = getHighestPriority(dateSchedules);

                // 添加日程数量
                buttonText.append("\n(").append(dateSchedules.size()).append(")");

                // 设置背景颜色
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
            }

            dayButton.setText(buttonText.toString());
            dayButton.setOnClickListener(v -> showSchedules(date));
            addButtonToGrid(dayButton);
        }

        // 下月日期
        int totalCells = 42; // 6行7列
        int remainingCells = totalCells - (firstDayOfWeek + monthLength);
        LocalDate nextMonth = firstDay.plusMonths(1);
        for (int i = 1; i <= remainingCells; i++) {
            LocalDate date = nextMonth.withDayOfMonth(i);
            Button dayButton = createDayButton(String.valueOf(i), date);

            // 设置日期和节日文本
            StringBuilder buttonText = new StringBuilder();
            buttonText.append(i);  // 日期数字

            String holiday = HolidayManager.getHoliday(date);
            if (holiday != null) {
                buttonText.append("\n").append(holiday);  // 节日
            }

            dayButton.setText(buttonText.toString());
            dayButton.setAlpha(0.3f);
            addButtonToGrid(dayButton);
        }
    }

    private Button createDayButton(String text, LocalDate date) {
        Button button = new Button(this);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setPadding(2, 2, 2, 2);
        button.setTextSize(11);

        // 使用 LinearLayout 作为按钮的背景
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        button.setLayoutParams(layoutParams);

        // 设置最小高度和宽度
        button.setMinHeight(50);
        button.setMinWidth(50);

        return button;
    }


    private void addButtonToGrid(Button button) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        //params.height = getResources().getDimensionPixelSize(R.dimen.calendar_button_height);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(1, 1, 1, 1); // 减小边距
        button.setLayoutParams(params);
        calendarGrid.addView(button);
    }

    private CalendarSchedule.Priority getHighestPriority(List<CalendarSchedule> dateSchedules) {
        CalendarSchedule.Priority highest = CalendarSchedule.Priority.LOW;
        for (CalendarSchedule schedule : dateSchedules) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)



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
        ArrayAdapter<CalendarSchedule.Priority> priorityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CalendarSchedule.Priority.values());
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
                .setTitle("add arrangement")
                .setPositiveButton("add", (dialog, which) -> {
                    String schedule = scheduleInput.getText().toString().trim();
                    if (!schedule.isEmpty()) {
                        LocalDate scheduleDate = currentDate.withDayOfMonth(dayPicker.getValue());
                        LocalTime scheduleTime = null;
                        if (timeCheckBox.isChecked()) {
                            scheduleTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                        }
                        CalendarSchedule newSchedule = new CalendarSchedule(
                                scheduleDate,
                                scheduleTime,
                                schedule,
                                (CalendarSchedule.Priority) prioritySpinner.getSelectedItem(),
                                categoryInput.getText().toString().trim()
                        );
                        schedules.computeIfAbsent(scheduleDate, k -> new ArrayList<>())
                                .add(newSchedule);
                        saveSchedules();
                        updateCalendar();
                    }
                })
                .setNegativeButton("cancel", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSchedules(LocalDate date) {
        List<CalendarSchedule> dateSchedules = schedules.getOrDefault(date, new ArrayList<>());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (dateSchedules.isEmpty()) {
            builder.setTitle(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd日")))
                    .setMessage("no available arrangement")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        // 创建日程列表视图
        LinearLayout scheduleList = new LinearLayout(this);
        scheduleList.setOrientation(LinearLayout.VERTICAL);
        scheduleList.setPadding(32, 16, 32, 16);

        for (int i = 0; i < dateSchedules.size(); i++) {
            CalendarSchedule schedule = dateSchedules.get(i);
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
            sb.append("\npriority: ").append(schedule.getPriority().toString());

            scheduleView.setText(sb.toString());
            scheduleList.addView(scheduleView);
        }

        builder.setTitle(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "arrangement")
                .setView(scheduleList)
                .setPositiveButton("confirm", null)
                .setNeutralButton("delete arrangement", (dialog, which) -> {
                    showDeleteScheduleDialog(date, dateSchedules);
                })
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDeleteScheduleDialog(LocalDate date, List<CalendarSchedule> dateSchedules) {
        String[] items = new String[dateSchedules.size()];
        for (int i = 0; i < dateSchedules.size(); i++) {
            items[i] = dateSchedules.get(i).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("choose arrangement to be deleted")
                .setItems(items, (dialog, which) -> {
                    dateSchedules.remove(which);
                    if (dateSchedules.isEmpty()) {
                        schedules.remove(date);
                    }
                    saveSchedules();
                    updateCalendar();
                })
                .setNegativeButton("cancel", null)
                .show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSchedules() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            writer.beginObject();
            for (Map.Entry<LocalDate, List<CalendarSchedule>> entry : schedules.entrySet()) {
                writer.name(entry.getKey().toString());
                writer.beginArray();
                for (CalendarSchedule schedule : entry.getValue()) {
                    writer.value(schedule.toJson());
                }
                writer.endArray();
            }
            writer.endObject();
            editor.putString(SCHEDULES_KEY, stringWriter.toString());
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadSchedules() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String schedulesStr = prefs.getString(SCHEDULES_KEY, "{}");

        try (JsonReader reader = new JsonReader(new StringReader(schedulesStr))) {
            reader.beginObject();
            while (reader.hasNext()) {
                try {
                    String dateStr = reader.nextName();
                    LocalDate date = LocalDate.parse(dateStr);
                    reader.beginArray();
                    List<CalendarSchedule> scheduleList = new ArrayList<>();
                    while (reader.hasNext()) {
                        CalendarSchedule schedule = CalendarSchedule.fromJson(reader.nextString());
                        if (schedule != null) {
                            scheduleList.add(schedule);
                        }
                    }
                    reader.endArray();
                    if (!scheduleList.isEmpty()) {
                        schedules.put(date, scheduleList);
                    }
                } catch (Exception e) {
                    // Skip this entry if there's an error
                    e.printStackTrace();
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            // If there's an error loading, clear the preferences to prevent future crashes
            prefs.edit().remove(SCHEDULES_KEY).apply();
        }
    }
}