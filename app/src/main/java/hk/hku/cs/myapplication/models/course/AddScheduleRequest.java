package hk.hku.cs.myapplication.models.course;

import android.util.Log;

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
        this.start_time = formatTimeForApi(startTime);
        this.end_time = formatTimeForApi(endTime);
        this.location = location;
    }

    // Getters
    public int getCourseId() { return course_id; }
    public String getScheduleDate() { return schedule_date; }
    public String getDayOfWeek() { return day_of_week; }
    public String getStartTime() { return start_time; }
    public String getEndTime() { return end_time; }
    public String getLocation() { return location; }
    private String formatTimeForApi(String inputTime) {
        try {
            if (inputTime.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                return inputTime + ":00.000Z";  // 转为 "09:00:00.000z"
            }

            if (inputTime.startsWith("T") && inputTime.length() == 9) {
                return inputTime.substring(1) + ".000z";  // "T09:00:00" → "09:00:00.000z"
            }

            throw new IllegalArgumentException("无法识别的时间格式: " + inputTime);
        } catch (Exception e) {
            Log.e("TimeFormat", "格式化失败: " + e.getMessage());
            return "00:00:00.000z";
        }
    }
}
