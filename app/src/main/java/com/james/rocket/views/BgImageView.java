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

    @Override
    public void setImageBitmap(Bitmap bm) {
        setBackground(bm);
    }

    public void setBackground(Bitmap background) {
        this.background = background;
        measure();
    }

    public boolean hasBackground() {
        return background != null;
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

        if (background != null && getWidth() > 0 && getHeight() > 0) {
            width = getWidth();
            int height = getHeight();

            if (background != null && width > 0 && height > 0) {
                lefty = width - background.getWidth();

                w = (height * background.getWidth()) / background.getHeight();
                background = Bitmap.createScaledBitmap(background, w, height, true);
            }
        } else {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    width = getWidth();
                    int height = getHeight();

                    if (background != null && width > 0 && height > 0) {
                        lefty = width - background.getWidth();

                        w = (height * background.getWidth()) / background.getHeight();
                        background = Bitmap.createScaledBitmap(background, w, height, true);

                        getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (background == null) return;

        if (lefty < 0) {
            lefty = w;
            Random r = new Random();
            c2 = c1;
            c1 = r.nextInt(width / 2);
        }

        canvas.drawBitmap(background, lefty, 0, paint);
        canvas.drawBitmap(background, lefty - w, 0, paint);

        if (cloud != null) {
            canvas.drawBitmap(cloud, lefty * 2, c1, paint);
            canvas.drawBitmap(cloud, (lefty - w) * 2, c2, paint);
            canvas.drawBitmap(cloud, (lefty - w / 3) * 2, c2 / 2, paint);
        }

        lefty -= interval;
        postInvalidateDelayed(20);
    }
}