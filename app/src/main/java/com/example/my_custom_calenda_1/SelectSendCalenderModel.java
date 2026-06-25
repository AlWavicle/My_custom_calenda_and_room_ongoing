package com.example.my_custom_calenda_1;

import android.graphics.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Random;

public class SelectSendCalenderModel {

    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int color;
    private String startTime;
    private String endTime;
    private String memo;
    private boolean isAllDay;
    private String category1;
    private String category2;
    private String createdAt;

    // 생성자를 private으로 만들어서 직접 호출을 막음
    private SelectSendCalenderModel(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.color = builder.color;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.memo = builder.memo;
        this.isAllDay = builder.isAllDay;
        this.category1 = builder.category1;
        this.category2 = builder.category2;
        this.createdAt = builder.createdAt;
    }

    public static class Builder {
        private int id;
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private int color;
        private String startTime;
        private String endTime;
        private String memo;
        private boolean isAllDay;
        private String category1;
        private String category2;
        private String createdAt;

        //스트링을 로컬데이트로 바꾸기 위한 포맷 생성
        private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/d"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년MM월dd일"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년M월d일"))
                .toFormatter();

        public Builder setId(String id) {
            if (id != null && !id.isEmpty()) {
                this.id = Integer.parseInt(id.trim());
            } else {
                this.id = 0;
            }
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            if (name != null && !name.trim().isEmpty()) {
                this.name = name;
            }
            return this;
        }

        public Builder setStartDate(String startDate) {
            if (startDate != null && !startDate.trim().isEmpty()) {
                this.startDate = LocalDate.parse(startDate.trim(), formatter);
            }else {
                this.startDate=null;
            }
            return this;
        }

        public Builder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            if (endDate != null && !endDate.trim().isEmpty()) {
                this.endDate = LocalDate.parse(endDate.trim(), formatter);
            }else {
                this.endDate=null;
            }
            return this;
        }

        public Builder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setMemo(String memo) {
            this.memo = memo;
            return this;
        }

        public Builder setIsAllDay(boolean isAllDay) {
            this.isAllDay = isAllDay;
            return this;
        }

        public Builder setCategory1(String category1) {
            this.category1 = category1;
            return this;
        }

        public Builder setCategory2(String category2) {
            this.category2 = category2;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SelectSendCalenderModel build() {
            // 컬러값이 지정 안 됐으면 랜덤 처리
            if (this.color == 0) {
                Random random = new Random();
                this.color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            }
            return new SelectSendCalenderModel(this);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public boolean isAllDay() { return isAllDay; }
    public void setAllDay(boolean allDay) { isAllDay = allDay; }

    public String getCategory1() { return category1; }
    public void setCategory1(String category1) { this.category1 = category1; }

    public String getCategory2() { return category2; }
    public void setCategory2(String category2) { this.category2 = category2; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "SelectSendCalenderModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", color=" + color +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", memo='" + memo + '\'' +
                ", isAllDay=" + isAllDay +
                ", category1='" + category1 + '\'' +
                ", category2='" + category2 + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    /**
     * 특정 날짜가 일정 범위에 포함되는지 확인합니다.
     * 시작일이나 종료일 중 하나만 있어도 해당 날짜에 표시되도록 개선되었습니다.
     */
    public int isWithin(LocalDate date) {
        if (date == null) return 0;

        // 시작일과 종료일이 모두 없는 경우 표시 불가
        if (startDate == null && endDate == null) return 0;

        // 시작일만 있는 경우: 시작일과 일치하면 표시
        if (startDate != null && endDate == null&&date.isEqual(startDate)) {
            return 1;
        }

        // 종료일만 있는 경우: 종료일과 일치하면 표시
        if (startDate == null && endDate != null&&date.isEqual(endDate)) {
            return 2;
        }

        // 시작일=종료일인 경우
        if (startDate != null && startDate.equals(endDate) && date.isEqual(endDate)) {
            return 3;
        }

        // 시작일과 종료일이 모두 있는 경우: 범위 구분
        if (startDate != null && endDate != null) {
            if (date.isEqual(startDate)) return 1;
            if (date.isEqual(endDate)) return 2;
            if (date.isAfter(startDate) && date.isBefore(endDate)) {
                return 4;
            }
        }

        return 0;
    }
}
