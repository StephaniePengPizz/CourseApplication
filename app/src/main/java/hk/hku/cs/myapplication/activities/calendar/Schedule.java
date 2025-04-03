package hk.hku.cs.myapplication.activities.calendar;

import java.time.LocalDate;

public class Schedule {
    private LocalDate date;
    private String content;
    
    public Schedule(LocalDate date, String content) {
        this.date = date;
        this.content = content;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return content;
    }
}