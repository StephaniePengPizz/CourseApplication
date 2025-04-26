package hk.hku.cs.myapplication.activities.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hk.hku.cs.myapplication.models.course.AddCourseRequest;
import hk.hku.cs.myapplication.models.course.AddScheduleRequest;
import hk.hku.cs.myapplication.models.course.CourseDetailResponse;
import hk.hku.cs.myapplication.models.response.ApiResponse;
import hk.hku.cs.myapplication.models.course.Course;
import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.CourseAdapter;
import hk.hku.cs.myapplication.models.course.CourseListResponse;
import hk.hku.cs.myapplication.models.course.CourseMyListResponse;
import hk.hku.cs.myapplication.models.response.BaseResponse;
import hk.hku.cs.myapplication.network.RetrofitClient;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import hk.hku.cs.myapplication.utils.ForumManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoolCourseActivity extends AppCompatActivity implements
        CourseAdapter.OnFavoriteClickListener,
        CourseAdapter.OnCourseLongClickListener {

    private static final int ITEMS_PER_PAGE = 16;
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();
    private String selectedTime = "";
    private BottomNavigationView bottomNavigationView;

    // Pagination
    private int currentPage = 1;
    private int totalPages = 2;
    private Button prevPageButton;
    private Button nextPageButton;
    private TextView pageInfoText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_course);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // pagination controls
        prevPageButton = findViewById(R.id.prevPageButton);
        nextPageButton = findViewById(R.id.nextPageButton);
        pageInfoText = findViewById(R.id.pageInfoText);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadPoolCoursesFromBackend();
            }
        });
        nextPageButton.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadPoolCoursesFromBackend();
            }
        });

        courseAdapter = new CourseAdapter(courseList);
        courseAdapter.setOnAddCourseClickListener(this::addToMyCoursesToBackend);
        courseAdapter.setOnFavoriteClickListener(this);
        courseAdapter.setOnCourseLongClickListener(this::showCourseDetails);
        recyclerView.setAdapter(courseAdapter);

        findViewById(R.id.addCourseButton).setOnClickListener(v -> showAddCourseDialog());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_pool_course);

        loadPoolCoursesFromBackend();
    }

    private void addToMyCoursesToBackend(int courseId) {
        Call<ApiResponse<Void>> call = RetrofitClient.getInstance().addCourseToUser(courseId);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getCode() == 200) {
                        Toast.makeText(PoolCourseActivity.this,
                                "Course added to your list successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PoolCourseActivity.this,
                                "Failed to add course: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PoolCourseActivity.this,
                            "Failed to add course",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddFavorite(int courseId) {
        Call<ApiResponse<Void>> call = RetrofitClient.getInstance().addCourseToFavorites(courseId);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getCode() == 200) {
                        Toast.makeText(PoolCourseActivity.this, "Course added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PoolCourseActivity.this, "Failed to add to favorites: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PoolCourseActivity.this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveFavorite(int courseId) {
        Call<ApiResponse<Void>> call = RetrofitClient.getInstance().removeCourseFromFavorites(courseId);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getCode() == 200) {
                        Toast.makeText(PoolCourseActivity.this, "Course removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PoolCourseActivity.this, "Failed to remove from favorites: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PoolCourseActivity.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPoolCoursesFromBackend() {
        Call<CourseListResponse> call = RetrofitClient.getInstance().getCourses(null, null, null, currentPage, ITEMS_PER_PAGE);
        call.enqueue(new Callback<CourseListResponse>() {
            @Override
            public void onResponse(Call<CourseListResponse> call, Response<CourseListResponse> response) {
                Log.d("PoolCourseActivity", response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    CourseListResponse courseResponse = response.body();
                    if (courseResponse.getCode() == 200 && courseResponse.getData() != null) {
                        List<Course> courseList = courseResponse.getCourses();
                        updatePaginationUI();
                        courseAdapter.updateCourses(courseList);

                        loadFavoriteCourses();
                        Log.d("API Response", courseResponse.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseListResponse> call, Throwable t) {
                //courseAdapter.updateCourses(courseList);
                Toast.makeText(PoolCourseActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePaginationUI() {
        pageInfoText.setText(String.format(Locale.getDefault(), "Page %d/%d", currentPage, totalPages));
        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
    }

    private void loadFavoriteCourses() {
        Call<CourseMyListResponse> call = RetrofitClient.getInstance().getFavoriteCourses();
        call.enqueue(new Callback<CourseMyListResponse>() {
            @Override
            public void onResponse(Call<CourseMyListResponse> call, Response<CourseMyListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseMyListResponse courseResponse = response.body();
                    List<Course> favoriteCourses = courseResponse.getData();
                    List<Course> currentCourses = courseAdapter.getCourseList();
                    for (Course course : currentCourses) {
                        // Check if the course is in the favoriteCourses list by matching IDs
                        for (Course favoriteCourse : favoriteCourses) {
                            if (course.getId() == favoriteCourse.getId()) {
                                course.setFavorite(true);
                                break;
                            }
                        }
                    }
                    courseAdapter.updateCourses(currentCourses);
                } else {
                    Toast.makeText(PoolCourseActivity.this, "Failed to load favorite courses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseMyListResponse> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    course.setSchedules(schedules);

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
                        Toast.makeText(PoolCourseActivity.this,
                                "Failed to add course: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PoolCourseActivity.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Course>> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCourseScheduleToBackend(int courseId, Course.Schedule schedule) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        AddScheduleRequest scheduleRequest = new AddScheduleRequest(
                courseId,
                currentDate,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLocation()
        );

        Log.d("11", String.valueOf(scheduleRequest.getCourseId()));
        Log.d("ScheduleRequest", "Request: " + scheduleRequest.getScheduleDate() + scheduleRequest.getDayOfWeek() + scheduleRequest.getStartTime() + scheduleRequest.getEndTime() + scheduleRequest.getLocation());

        Call<ApiResponse<Void>> scheduleCall = RetrofitClient.getInstance()
                .addCourseSchedule(scheduleRequest);

        scheduleCall.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.getCode() == 200) {
                        loadPoolCoursesFromBackend();
                        Toast.makeText(PoolCourseActivity.this,
                                "Course added successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PoolCourseActivity.this,
                                "Failed to add schedule: ",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PoolCourseActivity.this,
                            "Failed to add schedule",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCourseLongClick(int courseId) {
        showCourseDetails(courseId);
    }

    private void showCourseDetails(int courseId) {
        Call<CourseDetailResponse> call = RetrofitClient.getInstance().getPoolCourseDetails(courseId);
        call.enqueue(new Callback<CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseDetailResponse> call, Response<CourseDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseDetailResponse detailResponse = response.body();
                    if (detailResponse.getCode() == 200 && detailResponse.getData() != null) {
                        showCourseDetailsDialog(detailResponse.getData());
                    } else {
                        Toast.makeText(PoolCourseActivity.this,
                                "Failed to load details: " + detailResponse.getMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PoolCourseActivity.this,
                            "Failed to load course details",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseDetailResponse> call, Throwable t) {
                Toast.makeText(PoolCourseActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCourseDetailsDialog(Course course) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_course_details, null);

        TextView courseNameText = dialogView.findViewById(R.id.courseNameText);
        TextView courseCodeText = dialogView.findViewById(R.id.courseCodeText);
        TextView semesterText = dialogView.findViewById(R.id.semesterText);
        TextView teachersText = dialogView.findViewById(R.id.teachersText);
        TextView schedulesText = dialogView.findViewById(R.id.schedulesText);

        // Editable schedule components
        EditText startTimeEditText = dialogView.findViewById(R.id.startTimeEditText);
        EditText endTimeEditText = dialogView.findViewById(R.id.endTimeEditText);
        EditText locationEditText = dialogView.findViewById(R.id.courseLocationEditText);
        Spinner daySpinner = dialogView.findViewById(R.id.daySpinner);
        Button saveScheduleButton = dialogView.findViewById(R.id.saveScheduleButton);

        // Set basic info
        courseNameText.setText(course.getCourseName() != null ? course.getCourseName() : "Course name not available");
        courseCodeText.setText(course.getCourseCode() != null ? "Code: " + course.getCourseCode() : "Code not available");
        semesterText.setText(course.getSemester() != null ? "Semester: " + course.getSemester() : "Semester info not available");

        // Set teachers
        if (course.getTeachers() != null && !course.getTeachers().isEmpty()) {
            StringBuilder teachers = new StringBuilder();
            for (Course.Teacher teacher : course.getTeachers()) {
                teachers.append(teacher.getTeacherName()).append("\n");
            }
            teachersText.setText(teachers.toString().trim());
        } else {
            teachersText.setText("No teachers assigned");
        }

        // Set current schedules
        updateSchedulesText(course, schedulesText);

        // Initialize the Spinner with days of the week
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getDaysOfWeek());
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(daysAdapter);

        // Save schedule changes
        saveScheduleButton.setOnClickListener(v -> {
            String dayOfWeek = daySpinner.getSelectedItem().toString();
            String startTime = formatTimeInput(startTimeEditText.getText().toString().trim());
            String endTime = formatTimeInput(endTimeEditText.getText().toString().trim());
            String locationText = locationEditText.getText().toString().trim();

            if (!startTime.isEmpty() && !endTime.isEmpty()) {
                // Create new schedule
                Course.Schedule newSchedule = new Course.Schedule();
                newSchedule.setDayOfWeek(dayOfWeek);
                newSchedule.setStartTime(startTime);
                newSchedule.setEndTime(endTime);
                newSchedule.setLocation(locationText);

                // Add schedule to backend
                addCourseScheduleToBackend(course.getId(), newSchedule);
            } else {
                Toast.makeText(this, "Please fill all schedule fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        new AlertDialog.Builder(this)
                .setTitle("Course Details")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .show();
    }

    private String formatTimeInput(String time) {
        // Format user input time to match backend format (e.g., "09:00" -> "T09:00:00")
        if (time.length() == 5 && time.matches("\\d{2}:\\d{2}")) { // HH:mm format
            return "T" + time + ":00";
        }
        return time;
    }

    private void updateSchedulesText(Course course, TextView schedulesText) {
        if (course.getSchedules() != null && !course.getSchedules().isEmpty()) {
            StringBuilder schedules = new StringBuilder();
            for (Course.Schedule schedule : course.getSchedules()) {
                schedules.append(schedule.getDayOfWeek())
                        .append(" ")
                        .append(formatTimeForDisplay(schedule.getStartTime()))
                        .append(" - ")
                        .append(formatTimeForDisplay(schedule.getEndTime()))
                        .append(" @")
                        .append(schedule.getLocation())
                        .append("\n");
            }
            schedulesText.setText(schedules.toString().trim());
        } else {
            schedulesText.setText("No schedules available");
        }
    }

    private String formatTimeForDisplay(String time) {
        // Convert backend time format (T09:00:00) to display format (09:00)
        if (time != null && time.startsWith("T") && time.length() > 6) {
            return time.substring(1, 6); // Extract HH:mm
        }
        return time;
    }
    private List<String> getDaysOfWeek() {
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        return daysOfWeek;
    }
}