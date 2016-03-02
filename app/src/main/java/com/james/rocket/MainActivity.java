package com.james.rocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("installed", false)) {
            prefs.edit().putBoolean("installed", true).apply();
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    v.animate().translationZ(16.0f).setDuration(500).start();
                } else if ((event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    v.animate().translationZ(0.0f).setDuration(500).start();
                }
                return false;
            }
        };

        if (Calendar.getInstance().get(Calendar.MONTH) == 11) {
            findViewById(R.id.special).setVisibility(View.VISIBLE);
            findViewById(R.id.special).setOnTouchListener(onTouchListener);
            findViewById(R.id.special).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SpecialLevel.class));
                }
            });
        }

        findViewById(R.id.easy).setOnTouchListener(onTouchListener);
        findViewById(R.id.mid).setOnTouchListener(onTouchListener);
        findViewById(R.id.hard).setOnTouchListener(onTouchListener);
        findViewById(R.id.extr).setOnTouchListener(onTouchListener);

        findViewById(R.id.easy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Flappy.class);
                i.putExtra("difficulty", 5000);
                startActivity(i);
            }
        });

        findViewById(R.id.mid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Flappy.class);
                i.putExtra("difficulty", 3500);
                startActivity(i);
            }
        });


        findViewById(R.id.hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Flappy.class);
                i.putExtra("difficulty", 2000);
                startActivity(i);
            }
        });

        findViewById(R.id.extr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Flappy.class);
                i.putExtra("difficulty", 1000);
                startActivity(i);
            }
        });

        findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Options.class));
            }
        });

        findViewById(R.id.progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Progress.class));
            }
        });

        findViewById(R.id.scoreboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIxoaQv_8CEAIQCQ"), 5001);
            }
        });

        findViewById(R.id.achv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 5001);
            }
        });

        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked();
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.achv).setVisibility(View.VISIBLE);
        findViewById(R.id.scoreboard).setVisibility(View.VISIBLE);
        findViewById(R.id.signin).setVisibility(View.GONE);

        //launch the game 100 times to unlock
        Games.Achievements.increment(mGoogleApiClient, "CgkIxoaQv_8CEAIQBA", 1);
    }

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        if (mSignInClicked || mAutoStartSignInflow) {
            mSignInClicked = false;
            mAutoStartSignInflow = false;

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, "Sign-in failed");
        } else {
            //add sign in button
            findViewById(R.id.signin).setVisibility(View.VISIBLE);
            findViewById(R.id.progress).setVisibility(View.GONE);
            findViewById(R.id.achv).setVisibility(View.GONE);
            findViewById(R.id.scoreboard).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.sign_in_failure);
            }
        }
    }

    private void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }
}