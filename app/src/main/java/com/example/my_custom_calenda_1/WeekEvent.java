package com.example.my_custom_calenda_1;

public class WeekEvent {
    public int startDayIndex; // 0(일요일) ~ 6(토요일)
    public int endDayIndex;   // 0(일요일) ~ 6(토요일)
    public int color;         // 일정 막대의 색상
    public String title;      // 일정 제목 (옵션)
    public int id;            // 🚀 원본 일정 ID
    public int lane = 0;      // 🚀 세로 위치 (겹치지 않게 쌓기 위한 레인 번호)
    public int type = 0;      // 🚀 모양 타입: 0(막대), 1(시작만-왼쪽삼각형), 2(종료만-오른쪽삼각형), 3(당일-동그라미)

    public WeekEvent(int startDayIndex, int endDayIndex, int color, String title) {
        this.startDayIndex = startDayIndex;
        this.endDayIndex = endDayIndex;
        this.color = color;
        this.title = title;
        this.id = -1;
    }

    public WeekEvent(int startDayIndex, int endDayIndex, int color, String title, int lane, int type) {
        this.startDayIndex = startDayIndex;
        this.endDayIndex = endDayIndex;
        this.color = color;
        this.title = title;
        this.lane = lane;
        this.type = type;
        this.id = -1;
    }

    public WeekEvent(int startDayIndex, int endDayIndex, int color, String title, int lane, int type, int id) {
        this.startDayIndex = startDayIndex;
        this.endDayIndex = endDayIndex;
        this.color = color;
        this.title = title;
        this.lane = lane;
        this.type = type;
        this.id = id;
    }
}