package hk.hku.cs.myapplication.models.course;

public class CourseDetailResponse {
    private int code;
    private String msg;
    private Course data;

    // Getters and setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public Course getData() { return data; }
    public void setData(Course data) { this.data = data; }
}
