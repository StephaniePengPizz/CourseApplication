package hk.hku.cs.myapplication.models;

import java.util.List;

public class ApiResponse<T> {
    private int code;       // 响应状态码 (200表示成功)
    private String message; // 响应消息
    private T data;         // 响应数据 (泛型，可以是Course、Schedule等)
    private List<T> courses; // 特定情况下可能有列表数据

    // 构造方法
    public ApiResponse() {}

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getCourses() {
        return courses;
    }

    public void setCourses(List<T> courses) {
        this.courses = courses;
    }

    // 实用方法 - 检查请求是否成功
    public boolean isSuccess() {
        return code == 200;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}