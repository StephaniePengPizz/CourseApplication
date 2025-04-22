package hk.hku.cs.myapplication.activities.match;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.MatchAdapter;
import hk.hku.cs.myapplication.models.ApiResponse;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.User;
import hk.hku.cs.myapplication.models.UserChosenCourse;
import hk.hku.cs.myapplication.models.UserFavoriteCourse;
import hk.hku.cs.myapplication.models.UserInfoResponse;
import hk.hku.cs.myapplication.network.RetrofitClient;
import hk.hku.cs.myapplication.utils.NavigationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
    private List<User> matchedUsers = new ArrayList<>();
    private User currentUser;
    private List<UserChosenCourse> allUserCourses = new ArrayList<>();
    private List<UserFavoriteCourse> allFavoriteCourses = new ArrayList<>();
    private Map<Integer, User> userMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // 初始化UI
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        matchAdapter = new MatchAdapter(matchedUsers);
        recyclerView.setAdapter(matchAdapter);

        Button matchButton = findViewById(R.id.matchButton);
        matchButton.setOnClickListener(v -> loadDataAndMatch());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_match);

    }

    private void loadDataAndMatch() {
        // Show loading state
        Toast.makeText(this, "Loading data...", Toast.LENGTH_SHORT).show();

        // First fetch all user courses
        RetrofitClient.getInstance().getAllUserCourses().enqueue(new Callback<ApiResponse<List<UserChosenCourse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserChosenCourse>>> call,
                                   Response<ApiResponse<List<UserChosenCourse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUserCourses = response.body().getData();
                    // Then fetch all favorite courses
                    fetchFavoriteCourses();
                } else {
                    Toast.makeText(MatchActivity.this,
                            "Failed to load user courses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserChosenCourse>>> call, Throwable t) {
                Toast.makeText(MatchActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFavoriteCourses() {
        RetrofitClient.getInstance().getAllFavoriteCourses().enqueue(
                new Callback<ApiResponse<List<UserFavoriteCourse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<UserFavoriteCourse>>> call,
                                           Response<ApiResponse<List<UserFavoriteCourse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allFavoriteCourses = response.body().getData();
                            // Process the data and perform matching
                            processUserDataAndMatch();
                        } else {
                            Toast.makeText(MatchActivity.this,
                                    "Failed to load favorite courses", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<UserFavoriteCourse>>> call, Throwable t) {
                        Toast.makeText(MatchActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processUserDataAndMatch() {
        userMap.clear();
        Set<Integer> allCourseIds = new HashSet<>();
        // Process enrolled courses with null checks
        if (allUserCourses != null) {
            for (UserChosenCourse userCourse : allUserCourses) {
                if (userCourse == null) continue;
                allCourseIds.add(userCourse.getCourseId());

                User user = userMap.get(userCourse.getUserId());
                if (user == null) {
                    user = new User(userCourse.getUserId(), "User " + userCourse.getUserId());
                    userMap.put(userCourse.getUserId(), user);
                }

                if (userCourse.getCourseId() > 0) { // Validate course ID
                    Course course = new Course();
                    course.setId(userCourse.getCourseId());
                    course.setCourseName(userCourse.getCourseName());
                    user.addEnrolledCourse(course);
                }
            }
        }

        // Process favorite courses with null checks
        if (allFavoriteCourses != null) {
            for (UserFavoriteCourse favCourse : allFavoriteCourses) {
                if (favCourse == null) continue;
                allCourseIds.add(favCourse.getCourseId());
                User user = userMap.get(favCourse.getUserId());
                if (user != null && favCourse.getCourseId() > 0) {
                    Course course = new Course();
                    course.setId(favCourse.getCourseId());
                    course.setCourseName(favCourse.getCourseName());
                    user.addFavoriteCourse(course);
                }
            }
        }
        fetchCourseDetails(new ArrayList<>(allCourseIds), new CourseDetailsCallback() {
            @Override
            public void onCourseDetailsReceived(Course detailedCourse) {
                // 更新所有用户的课程名称
                for (User user : userMap.values()) {
                    for (Course course : user.getEnrolledCourses()) {
                        if (course.getId() == detailedCourse.getId()) {
                            course.setCourseName(detailedCourse.getCourseName());
                        }
                    }
                    for (Course course : user.getFavoriteCourses()) {
                        if (course.getId() == detailedCourse.getId()) {
                            course.setCourseName(detailedCourse.getCourseName());
                        }
                    }
                }
            }

            @Override
            public void onAllCoursesProcessed() {
                // Now that all course details are loaded, proceed with matching
                // Get current user safely
                getCurrentUserId(new UserIdCallback() {
                    @Override
                    public void onUserIdReceived(int currentUserId) {
                        if (currentUserId <= 0) {
                            Toast.makeText(MatchActivity.this, "Invalid current user ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        currentUser = userMap.get(currentUserId);
                        if (currentUser == null) {
                            Toast.makeText(MatchActivity.this, "Current user data not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Prepare and sort matched users
                        matchedUsers.clear();
                        for (User user : userMap.values()) {
                            if (user != null && user.getId() != currentUserId) {
                                matchedUsers.add(user);
                            }
                        }

                        matchedUsers.sort((u1, u2) -> {
                            int score1 = currentUser.calculateMatchScore(u1);
                            int score2 = currentUser.calculateMatchScore(u2);
                            return Integer.compare(score2, score1);
                        });

                        matchAdapter.updateUsers(matchedUsers, currentUser);
                    }
                });
            }
        });


    }


    private void getCurrentUserId(UserIdCallback callback) {
        Call<UserInfoResponse> call = RetrofitClient.getInstance().getMyInfo();
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfoResponse userResponse = response.body();
                    if (userResponse.getCode() == 200 && userResponse.getData() != null) {
                        callback.onUserIdReceived(userResponse.getData().getId());
                        return;
                    }
                }
                callback.onUserIdReceived(-1); // Invalid ID
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Toast.makeText(MatchActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onUserIdReceived(-1); // Invalid ID
            }
        });
    }

    interface UserIdCallback {
        void onUserIdReceived(int userId);
    }
    private void fetchCourseDetails(List<Integer> courseIds, CourseDetailsCallback callback) {
        Set<Integer> uniqueCourseIds = new HashSet<>(courseIds);
        if (uniqueCourseIds.isEmpty()) {
            callback.onAllCoursesProcessed();
            return;
        }

        final AtomicInteger remainingCourses = new AtomicInteger(uniqueCourseIds.size());

        for (int courseId : uniqueCourseIds) {
            RetrofitClient.getInstance().getCourseDetails(courseId).enqueue(
                    new Callback<ApiResponse<Course>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Course>> call, Response<ApiResponse<Course>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Course course = response.body().getData();
                                callback.onCourseDetailsReceived(course);
                                Log.d("MatchActivity", "course detail gotten: " + course.getCourseName());
                            }

                            if (remainingCourses.decrementAndGet() == 0) {
                                callback.onAllCoursesProcessed();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Course>> call, Throwable t) {
                            Log.e("MatchActivity", "Failed to fetch course details: " + t.getMessage());
                            if (remainingCourses.decrementAndGet() == 0) {
                                callback.onAllCoursesProcessed();
                            }
                        }
                    });
        }
    }

    interface CourseDetailsCallback {
        void onCourseDetailsReceived(Course course);
        void onAllCoursesProcessed();
    }

}