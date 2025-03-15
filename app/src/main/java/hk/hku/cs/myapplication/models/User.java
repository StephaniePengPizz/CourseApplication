package hk.hku.cs.myapplication.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String name; // 用户姓名
    private List<Course> courseList; // 用户的课程列表

    public User(String name, List<Course> courseList) {
        this.name = name;
        this.courseList = courseList;
    }

    public String getName() {
        return name;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", courseList=" + courseList +
                '}';
    }
}