package com.example.my_custom_calenda_1;

import java.time.LocalDate;

/**
 * 1. 코드 구성 및 핵심 구조 설명:
 * 이 클래스는 일정(Event) 정보를 관리하는 순수 Java 데이터 모델입니다.
 * 객체 지향 프로그래밍의 캡슐화 원칙을 따라 일정명, 시작일, 종료일, 색상을 관리합니다.
 */
public class Event {
    private String name;
    // native) java.time.LocalDate: Java 8 이상에서 제공하는 날짜 정보를 다루는 핵심 클래스입니다.
    private LocalDate startDate;
    private LocalDate endDate;
    private int color;

    public Event(String name, LocalDate startDate, LocalDate endDate, int color) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
    }

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

    /**
     * native) LocalDate#isEqual, isAfter, isBefore: 
     * 특정 날짜가 일정 기간 내에 포함되는지 확인하기 위해 사용되는 네이티브 비교 함수들입니다.
     */
    public boolean isWithin(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}
