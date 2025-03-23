package hk.hku.cs.myapplication.models;

public class Message {
    private String senderName;
    private String content;
    private long timestamp;

    public Message(String senderName, String content, long timestamp) {
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}