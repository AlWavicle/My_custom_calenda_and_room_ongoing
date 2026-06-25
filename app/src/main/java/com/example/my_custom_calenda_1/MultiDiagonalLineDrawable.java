package com.example.my_custom_calenda_1;


import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MultiDiagonalLineDrawable extends Drawable {
    private Paint paint;
    private int lineCount; // 그려야 할 선의 개수!

    public MultiDiagonalLineDrawable(int color) {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        int gap = 15;


        Path path = new Path();

        path.moveTo(width, 0);
        path.lineTo(width, height); // 오른쪽 아래 (직각)
        path.lineTo(width-height-10, height);     // 왼쪽 아래
        path.close();

        // 3. 그리기
        canvas.drawPath(path, paint);
        }


    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); }
    @Override public void setColorFilter(@Nullable ColorFilter colorFilter) { paint.setColorFilter(colorFilter); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}