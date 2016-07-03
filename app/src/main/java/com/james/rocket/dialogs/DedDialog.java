package com.james.rocket.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.TextView;

import com.james.rocket.R;

public class DedDialog extends AppCompatDialog {

    private Boolean isHighScore;
    private Integer score, highScore;
    private OnClickListener listener;

    public DedDialog(Context context) {
        super(context, R.style.FadeDialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ded);

        setCancelable(false);

        if (score != null && highScore != null && isHighScore != null) {
            TextView content = (TextView) findViewById(R.id.content);
            if (isHighScore) content.setText(String.format(getContext().getString(R.string.high_score), score) + System.getProperty("line.separator") + getContext().getString(R.string.action_continue));
            else content.setText(String.format(getContext().getString(R.string.normal_score), highScore) + System.getProperty("line.separator") + getContext().getString(R.string.action_continue));

            ((TextView) findViewById(R.id.score)).setText(String.valueOf(score));
        }

        findViewById(R.id.action_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(DedDialog.this);
            }
        });
    }

    public DedDialog setIsHighScore(boolean isHighScore) {
        this.isHighScore = isHighScore;
        return this;
    }

    public DedDialog withScore(int score) {
        this.score = score;
        return this;
    }

    public DedDialog withHighScore(int highScore) {
        this.highScore = highScore;
        return this;
    }

    public DedDialog withListener(OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnClickListener {
        void onClick(DedDialog dialog);
    }
}
