package hk.hku.cs.myapplication.models;

import java.util.List;

public class CourseMyListResponse {
    private int code;
    private String msg;
    private Data data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    public List<Course> getCourses() {
        return data != null ? data.items : null;
    }

    public static class Data {
        private List<Course> items;
    }
}