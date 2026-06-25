package com.example.my_custom_calenda_1;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// 단일 일정 동그라미를 그리는 커스텀 Drawable
public class CircleDrawable extends Drawable {
    private Paint paint;

    public CircleDrawable(int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 테두리를 부드럽고 깔끔하게
        paint.setStyle(Paint.Style.FILL);         // 안을 꽉 채우기
        paint.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // 현재 뷰의 가로, 세로 크기 가져오기
        int width = getBounds().width();
        int height = getBounds().height();
        float radius = Math.min(width, height) / 2f;
        canvas.drawCircle(width-radius, height / 2f, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}