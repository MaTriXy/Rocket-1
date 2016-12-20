package com.james.rocket.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.Random;

public class BgImageView extends ImageView {

    private Bitmap background, cloud;
    private int lefty = 0, width, w, c1, c2, interval = 5;
    private Paint paint;

    public BgImageView(Context context) {
        super(context);
        init();
    }

    public BgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BgImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        measure();
    }

    public void setBackground(Bitmap background) {
        this.background = background;
        measure();
        invalidate();
    }

    public void setCloud(Bitmap cloud) {
        this.cloud = cloud;
    }

    public void setSpeed(int interval) {
        this.interval = interval;
    }

    private void measure() {
        c1 = 20;
        c2 = 50;

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                width = getWidth();
                int height = getHeight();

                lefty = width - background.getWidth();

                w = (height * background.getWidth()) / background.getHeight();
                background = Bitmap.createScaledBitmap(background, w, height, true);
                invalidate();
                return true;
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        if (lefty < 0) {
            lefty = w;
            Random r = new Random();
            c2 = c1;
            c1 = r.nextInt(width / 2);
        }

        if (background != null) {
            canvas.drawBitmap(background, lefty, 0, paint);
            canvas.drawBitmap(background, lefty - w, 0, paint);
        }

        if (cloud != null) {
            canvas.drawBitmap(cloud, lefty * 2, c1, paint);
            canvas.drawBitmap(cloud, (lefty - w) * 2, c2, paint);
            canvas.drawBitmap(cloud, (lefty - w / 3) * 2, c2 / 2, paint);
        }

        lefty -= interval;
        postInvalidateDelayed(20);
    }
}