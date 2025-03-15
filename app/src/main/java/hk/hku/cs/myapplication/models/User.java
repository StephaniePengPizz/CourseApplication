package hk.hku.cs.myapplication.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String name;
    private List<Course> courseList;

    public User(String name, List<Course> courseList) {
        this.name = name;
        this.courseList = courseList;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", courseList=" + courseList +
                '}';
    }
}
//123