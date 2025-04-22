package hk.hku.cs.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String name;
    private List<Course> courseList;
    private String email;
    private String userString;
    private int Id;
    private int commonCoursesCount;
    private List<Course> enrolledCourses;  // 已选课程
    private List<Course> favoriteCourses;  // 收藏课程

    public User(String name, List<Course> courseList) {
        this.name = name;
        this.courseList = courseList;
        this.email = email;
        this.Id = Id;
        this.commonCoursesCount = commonCoursesCount;
        this.enrolledCourses = new ArrayList<>();
        this.favoriteCourses = new ArrayList<>();
    }

    public User(int Id, String userString) {
        this.userString = userString;
        this.Id = Id;
        this.enrolledCourses = new ArrayList<>();
        this.favoriteCourses = new ArrayList<>();
    }

    public int getCommonCoursesCount() {
        return commonCoursesCount;
    }
    public String getName() {
        return name;
    }
    public String getNameinMatch() {
        return userString;
    }
    public List<Course> getCourses() {
        return courseList;
    }
    public String getEmail() {
        return email;
    }
    public int getId() {
        return Id;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", courseList=" + courseList +
                '}';
    }
    public void addEnrolledCourse(Course course) {
        if (course != null && !enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
        }
    }

    public void addFavoriteCourse(Course course) {
        if (course != null && !favoriteCourses.contains(course)) {
            favoriteCourses.add(course);
        }
    }
    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    public List<Course> getFavoriteCourses() {
        return favoriteCourses;
    }

    public int calculateMatchScore(User otherUser) {
        int score = 0;

        // Match based on enrolled courses
        if (this.enrolledCourses != null && otherUser.enrolledCourses != null) {
            for (Course course : enrolledCourses) {
                if (otherUser.getEnrolledCourses().contains(course)) {
                    score += 5; // Higher weight for enrolled courses
                }
            }
        }

        // Match based on favorite courses
        if (this.favoriteCourses != null && otherUser.favoriteCourses != null) {
            for (Course course : favoriteCourses) {
                if (otherUser.getFavoriteCourses().contains(course)) {
                    score += 3; // Slightly lower weight for favorite courses
                }
            }
        }

        return score;
    }
}
//123