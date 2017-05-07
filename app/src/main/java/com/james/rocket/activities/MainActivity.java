package com.james.rocket.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.james.rocket.BuildConfig;
import com.james.rocket.R;
import com.james.rocket.utils.PreferenceUtils;
import com.james.rocket.views.BgImageView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int BACKGROUND = R.mipmap.bg;
    private static final int BACKGROUND_SAND = R.mipmap.sandbg;
    private static final int BACKGROUND_SUNNY = R.mipmap.sunnybg;
    private static final int BACKGROUND_SPACE = R.mipmap.spacebg;
    private static final int BACKGROUND_SPECIAL = R.mipmap.snowbg;

    private static final int CLOUD = R.mipmap.cloud;
    private static final int CLOUD_SAND = R.mipmap.sandcloud;
    private static final int CLOUD_SPACE = R.mipmap.spacecloud;

    private GoogleApiClient mGoogleApiClient;
    private View special, easy, mid, hard, extr, dropDown, buttonLayout;
    private BgImageView bg;
    private boolean isExpanded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("installed", false)) {
            prefs.edit().putBoolean("installed", true).apply();
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }

        if (!BuildConfig.DEBUG) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .build();
        }

        special = findViewById(R.id.special);
        easy = findViewById(R.id.easy);
        mid = findViewById(R.id.mid);
        hard = findViewById(R.id.hard);
        extr = findViewById(R.id.extr);
        dropDown = findViewById(R.id.dropDown);
        buttonLayout = findViewById(R.id.buttonLayout);
        bg = (BgImageView) findViewById(R.id.bg);

        bg.setSpeed(5);

        if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER) {
            special.setEnabled(true);
            special.setOnClickListener(this);
        }

        easy.setOnClickListener(this);
        mid.setOnClickListener(this);
        hard.setOnClickListener(this);
        extr.setOnClickListener(this);
        dropDown.setOnClickListener(this);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    if (easy.getVisibility() == View.VISIBLE)
                        startGame(PreferenceUtils.LevelIdentifier.EASY, R.mipmap.rocket3, R.mipmap.rocket, BACKGROUND, CLOUD);
                    else if (mid.getVisibility() == View.VISIBLE)
                        startGame(PreferenceUtils.LevelIdentifier.MEDIUM, R.mipmap.rocket4, R.mipmap.rocket2, BACKGROUND_SAND, CLOUD_SAND);
                    else if (hard.getVisibility() == View.VISIBLE)
                        startGame(PreferenceUtils.LevelIdentifier.HARD, R.mipmap.sunnyrocket, R.mipmap.rocket3, BACKGROUND_SUNNY, CLOUD);
                    else if (extr.getVisibility() == View.VISIBLE)
                        startGame(PreferenceUtils.LevelIdentifier.EXTREME, R.mipmap.spacerocket, R.mipmap.meteor, BACKGROUND_SPACE, CLOUD_SPACE);
                    else if (special.getVisibility() == View.VISIBLE)
                        startGame(PreferenceUtils.LevelIdentifier.SPECIAL, R.mipmap.sled, R.mipmap.snowball, BACKGROUND_SPECIAL, CLOUD);
                }
            }
        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
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
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIxoaQv_8CEAIQCQ"), 5001);
            }
        });

        findViewById(R.id.achv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 5001);
            }
        });

        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked();
            }
        });

        setBackground(BACKGROUND);
    }

    public void startGame(PreferenceUtils.LevelIdentifier level, int rocket, int projectile, int background, int cloud) {
        Intent i = new Intent(MainActivity.this, FlappyActivity.class);
        i.putExtra(FlappyActivity.EXTRA_LEVEL, level);
        i.putExtra(FlappyActivity.EXTRA_ROCKET, rocket);
        i.putExtra(FlappyActivity.EXTRA_PROJECTILE, projectile);
        i.putExtra(FlappyActivity.EXTRA_BACKGROUND, background);
        i.putExtra(FlappyActivity.EXTRA_CLOUD, cloud);
        startActivity(i);
    }

    @Override
    public void onConnected(Bundle bundle) {
        findViewById(R.id.achv).setVisibility(View.VISIBLE);
        findViewById(R.id.scoreboard).setVisibility(View.VISIBLE);
        findViewById(R.id.signin).setVisibility(View.GONE);

        //launch the game 100 times to unlock
        if (mGoogleApiClient != null)
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

        if (mGoogleApiClient != null && (mSignInClicked || mAutoStartSignInflow)) {
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
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (mGoogleApiClient != null && resultCode == RESULT_OK)
                mGoogleApiClient.connect();
            else
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.sign_in_failure);
        }
    }

    private void signInClicked() {
        mSignInClicked = true;
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        if (isExpanded) {
            if (v != special) special.setVisibility(View.GONE);
            else setBackground(BACKGROUND_SPECIAL);
            if (v != easy) easy.setVisibility(View.GONE);
            else setBackground(BACKGROUND);
            if (v != mid) mid.setVisibility(View.GONE);
            else setBackground(BACKGROUND_SAND);
            if (v != hard) hard.setVisibility(View.GONE);
            else setBackground(BACKGROUND_SUNNY);
            if (v != extr) extr.setVisibility(View.GONE);
            else setBackground(BACKGROUND_SPACE);
        } else {
            if (special.isEnabled())
                special.setVisibility(View.VISIBLE);
            easy.setVisibility(View.VISIBLE);
            mid.setVisibility(View.VISIBLE);
            hard.setVisibility(View.VISIBLE);
            extr.setVisibility(View.VISIBLE);
        }

        isExpanded = !isExpanded;
        buttonLayout.animate().alpha(isExpanded ? 0 : 1).setDuration(isExpanded ? 50 : 500).setStartDelay(isExpanded ? 0 : 1000).start();
        dropDown.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
    }

    private void setBackground(int background) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Glide.with(this).load(background).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, metrics.heightPixels) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !bg.hasBackground()) {
                    int cx = bg.getWidth() / 2;
                    int cy = bg.getHeight() / 2;

                    ViewAnimationUtils.createCircularReveal(bg, cx, cy, 0, (float) Math.hypot(cx, cy)).setDuration(1000).start();
                }

                bg.setBackground(resource);
            }
        });
    }
}