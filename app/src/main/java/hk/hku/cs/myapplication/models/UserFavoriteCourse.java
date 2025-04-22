package hk.hku.cs.myapplication.models;

public class UserFavoriteCourse {
    private int id;
    private int course_id;
    private String course_name;
    private int user_id;

    // Constructor
    public UserFavoriteCourse(int id, int course_id, int user_id) {
        this.id = id;
        this.course_id = course_id;
        this.user_id = user_id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return course_id;
    }

    public void setCourseId(int course_id) {
        this.course_id = course_id;
    }

    public String getCourseName() {
        return course_name;
    }
    public void setCourseName() {
        this.course_name = course_name;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
}