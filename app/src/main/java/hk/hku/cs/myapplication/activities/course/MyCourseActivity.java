package hk.hku.cs.myapplication.activities.course;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.MyCourseAdapter;
import hk.hku.cs.myapplication.models.ApiResponse;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.CourseMyListResponse;
import hk.hku.cs.myapplication.network.RetrofitClient;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCourseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button switchButton;
    private MyCourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        switchButton = findViewById(R.id.switchButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 设置适配器
        courseAdapter = new MyCourseAdapter(courseList);
        courseAdapter.setOnRemoveCourseClickListener(this::RemoveCourseFromBackend);
        recyclerView.setAdapter(courseAdapter);

        loadMyCoursesFromBackend();

        // 切换按钮点击事件
        switchButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyCourseActivity.this, TableActivity.class);
            startActivity(intent);
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_my_course);
    }

    private void loadMyCoursesFromBackend() {
        Call<CourseMyListResponse> call = RetrofitClient.getInstance().getMyCourses();
        call.enqueue(new Callback<CourseMyListResponse>() {
            @Override
            public void onResponse(Call<CourseMyListResponse> call, Response<CourseMyListResponse> response) {
                Log.d("API_RESPONSE", "Response: " + response);

                if (response.isSuccessful()) {
                    CourseMyListResponse courseResponse = response.body();
                    Log.d("API_RESPONSE", "Parsed: " + courseResponse);

                    if (courseResponse != null) {
                        if (courseResponse.getCode() == 200) {
                            List<Course> courses = courseResponse.getData();
                            if (courses != null && !courses.isEmpty()) {
                                courseAdapter.updateCourses(courses);
                            } else {
                                showEmptyState();
                            }
                        } else {
                            Toast.makeText(MyCourseActivity.this,
                                    "Error: " + courseResponse.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    try {
                        String error = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e("API_ERROR", "Server error: " + error);
                        Toast.makeText(MyCourseActivity.this,
                                "Server error: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseMyListResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Network error", t);
                Toast.makeText(MyCourseActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void RemoveCourseFromBackend(Course course) {
        int courseId = course.getId();
        Call<ApiResponse<Void>> call = RetrofitClient.getInstance()
                .removeCourseForUser(courseId);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getCode() == 200) {
                        Toast.makeText(MyCourseActivity.this,
                                "Course removed successfully",
                                Toast.LENGTH_SHORT).show();
                        loadMyCoursesFromBackend();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(MyCourseActivity.this,
                        "Failed to remove course",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        Toast.makeText(this, "No courses available", Toast.LENGTH_SHORT).show();
    }
}

