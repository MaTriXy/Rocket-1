package com.james.rocket.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.james.rocket.R;
import com.james.rocket.data.LevelData;
import com.james.rocket.utils.PreferenceUtils;
import com.james.rocket.views.BgImageView;

import java.util.Locale;

public class LevelFragment extends BaseFragment {

    public static final String EXTRA_LEVEL = "com.james.rocket.EXTRA_LEVEL";

    private LevelData level;
    private BgImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_level, container, false);
        imageView = (BgImageView) v.findViewById(R.id.imageView);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView score = (TextView) v.findViewById(R.id.score);

        level = getArguments().getParcelable(EXTRA_LEVEL);
        if (level == null) return null;

        imageView.setSpeed(level.getSpeed());
        title.setText(level.getName());
        score.setText(String.format(Locale.getDefault(), getString(R.string.total_high_score), PreferenceUtils.getScore(getContext(), level.getIdentifier(), PreferenceUtils.PreferenceIdentifier.HIGH_SCORE)));

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Glide.with(this).load(level.getBackground()).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, metrics.heightPixels) {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (imageView.getWidth() != 0 && imageView.getHeight() != 0) {
                    imageView.setAlpha(0f);
                    imageView.setBackground(resource);
                    imageView.animate().alpha(1).start();
                } else {
                    imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            imageView.setAlpha(0f);
                            imageView.setBackground(resource);
                            imageView.animate().alpha(1).start();
                            imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            }
        });

        return v;
    }

    @Override
    public String getTitle() {
        return level != null ? level.getName() : null;
    }
}
