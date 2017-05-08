package com.james.rocket.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.james.rocket.R;
import com.james.rocket.utils.StaticUtils;

public class CircleCutoutView extends View {

    private Paint paint;
    private Bitmap bitmap;
    private int padding;

    public CircleCutoutView(Context context) {
        this(context, null);
    }

    public CircleCutoutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCutoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        padding = (int) StaticUtils.getPixelsFromDp(getContext(), 86);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(getBitmap(canvas.getWidth(), canvas.getHeight()), 0, 0, null);
    }

    private Bitmap getBitmap(int width, int height) {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            canvas.drawCircle(width / 2, height / 2, (Math.max(width, height) / 2) - padding, paint);
        }

        return bitmap;
    }
}
