package hk.hku.cs.myapplication.models;

public class PostForumRequest {
    private int course_id;
    private String title;
    private String content;

    public PostForumRequest(int course_id, String title, String content) {
        this.course_id = course_id;
        this.title = title;
        this.content = content;
    }

    // Getter & Setter 可自动生成
}