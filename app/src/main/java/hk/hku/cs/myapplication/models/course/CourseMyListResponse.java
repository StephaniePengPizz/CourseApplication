package hk.hku.cs.myapplication.models.course;

import java.util.List;

public class CourseMyListResponse {
    private int code;
    private String msg;
    private List<Course> data;

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    public List<Course> getData() {
        return data;
    }

}