package hk.hku.cs.myapplication.models.forum;

public class ReplyItem {
    private int id;
    private int forum_id;
    private String content;
    private String created_time;
    private User user;

    public static class User {
        private String username;
        private String email;

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }

    public int getId() {
        return id;
    }

    public int getForum_id() {
        return forum_id;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_time() {
        return created_time;
    }

    public User getUser() {
        return user;
    }
}
