package hk.hku.cs.myapplication.network;

import java.util.List;

import hk.hku.cs.myapplication.models.AddCourseRequest;
import hk.hku.cs.myapplication.models.AddScheduleRequest;
import hk.hku.cs.myapplication.models.ApiResponse;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.CourseListResponse;
import hk.hku.cs.myapplication.models.CourseMyListResponse;
import hk.hku.cs.myapplication.models.ForumItem;
import hk.hku.cs.myapplication.models.LoginResponse;
import hk.hku.cs.myapplication.models.PostForumRequest;
import hk.hku.cs.myapplication.models.RegisterRequest;
import hk.hku.cs.myapplication.models.LoginRequest;
import hk.hku.cs.myapplication.models.User;
import hk.hku.cs.myapplication.models.UserChosenCourse;
import hk.hku.cs.myapplication.models.UserFavoriteCourse;
import hk.hku.cs.myapplication.models.UserInfoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
    @GET("api/v1/main/user/courses")
    Call<CourseMyListResponse> getMyCourses();

    // 添加池子课程
    @POST("/api/v1/main/course")
    Call<ApiResponse<Course>> createCourse(@Body AddCourseRequest courseRequest);

    // 添加课程时间表
    @POST("/api/v1/main/course/schedule")
    Call<ApiResponse<Course.Schedule>> addCourseSchedule(@Body AddScheduleRequest scheduleRequest);

    @POST("/api/v1/main/user/course/{course_id}")
    Call<ApiResponse<Void>> addCourseToUser(@Path("course_id") int courseId);

    @DELETE("/api/v1/main/user/course/{course_id}")
    Call<ApiResponse<Void>> removeCourseForUser(@Path("course_id") int courseId);

    @GET("/api/v1/main/user/me")
    Call<UserInfoResponse> getMyInfo();

    @GET("/api/v1/main/user/favorite")
    Call<CourseMyListResponse> getFavoriteCourses();

    @POST("/api/v1/main/user/favorite/{course_id}")
    Call<ApiResponse<Void>> addCourseToFavorites(@Path("course_id") int courseId);

    @DELETE("/api/v1/main/user/favorite/{course_id}")
    Call<ApiResponse<Void>> removeCourseFromFavorites(@Path("course_id") int courseId);

    @GET("/api/v1/main/sys/all_user_course")
    Call<ApiResponse<List<UserChosenCourse>>> getAllUserCourses();

    @GET("/api/v1/main/sys/all_user_favorite_course")
    Call<ApiResponse<List<UserFavoriteCourse>>> getAllFavoriteCourses();

    @GET("/api/v1/main/course/{course_id}")
    Call<ApiResponse<Course>> getCourseDetails(@Path("course_id") int courseId);
    @GET("/api/v1/main/forum/{course_id}")
    Call<ApiResponse<List<ForumItem>>> getForumMessages(@Path("course_id") int courseId);


    // 发布一条论坛消息
    @POST("/api/v1/main/forum")
    Call<Void> postForumMessage(@Body PostForumRequest request);

}