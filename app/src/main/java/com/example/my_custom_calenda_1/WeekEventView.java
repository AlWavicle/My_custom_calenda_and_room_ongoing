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

        // 행 가로를 7등분 (하루 너비)
        float cellWidth = getWidth() / 7f;

        for (WeekEvent event : eventList) {
            paint.setColor(event.color);

            // 막대 가로 위치 계산 (0~6 기준)
            float left = event.startDayIndex * cellWidth + 5f;
            float right = (event.endDayIndex + 1) * cellWidth - 5f;

            // 막대 세로 위치 (날짜 가리지 않게 아래로 조정 및 얇게)
            float barHeight = 10f; // 더 얇게 (기존 15f)
            float barSpacing = 4f;
            float startTop = 75f;  // 날짜 숫자 아래로 배치 (기존 40f)

            float top = startTop + (event.lane * (barHeight + barSpacing));
            float bottom = top + barHeight;

            if (event.type == 3) {
                // 당일: 동그라미
                float centerX = (left + right) / 2f;
                float centerY = (top + bottom) / 2f;
                float radius = barHeight / 2f + 2f;
                canvas.drawCircle(centerX, centerY, radius, paint);
            } else if (event.type == 1) {
                // 시작일만 있음: 왼쪽 하단 직삼각형
                android.graphics.Path path = new android.graphics.Path();
                path.moveTo(left, top); // 상단 좌측 (직각의 위쪽) -> 아님, 문제 요구사항: "왼쪽 밑에있는 직삼각형"
                // 왼쪽 밑에 직각이 있는 삼각형: (L, T), (L, B), (R, B)
                path.moveTo(left, top);
                path.lineTo(left, bottom);
                path.lineTo(right, bottom);
                path.close();
                canvas.drawPath(path, paint);
            } else if (event.type == 2) {
                // 종료일만 있음: 오른쪽 하단 직삼각형
                // 오른쪽 밑에 직각이 있는 삼각형: (L, B), (R, T), (R, B)
                android.graphics.Path path = new android.graphics.Path();
                path.moveTo(left, bottom);
                path.lineTo(right, top);
                path.lineTo(right, bottom);
                path.close();
                canvas.drawPath(path, paint);
            } else {
                // 기간 일정: 막대
                top = startTop + (event.lane * (barHeight-5f + barSpacing));
                bottom = top + barHeight-5f;
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rect, 4f, 4f, paint);
            }
        }
    }
}