package hk.hku.cs.myapplication.models.forum;

public class PostReplyRequest {
    private int forum_id;
    private String content;

    public PostReplyRequest(int forum_id, String content) {
        this.forum_id = forum_id;
        this.content = content;
    }

    public int getForum_id() {
        return forum_id;
    }

    public void setForum_id(int forum_id) {
        this.forum_id = forum_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
