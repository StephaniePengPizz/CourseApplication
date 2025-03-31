package hk.hku.cs.myapplication.models;

// ScheduleRequest.java
public class AddScheduleRequest {
    private int course_id;
    private String schedule_date;
    private String day_of_week;
    private String start_time;
    private String end_time;
    private String location;

    // 构造方法
    public AddScheduleRequest(int courseId, String date, String day,
                           String startTime, String endTime, String location) {
        this.course_id = courseId;
        this.schedule_date = date;
        this.day_of_week = day;
        this.start_time = startTime;
        this.end_time = endTime;
        this.location = location;
    }

    // Getters
    public int getCourseId() { return course_id; }
    public String getScheduleDate() { return schedule_date; }
    public String getDayOfWeek() { return day_of_week; }
    public String getStartTime() { return start_time; }
    public String getEndTime() { return end_time; }
    public String getLocation() { return location; }
}
