package com.example.my_custom_calenda_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class WeekEventView extends View {
    private List<WeekEvent> eventList = new ArrayList<>();
    private Paint paint;

    public WeekEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setEvents(List<WeekEvent> events) {
        this.eventList.clear();
        if (events != null) {
            this.eventList.addAll(events);
        }
        invalidate(); // 다시 그리기
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (eventList == null || eventList.isEmpty()) return;

        // 화면 밀도(DP) 계산
        float density = getContext().getResources().getDisplayMetrics().density;
        float cellWidth = getWidth() / 7f;
        float viewHeight = getHeight();

        // 🚀 각 날짜별로 그려진 도형 개수를 추적하기 위한 카운터 (0~6일)
        int[] type1Counts = new int[7];
        int[] type2Counts = new int[7];
        int[] type3Counts = new int[7];

        for (WeekEvent event : eventList) {
            paint.setColor(event.color);

            // 막대 세로 위치 (날짜 가리지 않게 아래로 조정 및 얇게)
            float barHeight = 7f; 
            float barSpacing = 4f;
            float startTop = 75f;  // 날짜 숫자 아래로 배치

            // 기본 위치 계산 (막대용)
            float left = event.startDayIndex * cellWidth + 5f;
            float right = (event.endDayIndex + 1) * cellWidth - 5f;
            float barTop = startTop + (event.lane * (barHeight + barSpacing));
            float barBottom = barTop + barHeight;

            if (event.type == 3) {
                // 🚀 당일: 동그라미 (2개 이상 시 절반 겹치기 및 날짜 텍스트 높이 배치)
                int dayIdx = event.startDayIndex;
                float radius = 4f * density; // 크기 소폭 확대
                
                // 날짜 텍스트 위치 (보통 상단에서 10-20dp 지점)
                float textCenterX = dayIdx * cellWidth + cellWidth / 2f;
                float centerY = 20 * density; 
                
                // 날짜 텍스트를 가리지 않도록 텍스트 중심에서 오른쪽으로 오프셋
                float startX = textCenterX + (10 * density); 
                
                // 절반 겹치기: 간격을 반지름(radius)만큼만 줌 (radius * 2f가 아닌 radius)
                float offsetX = type3Counts[dayIdx] * radius; 
                float centerX = startX + offsetX;
                
                canvas.drawCircle(centerX, centerY, radius, paint);
                type3Counts[dayIdx]++;
            } else if (event.type == 1) {
                // 🚀 시작일만 있음 (Type 1): 왼쪽 끝 상단 배치
                // 전역 lane 대신 이 날짜의 Type 1 중 몇 번째인지(local index)를 사용
                int dayIdx = event.startDayIndex;
                float triSize = 25f;
                float baseX = dayIdx * cellWidth;
                float offsetX = type1Counts[dayIdx] * (triSize + 1f); // 간격을 1로 좁힘
                float targetX = baseX + offsetX;
                float targetY = 0f;

                android.graphics.Path path = new android.graphics.Path();
                path.moveTo(targetX, targetY); // 직각점 (왼쪽 위 끝)
                path.lineTo(targetX + triSize, targetY); // 상변
                path.lineTo(targetX, targetY + triSize); // 높이
                path.close();
                canvas.drawPath(path, paint);
                
                type1Counts[dayIdx]++; // 카운트 증가
            } else if (event.type == 2) {
                // 🚀 종료일만 있음 (Type 2): 오른쪽 끝 하단 배치
                // 전역 lane 대신 이 날짜의 Type 2 중 몇 번째인지(local index)를 사용
                int dayIdx = event.endDayIndex;
                float triSize = 25f; 
                float baseX = (dayIdx + 1) * cellWidth;
                float offsetX = type2Counts[dayIdx] * (triSize + 1f); // 간격을 1로 좁힘
                float targetX = baseX - offsetX;
                float targetY = viewHeight;

                android.graphics.Path path = new android.graphics.Path();
                path.moveTo(targetX, targetY); // 직각점 (오른쪽 아래 끝)
                path.lineTo(targetX - triSize, targetY); // 밑변
                path.lineTo(targetX, targetY - triSize); // 높이
                path.close();
                canvas.drawPath(path, paint);
                
                type2Counts[dayIdx]++; // 카운트 증가
            } else {
                // 기간 일정: 막대
                RectF rect = new RectF(left, barTop, right, barBottom);
                canvas.drawRoundRect(rect, 4f, 4f, paint);
            }
        }
    }
}