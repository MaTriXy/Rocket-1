package com.james.rocket.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.james.rocket.R;
import com.james.rocket.utils.PreferenceUtils;

public class ProgressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_progress, container, false);

        PreferenceUtils.LevelIdentifier level = (PreferenceUtils.LevelIdentifier) getArguments().getSerializable("level");

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView attempts = (TextView) v.findViewById(R.id.attempts);
        TextView scores = (TextView) v.findViewById(R.id.hiscore);

        ProgressBar ratio = (ProgressBar) v.findViewById(R.id.progressBar);
        title.setText(level.toString());

        Integer tries = PreferenceUtils.getScore(getContext(), level, PreferenceUtils.PreferenceIdentifier.TOTAL_ATTEMPTS);
        if (tries == null) tries = 0;

        Integer score = PreferenceUtils.getScore(getContext(), level, PreferenceUtils.PreferenceIdentifier.HIGH_SCORE);
        if (score == null) score = 0;

        attempts.setText(String.format(getString(R.string.total_attempts), tries));
        scores.setText(String.valueOf(score));
        if (tries > 0) ratio.setProgress(score/tries);

        ImageView image = (ImageView) v.findViewById(R.id.image);
        ImageView rocket = (ImageView) v.findViewById(R.id.rocket);

        switch(level) {
            case EASY:
                image.setImageResource(R.mipmap.bg);
                rocket.setImageResource(R.mipmap.rocket3);
                break;
            case MEDIUM:
                image.setImageResource(R.mipmap.sandbg);
                rocket.setImageResource(R.mipmap.rocket4);
                break;
            case HARD:
                image.setImageResource(R.mipmap.sunnybg);
                rocket.setImageResource(R.mipmap.sunnyrocket);
                break;
            case EXTREME:
                image.setImageResource(R.mipmap.spacebg);
                rocket.setImageResource(R.mipmap.spacerocket);
        }

        return v;
    }
}
