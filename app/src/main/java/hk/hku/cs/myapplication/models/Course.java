package hk.hku.cs.myapplication.models;

import java.util.List;
import java.util.ArrayList;

public class Course {
    private int id;
    private String course_code;
    private String course_name;
    private String course_description;
    private String semester;
    private int created_by;
    private boolean isFavorite;
    private List<Teacher> course_teachers;
    private List<Schedule> course_schedules;

    // 构造方法
    public Course() {
        this.course_teachers = new ArrayList<>();
        this.course_schedules = new ArrayList<>();
    }

    public Course(String courseCode, String courseName, String description,
                  String semester, int createdBy) {
        this();
        this.course_code = courseCode;
        this.course_name = courseName;
        this.course_description = description;
        this.semester = semester;
        this.created_by = createdBy;

    }

    // 嵌套教师类
    public static class Teacher {
        private int id;
        private String teacher_name;

        // 构造方法
        public Teacher() {}

        public Teacher(int id, String name) {
            this.id = id;
            this.teacher_name = name;
        }

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getTeacherName() { return teacher_name; }
        public void setTeacherName(String teacher_name) { this.teacher_name = teacher_name; }

        @Override
        public String toString() {
            return teacher_name;
        }
    }

    // 嵌套课程表类
    public static class Schedule {
        private int id;
        private String schedule_date;
        private String day_of_week;
        private String start_time;
        private String end_time;
        private String location;

        // 构造方法
        public Schedule() {}

        public Schedule(String day, String start, String end, String location) {
            this.day_of_week = day;
            setStartTime(start);
            setEndTime(end);
            this.location = location;
        }

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getDate() { return schedule_date; }
        public void setDate(String date) { this.schedule_date = date; }

        public String getDayOfWeek() { return day_of_week; }
        public void setDayOfWeek(String day) { this.day_of_week = day; }

        public String getStartTime() {
            return start_time;
        }
        public void setStartTime(String time) {
            this.start_time = time;
        }

        public String getEndTime() {
            return end_time;
        }
        public void setEndTime(String time) {
            this.end_time = time;
        }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getDisplayTime() {
            return String.format("%s %s-%s",
                    getDayOfWeek(), getStartTime(), getEndTime());
        }

        @Override
        public String toString() {
            return getDisplayTime() + " @ " + location;
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourseCode() { return course_code; }
    public void setCourseCode(String code) { this.course_code = code; }

    public String getCourseName() { return course_name; }
    public void setCourseName(String name) { this.course_name = name; }

    public String getCourseDescription() { return course_description; }
    public void setCourseDescription(String description) { this.course_description = description; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getCreatedBy() { return created_by; }
    public void setCreatedBy(int createdBy) { this.created_by = createdBy; }

    public List<Teacher> getTeachers() { return course_teachers; }
    public void setTeachers(List<Teacher> teachers) { this.course_teachers = teachers; }

    public List<Schedule> getSchedules() { return course_schedules; }
    public void setSchedules(List<Schedule> schedules) { this.course_schedules = schedules; }

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public void addTeacher(Teacher teacher) {
        if (course_teachers == null) {
            course_teachers = new ArrayList<>();
        }
        course_teachers.add(teacher);
    }

    public void addSchedule(Schedule schedule) {
        if (course_schedules == null) {
            course_schedules = new ArrayList<>();
        }
        course_schedules.add(schedule);
    }

    public String getPrimaryScheduleTime() {
        if (course_schedules != null && !course_schedules.isEmpty()) {
            return course_schedules.get(0).getDisplayTime();
        }
        return semester != null ? semester : "时间未安排";
    }

    public String getPrimaryLocation() {
        if (course_schedules != null && !course_schedules.isEmpty()) {
            return course_schedules.get(0).getLocation();
        }
        return "地点未定";
    }

    public String getTeachersNames() {
        if (course_teachers == null || course_teachers.isEmpty()) {
            return "教师未分配";
        }
        StringBuilder names = new StringBuilder();
        for (Teacher teacher : course_teachers) {
            names.append(teacher.getTeacherName()).append(", ");
        }
        return names.substring(0, names.length() - 2);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)",
                course_code, course_name, semester);
    }
}