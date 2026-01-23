package org.deafop.digital_library.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import org.deafop.digital_library.R;
import org.deafop.digital_library.callbacks.CallbackAds;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.rests.RestAdapter;
import org.deafop.digital_library.utils.SharedPref;
import org.deafop.digital_library.utils.Tools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = "ActivitySplash";
    private static final long MIN_SPLASH_TIME = 2000; // Minimum 2 seconds

    String id = "0";
    String url = "";
    ImageView img_splash;
    ImageView img_floating_logo;
    SharedPref sharedPref;
    Call<CallbackAds> callbackCall = null;

    private long splashStartTime;
    private boolean isSplashFinished = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Make splash screen fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);

        // Record start time
        splashStartTime = System.currentTimeMillis();

        sharedPref = new SharedPref(this);
        img_splash = findViewById(R.id.img_splash);
        img_floating_logo = findViewById(R.id.img_floating_logo);

        // Set splash image
        if (sharedPref.getIsDarkTheme()) {
            img_splash.setImageResource(R.drawable.bg_splash);
        } else {
            img_splash.setImageResource(R.drawable.bg_splash);
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (getIntent().hasExtra("nid")) {
            id = getIntent().getStringExtra("nid");
            url = getIntent().getStringExtra("external_link");
        }

        // Start the logo animation after a short delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startLogoAnimation();
            }
        }, 300);

        loadConfig();
    }

    private void startLogoAnimation() {
        // Make the logo visible
        img_floating_logo.setVisibility(View.VISIBLE);

        // Get screen dimensions
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Get logo dimensions
        int logoWidth = (int) (120 * getResources().getDisplayMetrics().density);
        int logoHeight = (int) (120 * getResources().getDisplayMetrics().density);

        // Calculate final center position
        int centerX = screenWidth / 2 - logoWidth / 2;
        int centerY = screenHeight / 2 - logoHeight / 2;

        // Calculate start position (top-right corner, off-screen)
        int startX = screenWidth;
        int startY = -logoHeight;

        // Set initial position
        img_floating_logo.setTranslationX(startX);
        img_floating_logo.setTranslationY(startY);
        img_floating_logo.setScaleX(0.5f);
        img_floating_logo.setScaleY(0.5f);
        img_floating_logo.setAlpha(0f);

        // Animation sequence
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1. Move to center with fade in
                ObjectAnimator moveX = ObjectAnimator.ofFloat(img_floating_logo, "translationX", startX, centerX);
                ObjectAnimator moveY = ObjectAnimator.ofFloat(img_floating_logo, "translationY", startY, centerY);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(img_floating_logo, "alpha", 0f, 1f);
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(img_floating_logo, "scaleX", 0.5f, 1f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(img_floating_logo, "scaleY", 0.5f, 1f);

                // Set durations
                moveX.setDuration(1200);
                moveY.setDuration(1200);
                fadeIn.setDuration(800);
                scaleUpX.setDuration(1200);
                scaleUpY.setDuration(1200);

                // Start first animation
                moveX.start();
                moveY.start();
                fadeIn.start();
                scaleUpX.start();
                scaleUpY.start();

                // Add bounce effect when movement finishes
                moveY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 2. Bounce animation
                        ValueAnimator bounceAnimator = ValueAnimator.ofFloat(0f, 1f);
                        bounceAnimator.setDuration(600);
                        bounceAnimator.setInterpolator(new BounceInterpolator());
                        bounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                // Create a bounce effect on Y axis
                                float bounceY = centerY - (50 * value);
                                img_floating_logo.setTranslationY(bounceY);
                            }
                        });
                        bounceAnimator.start();
                    }
                });
            }
        }, 100);
    }

    private void requestAds(String str) {
        this.callbackCall = RestAdapter.createAPI(str).getAds(AppConfig.REST_API_KEY);
        this.callbackCall.enqueue(new Callback<CallbackAds>() {
            @Override
            public void onResponse(Call<CallbackAds> call, Response<CallbackAds> response) {
                CallbackAds resp = response.body();
                onSplashFinished();
            }

            @Override
            public void onFailure(Call<CallbackAds> call, Throwable th) {
                Log.e("onFailure", "" + th.getMessage());
                onSplashFinished();
            }
        });
    }

    private void loadConfig() {
        String decode = Tools.decodeBase64(AppConfig.SERVER_KEY);
        String data = Tools.decrypt(decode);
        String[] results = data.split("_applicationId_");
        String api_url = results[0];
        String application_id = results[1];
        sharedPref.saveConfig(api_url, application_id);

        if (application_id.equals(BuildConfig.APPLICATION_ID)) {
            if (Tools.isConnect(this)) {
                requestAds(api_url);
            } else {
                launchMainActivity();
            }
        } else {
            launchMainActivity();
        }
        Log.d(TAG, api_url);
        Log.d(TAG, application_id);
    }

    private void onSplashFinished() {
        launchMainActivity();
    }

    private void launchMainActivity() {
        if (isSplashFinished) {
            return; // Already launched
        }
        isSplashFinished = true;

        // Calculate elapsed time
        long elapsedTime = System.currentTimeMillis() - splashStartTime;
        long remainingTime = MIN_SPLASH_TIME - elapsedTime;

        if (remainingTime > 0) {
            // Wait for remaining time to complete minimum splash duration
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    proceedToMainActivity();
                }
            }, remainingTime);
        } else {
            // Already waited enough time
            proceedToMainActivity();
        }
    }

    private void proceedToMainActivity() {
        // Add fade-out animation for the logo before transitioning
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(img_floating_logo, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.start();

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(getApplicationContext(), FirstTime.class);
                if (getIntent().hasExtra("nid")) {
                    intent.putExtra("nid", id);
                    intent.putExtra("external_link", url);
                }
                startActivity(intent);

                // Add fade transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                // Finish splash screen after transition
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callbackCall != null && !callbackCall.isCanceled()) {
            callbackCall.cancel();
        }
    }
}