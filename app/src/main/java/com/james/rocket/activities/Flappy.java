package com.james.rocket.activities;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.james.rocket.R;
import com.james.rocket.views.BgImageView;

import java.util.Random;

public class Flappy extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageView flappy, antiflappy;
    BgImageView bg;
    int counter = 0, multiplier = 1;
    TextView text;
    int height = 1000, antiwidth, widthanti;
    boolean cancelled, cancelled2, dialog = false, active = true;
    int difficulty = 5000;
    float initial;
    Drawable antirocket, rocket, background, cloud;
    Handler h;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flappy);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        text = (TextView) findViewById(R.id.textView5);
        flappy = (ImageView) findViewById(R.id.imageView);
        antiflappy = (ImageView) findViewById(R.id.antiflappy);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        initial = height/2;

        difficulty = getIntent().getIntExtra("difficulty", 5000);

        rocket = ContextCompat.getDrawable(this, R.mipmap.rocket);
        bg = (BgImageView) findViewById(R.id.bg);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        antiflappy.setX(size.x + antiflappy.getWidth());

        int status = Color.parseColor("#000000"), nav = Color.parseColor("#000000");
        switch (difficulty) {
            case 5000 :
                status = Color.parseColor("#42A5F5");
                nav = Color.parseColor("#9E9E9E");

                rocket = ContextCompat.getDrawable(this, R.mipmap.rocket3);
                antirocket = ContextCompat.getDrawable(this, R.mipmap.rocket);
                background = ContextCompat.getDrawable(this, R.mipmap.bg);
                cloud = ContextCompat.getDrawable(this, R.mipmap.cloud);

                bg.setSpeed(5);
                multiplier = 1;
                break;
            case 3500 :
                status = Color.parseColor("#f57c00");
                nav = Color.parseColor("#f57c00");

                rocket = ContextCompat.getDrawable(this, R.mipmap.rocket4);
                antirocket = ContextCompat.getDrawable(this, R.mipmap.rocket2);
                background = ContextCompat.getDrawable(this, R.mipmap.sandbg);
                cloud = ContextCompat.getDrawable(this, R.mipmap.sandcloud);

                bg.setSpeed(10);
                multiplier = 2;
                break;
            case 2000 :
                status = Color.parseColor("#1976d2");
                nav = Color.parseColor("#388e3c");

                rocket = ContextCompat.getDrawable(this, R.mipmap.sunnyrocket);
                antirocket = ContextCompat.getDrawable(this, R.mipmap.rocket3);
                background = ContextCompat.getDrawable(this, R.mipmap.sunnybg);
                cloud = ContextCompat.getDrawable(this, R.mipmap.cloud);


                bg.setSpeed(15);
                multiplier = 3;
                break;
            case 1000 :
                status = Color.parseColor("#263238");
                nav = Color.parseColor("#263238");

                rocket = ContextCompat.getDrawable(this, R.mipmap.spacerocket);
                antirocket = ContextCompat.getDrawable(this, R.mipmap.meteor);
                background = ContextCompat.getDrawable(this, R.mipmap.spacebg);
                cloud = ContextCompat.getDrawable(this, R.mipmap.spacecloud);

                bg.setSpeed(20);
                multiplier = 5;
                break;
        }

        flappy.setImageDrawable(rocket);
        antiflappy.setImageDrawable(antirocket);
        antiflappy.setRotation(180);

        bg.setBackground(((BitmapDrawable) background).getBitmap());
        bg.setCloud(((BitmapDrawable) cloud).getBitmap());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(status);
            getWindow().setNavigationBarColor(nav);
        }

        findViewById(R.id.click).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    active = true;
                    dialog = false;
                    counter++;
                    flappy.animate().y(0).rotationBy(-17 - (flappy.getRotation()/3)).setDuration(difficulty / (((counter * 10) / (counter + 1)) + 1)).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            cancelled = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (!cancelled && !dialog && !(counter == 0)) {
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
                    flappy.animate().y(height).rotationBy(25 - (flappy.getRotation()/3)).setDuration(difficulty / (((counter * 10) / (counter + 1)) + 1)).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            cancelled2 = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (!cancelled2 && (!dialog) && !(counter == 0)) {
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
                return false;
            }
        });

        antiwidth = size.x - antiflappy.getWidth();
        widthanti = antiwidth + antiflappy.getWidth();

        h = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (!dialog) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    Random r = new Random();
                    antiflappy.setImageDrawable(getResources().getDrawable(R.mipmap.warning));
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
                                    antiflappy.setImageDrawable(antirocket);
                                    antiflappy.animate().x(antiflappy.getWidth()*-1).setDuration(difficulty / 2).start();
                                }
                            });
                        }
                    }.start();
                }
                h.postDelayed(this, difficulty);
            }
        };
        h.postDelayed(r, difficulty);

        new Thread() {
            @Override
            public void run() {
                while(true) {
                    if ((!dialog && !(counter == 0)) && (antiflappy.getY() + (antiflappy.getHeight()/3) >= flappy.getY() - (flappy.getHeight()/3) && antiflappy.getY() - (antiflappy.getHeight()/3) <= flappy.getY() + (flappy.getHeight()/3)) && (antiflappy.getX() + (antiflappy.getWidth()/2) >= flappy.getX() - (flappy.getWidth()/2) && antiflappy.getX() - (antiflappy.getWidth()/2) <= flappy.getX() + (flappy.getWidth()/2))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onFailed(counter);
                                counter = 0;
                            }
                        });
                    }
                    if (isInterrupted()) return;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void onFailed(final int counter) {
        dialog = true;

        flappy.animate().cancel();
        flappy.setY(initial);
        flappy.setRotation(0.0f);
        text.setText("Tap to start");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.edit().putInt(String.valueOf(difficulty), prefs.getInt(String.valueOf(difficulty), 0) + 1).apply();

        int hiscore = prefs.getInt("hiscore" + String.valueOf(difficulty), -1);
        int score = counter * multiplier;

        String string = "";

        if (score > hiscore) {
            hiscore = score;
            string = "New high score, " + hiscore + "!" + System.getProperty("line.separator") + "Press ok to continue.";
            prefs.edit().putInt("hiscore" + String.valueOf(difficulty), score).apply();
            if (mGoogleApiClient.isConnected()) {
                //leaderboards
                Games.Leaderboards.submitScore(mGoogleApiClient, "CgkIxoaQv_8CEAIQCQ", score);

                if (difficulty == 1000 && hiscore >= 50) {
                    //ninja
                    Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQBg");
                    if (hiscore >= 100) {
                        //super ninja
                        Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQBw");
                    }
                }
                //spammer
                else if (difficulty == 5000 && hiscore >= 1000) {
                    Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQCA");
                }
                //t-100
                if (hiscore >= 100) {
                    Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQAA");
                    //t-500
                    if (hiscore >= 500) {
                        Games.Achievements.unlock(mGoogleApiClient, "CgkIxoaQv_8CEAIQAQ");
                    }
                }

                //1000 and up
                if (score > 0) {
                    Games.Achievements.increment(mGoogleApiClient, "CgkIxoaQv_8CEAIQAg", score);
                }
            }
        } else {
            string = "Your high score is " + String.valueOf(hiscore) + "." + System.getProperty("line.separator") + "Press ok to continue.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Flappy.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        ((TextView) view.findViewById(R.id.content)).setText(string);
        ((TextView) view.findViewById(R.id.score)).setText(String.valueOf(score));
        builder.setCancelable(false).setView(view).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int id) {
                flappy.animate().cancel();
                flappy.setY(initial);
                flappy.setRotation(0.0f);
                text.setText("Press to start");
                dialog = false;
                d.dismiss();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialog = false;
            }
        });
        if (active) {
            builder.create().show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        active = focus;
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
        Intent i = new Intent(Flappy.this, MainActivity.class);
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

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, "Sign-in failed");
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