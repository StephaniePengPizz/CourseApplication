package hk.hku.cs.myapplication.activities.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class Schedule {
    private LocalDate date;
    private LocalTime time; // 可以为 null，表示全天日程
    private String content;
    private Priority priority;
    private String category;

    public enum Priority {
        LOW("低优先级"),
        MEDIUM("中优先级"),
        HIGH("高优先级");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public Schedule(LocalDate date, String content) {
        this(date, null, content, Priority.MEDIUM, "");
    }

    public Schedule(LocalDate date, LocalTime time, String content, Priority priority, String category) {
        this.date = date;
        this.time = time;
        this.content = content;
        this.priority = priority;
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (time != null) {
            sb.append(time.toString()).append(" ");
        }
        if (!category.isEmpty()) {
            sb.append("[").append(category).append("] ");
        }
        sb.append(content);
        return sb.toString();
    }
}