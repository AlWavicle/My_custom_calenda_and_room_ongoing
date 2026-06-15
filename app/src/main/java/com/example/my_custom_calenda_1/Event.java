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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getColor() {
        return color;
    }



    @Override
    public String toString() {
        return "Event{" +
                "이름='" + name + '\'' +
                ", 시작일=" + startDate +
                '}';
    }
    /**
     * native) LocalDate#isEqual, isAfter, isBefore: 
     * 특정 날짜가 일정 기간 내에 포함되는지 확인하기 위해 사용되는 네이티브 비교 함수들입니다.
     */
    public boolean isWithin(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}
