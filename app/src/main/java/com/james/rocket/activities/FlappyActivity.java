package com.james.rocket.activities;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.james.rocket.R;
import com.james.rocket.dialogs.DedDialog;
import com.james.rocket.utils.PreferenceUtils;
import com.james.rocket.views.BgImageView;

import java.util.Random;

public class FlappyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static String EXTRA_LEVEL = "level";
    public final static String EXTRA_ROCKET = "rocket";
    public final static String EXTRA_PROJECTILE = "projectile";
    public final static String EXTRA_BACKGROUND = "background";
    public final static String EXTRA_CLOUD = "cloud";

    private TextView text;
    private ImageView flappy, antiflappy;
    private BgImageView bg;
    private View progressBar, game;

    private int counter = 0, multiplier = 1, difficulty = 5000;
    private int height = 1000, antiwidth, widthanti;
    private boolean cancelled, cancelled2;
    private PreferenceUtils.LevelIdentifier level;
    private float initial;
    private Drawable antirocket;
    private Handler handler;
    private DedDialog dedDialog;

    private GoogleApiClient mGoogleApiClient;

    private Thread dedThread = new Thread() {
        @Override
        public void run() {
            while (true) {
                if (game.isEnabled() && (dedDialog == null || !dedDialog.isShowing()) && counter > 0 && (antiflappy.getY() + (antiflappy.getHeight() / 3) >= flappy.getY() - (flappy.getHeight() / 3) && antiflappy.getY() - (antiflappy.getHeight() / 3) <= flappy.getY() + (flappy.getHeight() / 3)) && (antiflappy.getX() + (antiflappy.getWidth() / 2) >= flappy.getX() - (flappy.getWidth() / 2) && antiflappy.getX() - (antiflappy.getWidth() / 2) <= flappy.getX() + (flappy.getWidth() / 2))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onFailed(counter);
                            counter = 0;
                        }
                    });
                }

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_flappy);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        text = (TextView) findViewById(R.id.textView5);
        flappy = (ImageView) findViewById(R.id.imageView);
        antiflappy = (ImageView) findViewById(R.id.antiflappy);
        bg = (BgImageView) findViewById(R.id.bg);
        progressBar = findViewById(R.id.progressBar);
        game = findViewById(R.id.game);

        game.setEnabled(false);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        initial = height / 2;

        level = (PreferenceUtils.LevelIdentifier) getIntent().getSerializableExtra(EXTRA_LEVEL);

        Drawable rocket = ContextCompat.getDrawable(this, getIntent().getIntExtra(EXTRA_ROCKET, R.mipmap.rocket3));
        antirocket = ContextCompat.getDrawable(this, getIntent().getIntExtra(EXTRA_PROJECTILE, R.mipmap.rocket));

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        antiflappy.setX(size.x + antiflappy.getWidth());

        switch (level) {
            case EASY:
                bg.setSpeed(5);
                multiplier = 1;
                difficulty = 5000;
                break;
            case MEDIUM:
                bg.setSpeed(10);
                multiplier = 2;
                difficulty = 3500;
                break;
            case HARD:
                bg.setSpeed(15);
                multiplier = 3;
                difficulty = 2000;
                break;
            case EXTREME:
                bg.setSpeed(20);
                multiplier = 5;
                difficulty = 1000;
                break;
            case SPECIAL:
                bg.setSpeed(10);
                multiplier = 2;
                difficulty = 3500;
                break;
        }

        flappy.setImageDrawable(rocket);
        antiflappy.setImageDrawable(antirocket);
        antiflappy.setRotation(180);

        antiwidth = size.x - antiflappy.getWidth();
        widthanti = antiwidth + antiflappy.getWidth();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (game.isEnabled() && (dedDialog == null || !dedDialog.isShowing())) {
                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);
                    Random r = new Random();
                    antiflappy.setImageDrawable(ContextCompat.getDrawable(FlappyActivity.this, R.drawable.ic_warning));
                    antiflappy.setScaleY(-1);
                    antiflappy.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    antiflappy.setY(r.nextInt(size.y));
                    antiwidth = size.x - antiflappy.getWidth();
                    antiflappy.setX(antiwidth);

                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(difficulty / 3);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    antiflappy.setX(widthanti);
                                    antiflappy.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    antiflappy.setImageDrawable(antirocket);
                                    antiflappy.setScaleY(1);
                                    antiflappy.animate().x(antiflappy.getWidth()*-1).setDuration(difficulty / 2).start();
                                }
                            });
                        }
                    }.start();
                }
                handler.postDelayed(this, difficulty);
            }
        }, difficulty);

        Glide.with(this).load(getIntent().getStringExtra(EXTRA_BACKGROUND)).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, height) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bg.setBackground(resource);
                dedThread.start();

                progressBar.animate().scaleY(0).scaleX(0).setInterpolator(new AccelerateInterpolator()).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        game.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            int cx = game.getWidth() / 2;
                            int cy = game.getHeight() / 2;

                            Animator animator = ViewAnimationUtils.createCircularReveal(game, cx, cy, 0, (float) Math.hypot(cx, cy));
                            animator.setDuration(1000);
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    game.setEnabled(true);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            animator.start();
                        } else {
                            game.setAlpha(0);
                            game.animate().alpha(1).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    game.setEnabled(true);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        });

        Glide.with(this).load(getIntent().getStringExtra(EXTRA_CLOUD)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bg.setCloud(resource);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (game.isEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                counter++;
                flappy.animate().y(0).rotationBy(-17 - (flappy.getRotation() / 3)).setDuration(difficulty / (((counter * 10) / (counter + 1)) + 1)).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        cancelled = false;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!cancelled && (dedDialog == null || !dedDialog.isShowing()) && counter > 0) {
                            onFailed(counter);
                            counter = 0;
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        cancelled = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                flappy.animate().y(height).rotationBy(25 - (flappy.getRotation() / 3)).setDuration(difficulty / (((counter * 10) / (counter + 1)) + 1)).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        cancelled2 = false;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!cancelled2 && (dedDialog == null || !dedDialog.isShowing()) && counter > 0) {
                            onFailed(counter);
                            counter = 0;
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        cancelled2 = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).start();
            }

            text.setText(String.valueOf(counter));
        }

        return super.dispatchTouchEvent(event);
    }

    public void onFailed(final int counter) {
        flappy.animate().cancel();
        flappy.setY(initial);
        flappy.setRotation(0.0f);
        text.setText(R.string.action_start);

        Integer totalScore = PreferenceUtils.getScore(this, level, PreferenceUtils.PreferenceIdentifier.TOTAL_ATTEMPTS);
        PreferenceUtils.putScore(this, level, PreferenceUtils.PreferenceIdentifier.TOTAL_ATTEMPTS, totalScore == null ? 1 : totalScore + 1);

        Integer highScore = PreferenceUtils.getScore(this, level, PreferenceUtils.PreferenceIdentifier.HIGH_SCORE);
        int score = counter * multiplier;

        if (highScore == null || score > highScore) {
            highScore = score;
            PreferenceUtils.putScore(this, level, PreferenceUtils.PreferenceIdentifier.HIGH_SCORE, score);

            if (mGoogleApiClient.isConnected()) {
                //leaderboards
                Games.Leaderboards.submitScore(mGoogleApiClient, "CgkIxoaQv_8CEAIQCQ", score);

                if (level == PreferenceUtils.LevelIdentifier.EXTREME && highScore >= 50) {
                    //ninja
                    Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQBg");
                    if (highScore >= 100) {
                        //super ninja
                        Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQBw");
                    }
                }
                //spammer
                else if (level == PreferenceUtils.LevelIdentifier.EASY && highScore >= 1000) {
                    Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQCA");
                }
                //t-100
                if (highScore >= 100) {
                    Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQAA");
                    //t-500
                    if (highScore >= 500) {
                        Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQAQ");
                    }
                }

                //1000 and up
                if (score > 0) {
                    Games.Achievements.increment(mGoogleApiClient, "CgkIxoaQv_8CEAIQAg", score);
                }
            }
        }

        if (dedDialog == null || !dedDialog.isShowing()) {
            dedDialog = new DedDialog(this)
                    .setIsHighScore(score > highScore)
                    .withScore(score)
                    .withHighScore(highScore)
                    .withListener(new DedDialog.OnClickListener() {
                        @Override
                        public void onClick(DedDialog dialog) {
                            flappy.animate().cancel();
                            flappy.setY(initial);
                            flappy.setRotation(0.0f);
                            text.setText(R.string.action_start);
                            dialog.dismiss();
                        }
                    });

            try {
                dedDialog.show();
            } catch (WindowManager.BadTokenException ignored) {
            }
        }
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = new Intent(FlappyActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return i;
    }


    //google api stuff---------------------------------------------------------------------------------------------------------------------------------------------

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        if (mSignInClicked || mAutoStartSignInflow) {
            mSignInClicked = false;
            mAutoStartSignInflow = false;

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, getString(R.string.sign_in_failure));
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
}