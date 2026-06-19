package com.example.my_custom_calenda_1;

import android.graphics.Color;

import java.time.LocalDate;
import java.util.Random;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 1. 코드 구성 및 핵심 구조 설명:
 * 이 클래스는 일정(Event) 정보를 관리하는 순수 Java 데이터 모델입니다.
 * 객체 지향 프로그래밍의 캡슐화 원칙을 따라 일정명, 시작일, 종료일, 색상을 관리합니다.
 */
@Entity(tableName="events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    // native) java.time.LocalDate: Java 8 이상에서 제공하는 날짜 정보를 다루는 핵심 클래스입니다.
    private LocalDate startDate;
    private LocalDate endDate;
    private int color;

    @Ignore
    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        Random random = new Random();
        // 0~255 사이의 값을 각각 생성
        this.color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public Event(int id, String name, LocalDate startDate, LocalDate endDate, int color) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getColor() {
        return color;
    }

    public void setColor(int color) { this.color = color; }


    @Override
    public String toString() {
        return "Event{" +
                "이름='" + name + '\'' +
                ", 시작일=" + startDate +
                ", 종료일=" + endDate +
                '}';
    }

    /**
     * 특정 날짜가 일정 범위에 포함되는지 확인합니다.
     * 시작일이나 종료일 중 하나만 있어도 해당 날짜에 표시되도록 개선되었습니다.
     */
    public boolean isWithin(LocalDate date) {
        if (date == null) return false;

        // 시작일과 종료일이 모두 없는 경우 표시 불가
        if (startDate == null && endDate == null) return false;

        // 시작일만 있는 경우: 시작일과 일치하면 표시
        if (startDate != null && endDate == null) {
            return date.isEqual(startDate);
        }

        // 종료일만 있는 경우: 종료일과 일치하면 표시
        if (startDate == null && endDate != null) {
            return date.isEqual(endDate);
        }

        // 시작일과 종료일이 모두 있는 경우: 범위 내에 있는지 확인 (시작일 <= date <= 종료일)
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}
