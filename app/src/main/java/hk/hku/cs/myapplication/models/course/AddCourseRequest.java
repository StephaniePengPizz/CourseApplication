package hk.hku.cs.myapplication.models.course;

public class AddCourseRequest {
    private String course_code;
    private String course_name;
    private String course_description;
    private String semester;

    // 构造方法
    public AddCourseRequest(String code, String name, String description, String semester) {
        this.course_code = code;
        this.course_name = name;
        this.course_description = description;
        this.semester = semester;
    }

    // Getters
    public String getCourseCode() { return course_code; }
    public String getCourseName() { return course_name; }
    public String getCourseDescription() { return course_description; }
    public String getSemester() { return semester; }
}
