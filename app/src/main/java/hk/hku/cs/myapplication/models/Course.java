package hk.hku.cs.myapplication.models;
public class Course {
    private String courseName;
    private String courseTime;
    private String courseLocation;
    private String day;

    public Course(String courseName, String courseTime, String courseLocation, String day) {
        this.courseName = courseName;
        this.courseTime = courseTime;
        this.courseLocation = courseLocation;
        this.day = day;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public String getCourseLocation() {
        return courseLocation;
    }

    public String getDay() {
        return day;
    }
}