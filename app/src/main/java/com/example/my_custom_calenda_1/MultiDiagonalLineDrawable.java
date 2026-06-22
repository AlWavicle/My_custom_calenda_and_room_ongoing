package com.example.my_custom_calenda_1;


import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MultiDiagonalLineDrawable extends Drawable {
    private Paint paint;
    private int lineCount; // 그려야 할 선의 개수!

    public MultiDiagonalLineDrawable(int color, int lineCount) {
        this.lineCount = lineCount;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        int gap = 15;

        // 투명 사각형은 1개지만, 그 안에서 lineCount 개수만큼 반복해서 선을 긋습니다!
        for (int i = 0; i < lineCount; i++) {
            int shift = i * gap;
            float startX = width-height*2;
            float startY = height;
            float endX = width;
            float endY = 0;

            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }

    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); }
    @Override public void setColorFilter(@Nullable ColorFilter colorFilter) { paint.setColorFilter(colorFilter); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}