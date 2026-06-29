package com.example.my_custom_calenda_1;

import android.graphics.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Random;

public class SelectSendCalenderModel {
    private int id;
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
    private String createdAt;

    private SelectSendCalenderModel(Builder builder) {
        this.id = builder.id;
        this.isChecked = builder.isChecked;
        this.one = builder.one;
        this.two = builder.two;
        this.three = builder.three;
        this.name = builder.name;
        this.comment = builder.comment;
        this.content = builder.content;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.color = builder.color;
        this.createdAt = builder.createdAt;
    }

    public static class Builder {
        private int id;
        private boolean isChecked;
        private String one, two, three, name, comment, content, createdAt;
        private LocalDate startDate, endDate;
        private int color;

        private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/d"))
                .toFormatter();

        public Builder setId(int id) { this.id = id; return this; }
        public Builder setId(String id) {
            try { this.id = Integer.parseInt(id.trim()); } catch (Exception e) { this.id = 0; }
            return this;
        }
        public Builder setChecked(boolean isChecked) { this.isChecked = isChecked; return this; }
        public Builder setOne(String one) { this.one = one; return this; }
        public Builder setTwo(String two) { this.two = two; return this; }
        public Builder setThree(String three) { this.three = three; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setComment(String comment) { this.comment = comment; return this; }
        public Builder setContent(String content) { this.content = content; return this; }
        public Builder setStartDate(LocalDate date) { this.startDate = date; return this; }
        public Builder setStartDate(String date) {
            try { this.startDate = LocalDate.parse(date.trim(), formatter); } catch (Exception e) { this.startDate = null; }
            return this;
        }
        public Builder setEndDate(LocalDate date) { this.endDate = date; return this; }
        public Builder setEndDate(String date) {
            try { this.endDate = LocalDate.parse(date.trim(), formatter); } catch (Exception e) { this.endDate = null; }
            return this;
        }
        public Builder setColor(int color) { this.color = color; return this; }
        public Builder setCreatedAt(String createdAt) { this.createdAt = createdAt; return this; }

        public SelectSendCalenderModel build() {
            if (this.color == 0) {
                Random random = new Random();
                this.color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            }
            return new SelectSendCalenderModel(this);
        }
    }

    // Getters
    public int getId() { return id; }
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

    public int isWithin(LocalDate date) {
        if (date == null) return 0;
        if (startDate == null && endDate == null) return 0;
        if (startDate != null && endDate == null && date.isEqual(startDate)) return 1;
        if (startDate == null && endDate != null && date.isEqual(endDate)) return 2;
        if (startDate != null && startDate.equals(endDate) && date.isEqual(endDate)) return 3;
        if (startDate != null && endDate != null) {
            if (date.isEqual(startDate)) return 1;
            if (date.isEqual(endDate)) return 2;
            if (date.isAfter(startDate) && date.isBefore(endDate)) return 4;
        }
        return 0;
    }
}
