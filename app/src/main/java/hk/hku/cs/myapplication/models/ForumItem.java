package hk.hku.cs.myapplication.models;

public class ForumItem {
    private int id;
    private int course_id;
    private String title;
    private String content;
    private int created_by;
    private Creator creator;

    public static class Creator {
        private String username;
        private String email;

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public Creator getCreator() {
        return creator;
    }
}