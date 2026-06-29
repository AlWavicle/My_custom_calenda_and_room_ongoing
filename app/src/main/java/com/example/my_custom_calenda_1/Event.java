package com.example.my_custom_calenda_1;

import android.graphics.Color;
import java.time.LocalDate;
import java.util.Random;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName="events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(defaultValue = "0")
    private boolean isChecked;
    
    private String one;
    private String two;
    private String three;
    private String name;
    private String comment;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private int color;

    @ColumnInfo(defaultValue = "CURRENT_DATE")
    private String createdAt;

    @Ignore
    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        Random random = new Random();
        this.color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        this.createdAt = LocalDate.now().toString();
        this.isChecked = false;
        this.one = "";
        this.two = "";
        this.three = "";
        this.comment = "";
        this.content = "";
    }

    public Event(int id, boolean isChecked, String one, String two, String three, String name, 
                 String comment, String content, LocalDate startDate, LocalDate endDate, 
                 int color, String createdAt) {
        this.id = id;
        this.isChecked = isChecked;
        this.one = one;
        this.two = two;
        this.three = three;
        this.name = name;
        this.comment = comment;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
    public String getOne() { return one; }
    public void setOne(String one) { this.one = one; }
    public String getTwo() { return two; }
    public void setTwo(String two) { this.two = two; }
    public String getThree() { return three; }
    public void setThree(String three) { this.three = three; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isWithin(LocalDate date) {
        if (date == null) return false;
        if (startDate == null && endDate == null) return false;
        if (startDate != null && endDate == null) return date.isEqual(startDate);
        if (startDate == null && endDate != null) return date.isEqual(endDate);
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}
