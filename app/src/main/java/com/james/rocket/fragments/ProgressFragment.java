package com.james.rocket.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.james.rocket.R;

public class ProgressFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        View v = inflater.inflate(R.layout.progress_item, container, false);
        Bundle b = getArguments();
        int pos = b.getInt("position");

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView attempts = (TextView) v.findViewById(R.id.attempts);
        TextView scores = (TextView) v.findViewById(R.id.hiscore);

        ProgressBar ratio = (ProgressBar) v.findViewById(R.id.progressBar);
        title.setText(b.getString("title"));

        int tries = 0, score = 0;
        switch(pos) {
            case 0:
                tries = prefs.getInt("5000", 0);
                score = prefs.getInt("hiscore5000", 0);
                break;
            case 1:
                tries = prefs.getInt("3500", 0);
                score = prefs.getInt("hiscore3500", 0);
                break;
            case 2:
                tries = prefs.getInt("2000", 0);
                score = prefs.getInt("hiscore2000", 0);
                break;
            case 3:
                tries = prefs.getInt("1000", 0);
                score = prefs.getInt("hiscore1000", 0);
                break;
        }

        attempts.setText(attempts.getText() + String.valueOf(tries));
        scores.setText(scores.getText() + String.valueOf(score));
        if (tries > 0) ratio.setProgress(score/tries);
        return v;
    }
}
