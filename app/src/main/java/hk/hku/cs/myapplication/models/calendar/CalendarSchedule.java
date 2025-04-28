package hk.hku.cs.myapplication.models.calendar;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.Serializable;

public class CalendarSchedule implements Serializable {
    private LocalDate date;
    private LocalTime time;
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

        public String getName() {
            return name();
        }
        public static Priority fromDisplayName(String displayName) {
            for (Priority p : values()) {
                if (p.displayName.equals(displayName)) {
                    return p;
                }
            }
            return MEDIUM; // default value
        }
    }

    public String toJson() {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(stringWriter)) {
            writer.beginObject();
            writer.name("date").value(date.toString());
            writer.name("content").value(content);
            writer.name("priority").value(priority.getName()); // Use enum name instead of display name
            writer.name("category").value(category);
            if (time != null) {
                writer.name("time").value(time.toString());
            }
            writer.endObject();
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CalendarSchedule fromJson(String jsonStr) {
        try (JsonReader reader = new JsonReader(new StringReader(jsonStr))) {
            LocalDate date = null;
            String content = "";
            Priority priority = Priority.MEDIUM;
            String category = "";
            LocalTime time = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "date":
                        date = LocalDate.parse(reader.nextString());
                        break;
                    case "content":
                        content = reader.nextString();
                        break;
                    case "priority":
                        String priorityStr = reader.nextString();
                        try {
                            priority = Priority.valueOf(priorityStr);
                        } catch (IllegalArgumentException e) {
                            priority = Priority.fromDisplayName(priorityStr);
                        }
                        break;
                    case "category":
                        category = reader.nextString();
                        break;
                    case "time":
                        String timeStr = reader.nextString();
                        if (timeStr != null && !timeStr.isEmpty()) {
                            time = LocalTime.parse(timeStr);
                        }
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();

            if (date != null) {
                return new CalendarSchedule(date, time, content, priority, category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CalendarSchedule(LocalDate date, String content) {
        this(date, null, content, Priority.MEDIUM, "");
    }

    public CalendarSchedule(LocalDate date, LocalTime time, String content, Priority priority, String category) {
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