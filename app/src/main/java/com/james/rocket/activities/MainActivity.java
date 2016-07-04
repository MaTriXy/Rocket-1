package com.james.rocket.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.james.rocket.R;
import com.james.rocket.utils.PreferenceUtils;

import java.util.Calendar;

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.animate().translationZ(16.0f).setDuration(150).start();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            v.animate().translationZ(0.0f).setDuration(150).start();
                            break;
                    }
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
                    Intent i = new Intent(MainActivity.this, FlappyActivity.class);
                    i.putExtra("level", PreferenceUtils.LevelIdentifier.SPECIAL);
                    i.putExtra("rocket", R.mipmap.sled);
                    i.putExtra("antirocket", R.mipmap.snowball);
                    i.putExtra("background", R.mipmap.snowbg);
                    i.putExtra("cloud", R.mipmap.cloud);
                    startActivity(i);
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
                Intent i = new Intent(MainActivity.this, FlappyActivity.class);
                i.putExtra("level", PreferenceUtils.LevelIdentifier.EASY);
                i.putExtra("rocket", R.mipmap.rocket3);
                i.putExtra("antirocket", R.mipmap.rocket);
                i.putExtra("background", R.mipmap.bg);
                i.putExtra("cloud", R.mipmap.cloud);
                startActivity(i);
            }
        });

        findViewById(R.id.mid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FlappyActivity.class);
                i.putExtra("level", PreferenceUtils.LevelIdentifier.MEDIUM);
                i.putExtra("rocket", R.mipmap.rocket4);
                i.putExtra("antirocket", R.mipmap.rocket2);
                i.putExtra("background", R.mipmap.sandbg);
                i.putExtra("cloud", R.mipmap.sandcloud);
                startActivity(i);
            }
        });


        findViewById(R.id.hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FlappyActivity.class);
                i.putExtra("level", PreferenceUtils.LevelIdentifier.HARD);
                i.putExtra("rocket", R.mipmap.sunnyrocket);
                i.putExtra("antirocket", R.mipmap.rocket3);
                i.putExtra("background", R.mipmap.sunnybg);
                i.putExtra("cloud", R.mipmap.cloud);
                startActivity(i);
            }
        });

        findViewById(R.id.extr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FlappyActivity.class);
                i.putExtra("level", PreferenceUtils.LevelIdentifier.EXTREME);
                i.putExtra("rocket", R.mipmap.spacerocket);
                i.putExtra("antirocket", R.mipmap.meteor);
                i.putExtra("background", R.mipmap.spacebg);
                i.putExtra("cloud", R.mipmap.spacecloud);
                startActivity(i);
            }
        });

        findViewById(R.id.progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProgressActivity.class));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Drawable settings = ContextCompat.getDrawable(this, R.drawable.ic_settings);
        DrawableCompat.setTint(settings, Color.WHITE);
        menu.findItem(R.id.action_settings).setIcon(settings);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
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

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, getString(R.string.sign_in_failure));
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