package hk.hku.cs.myapplication.network;

import java.util.List;

import hk.hku.cs.myapplication.models.AddCourseRequest;
import hk.hku.cs.myapplication.models.AddScheduleRequest;
import hk.hku.cs.myapplication.models.ApiResponse;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.CourseListResponse;
import hk.hku.cs.myapplication.models.CourseMyListResponse;
import hk.hku.cs.myapplication.models.LoginResponse;
import hk.hku.cs.myapplication.models.RegisterRequest;
import hk.hku.cs.myapplication.models.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/v1/auth/register")
    Call<Void> register(@Body RegisterRequest request);
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @GET("api/v1/main/courses")
    Call<CourseListResponse> getCourses(
            @Query("course_code") String courseCode,
            @Query("course_name") String courseName,
            @Query("semester") String semester,
            @Query("page") int page,
            @Query("size") int size
    );
    @GET("api/v1/main/courses/me")
    Call<CourseMyListResponse> getMyCourses();

    // 添加课程基本信息
    @POST("/api/v1/main/course")
    Call<ApiResponse<Course>> createCourse(@Body AddCourseRequest courseRequest);

    // 添加课程时间表
    @POST("/api/v1/main/course/schedule")
    Call<ApiResponse<Course.Schedule>> addCourseSchedule(@Body AddScheduleRequest scheduleRequest);
}