package com.james.rocket.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.james.rocket.BuildConfig;
import com.james.rocket.R;
import com.james.rocket.adapters.BasePagerAdapter;
import com.james.rocket.fragments.BaseFragment;
import com.james.rocket.fragments.LevelFragment;
import com.james.rocket.utils.PreferenceUtils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ViewPager.PageTransformer {

    private GoogleApiClient mGoogleApiClient;
    private ViewPager viewPager;
    private BasePagerAdapter adapter;
    private FloatingActionButton play;

    private boolean isDecember;
    private int currentPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        play = (FloatingActionButton) findViewById(R.id.play);

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

        isDecember = BuildConfig.DEBUG || Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;

        adapter = new BasePagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(adapter);
        viewPager.setPageTransformer(false, this);

        play.show();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(isDecember ? viewPager.getCurrentItem() : viewPager.getCurrentItem() + 1);
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
                //startActivity(new Intent(MainActivity.this, ProgressActivity.class));
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
    }

    private BaseFragment[] getFragments() {
        LevelFragment[] fragments = new LevelFragment[isDecember ? 5 : 4];

        int counter = 0;

        if (isDecember) {
            Bundle args = new Bundle();
            args.putParcelable(LevelFragment.EXTRA_LEVEL, PreferenceUtils.LEVELS[0]);

            fragments[counter] = new LevelFragment();
            fragments[counter].setArguments(args);
            counter++;
        }

        Bundle args = new Bundle();
        args.putParcelable(LevelFragment.EXTRA_LEVEL, PreferenceUtils.LEVELS[1]);

        fragments[counter] = new LevelFragment();
        fragments[counter].setArguments(args);
        counter++;

        args = new Bundle();
        args.putParcelable(LevelFragment.EXTRA_LEVEL, PreferenceUtils.LEVELS[2]);

        fragments[counter] = new LevelFragment();
        fragments[counter].setArguments(args);
        counter++;

        args = new Bundle();
        args.putParcelable(LevelFragment.EXTRA_LEVEL, PreferenceUtils.LEVELS[3]);

        fragments[counter] = new LevelFragment();
        fragments[counter].setArguments(args);
        counter++;

        args = new Bundle();
        args.putParcelable(LevelFragment.EXTRA_LEVEL, PreferenceUtils.LEVELS[4]);

        fragments[counter] = new LevelFragment();
        fragments[counter].setArguments(args);

        return fragments;
    }

    public void startGame(int level) {
        Intent i = new Intent(MainActivity.this, FlappyActivity.class);
        i.putExtra(FlappyActivity.EXTRA_LEVEL, PreferenceUtils.LEVELS[level]);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        currentPage = viewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new BasePagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(adapter);
        viewPager.setPageTransformer(false, this);
        viewPager.setCurrentItem(currentPage);
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
    public void transformPage(View page, float position) {
        View cutout = page.findViewById(R.id.cutout);
        View imageView = page.findViewById(R.id.imageView);
        View title = page.findViewById(R.id.title);
        View score = page.findViewById(R.id.score);

        cutout.setTranslationX(-position * cutout.getWidth());
        //imageView.setTranslationX(-position * cutout.getWidth() / 2);
        title.setTranslationX(-position * cutout.getWidth() / 4);
        score.setTranslationX(-position * cutout.getWidth() / 3);
    }
}