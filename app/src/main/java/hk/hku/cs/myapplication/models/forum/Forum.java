package hk.hku.cs.myapplication.models.forum;

import java.util.ArrayList;
import java.util.List;

public class Forum {
    private String forumId; // courseName 即 forumId
    private List<Message> messages;

    public Forum(String forumId) {
        this.forumId = forumId;
        this.messages = new ArrayList<>();
    }

    public String getForumId() {
        return forumId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void postMessage(Message message) {
        messages.add(message);
    }
}