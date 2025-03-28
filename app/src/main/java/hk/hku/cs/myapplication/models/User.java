package hk.hku.cs.myapplication.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String name;
    private List<Course> courseList;
    private String email;
    private int Id;

    public User(String name, List<Course> courseList) {
        this.name = name;
        this.courseList = courseList;
        this.email = email;
        this.Id = Id;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public int getId() {
        return Id;
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