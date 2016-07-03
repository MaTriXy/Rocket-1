package com.james.rocket.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;

import com.james.rocket.R;

public class ConnectionDialog extends AppCompatDialog {

    public ConnectionDialog(Context context) {
        super(context, R.style.FadeDialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_connection);

        findViewById(R.id.action_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
    }
}
