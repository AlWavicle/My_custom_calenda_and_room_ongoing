package com.example.my_custom_calenda_1;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeekRow {
    // 1. 이 행의 날짜들 (7개 고정)
    public List<LocalDate> days;

    // 2. [캔버스용] 행 전체를 가로지르는 긴 막대기 데이터들
    public List<WeekEvent> rowEvents;

    // 3. [개별 셀용] 점, 대각선 등 상세 마커 데이터들
    // (기존에 사용하시던 sSCModel 리스트를 여기서 관리하거나 필터링해서 담습니다)
    public List<SelectSendCalenderModel> cellEvents;

    public WeekRow() {
        this.days = new ArrayList<>(7);
        this.rowEvents = new ArrayList<>();
        this.cellEvents = new ArrayList<>();
    }

    public WeekRow(List<LocalDate> days) {
        this.days = days;
        this.rowEvents = new ArrayList<>();
        this.cellEvents = new ArrayList<>();
    }

    public void setDays(List<LocalDate> days) {
        this.days = days;
    }

    public List<LocalDate> getDays() {
        return days;
    }

    public List<WeekEvent> getRowEvents() {
        return rowEvents;
    }

    public List<SelectSendCalenderModel> getCellEvents() {
        return cellEvents;
    }

    public void addCellEvent(SelectSendCalenderModel event) {
        this.cellEvents.add(event);
    }

    public void addRowEvent(WeekEvent event) {
        this.rowEvents.add(event);
    }
}