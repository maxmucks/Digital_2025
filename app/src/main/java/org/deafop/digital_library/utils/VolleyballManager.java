// org/deafop/digital_library/utils/VolleyballManager.java
package org.deafop.digital_library.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import org.deafop.digital_library.R;

public class VolleyballManager {

    public interface VolleyballListener {
        void onVolleyballClicked();
        void onVolleyballExited();
    }

    private Activity activity;
    private ViewGroup container;
    private ImageView volleyballView;
    private VolleyballListener listener;
    private AnimatorSet currentAnimator;

    // Configuration
    private int size = 120; // dp
    private int rotationSpeed = 8000; // ms
    private String exitCorner = "top_right";
    private boolean enableClickAnywhere = true;
    private boolean showOnlyOnce = true;

    public VolleyballManager(Activity activity, ViewGroup container) {
        this.activity = activity;
        this.container = container;
    }

    public void setListener(VolleyballListener listener) {
        this.listener = listener;
    }

    public void show() {
        // Check if already showing or should show based on preferences
        if (showOnlyOnce) {
            SharedPreferences prefs = activity.getSharedPreferences("volleyball_prefs", 0);
            boolean dismissed = prefs.getBoolean("volleyball_dismissed", false);

            if (dismissed) {
                return;
            }
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createVolleyballView();
                startDropAnimation();
            }
        });
    }

    public void hide() {
        if (volleyballView != null && currentAnimator != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startExitAnimation();
                }
            });
        }
    }

    public void setSize(int sizeDp) {
        this.size = sizeDp;
    }

    public void setExitCorner(String corner) {
        this.exitCorner = corner;
    }

    public void setEnableClickAnywhere(boolean enable) {
        this.enableClickAnywhere = enable;
    }

    public void setShowOnlyOnce(boolean showOnlyOnce) {
        this.showOnlyOnce = showOnlyOnce;
    }

    private void createVolleyballView() {
        // Remove existing view if any
        if (volleyballView != null) {
            container.removeView(volleyballView);
        }

        // Create new ImageView
        volleyballView = new ImageView(activity);
        volleyballView.setImageResource(R.drawable.ic_volleyball);

        // Convert dp to pixels
        int sizeInPixels = dpToPx(size);

        // Set layout parameters
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(sizeInPixels, sizeInPixels);
        params.leftMargin = container.getWidth() / 2 - sizeInPixels / 2;
        params.topMargin = -sizeInPixels; // Start above screen

        volleyballView.setLayoutParams(params);

        // Add to container
        container.addView(volleyballView);

        // Set click listener
        volleyballView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onVolleyballClicked();
                }
                startExitAnimation();
            }
        });
    }

    private void startDropAnimation() {
        if (volleyballView == null) return;

        // Calculate positions
        int screenHeight = container.getHeight();
        int ballHeight = volleyballView.getHeight();
        float startY = -ballHeight;
        float centerY = (screenHeight / 2f) - (ballHeight / 2f);

        // Set initial position
        volleyballView.setY(startY);
        volleyballView.setVisibility(View.VISIBLE);
        volleyballView.bringToFront(); // Ensure it's on top

        // Create drop animation
        ObjectAnimator dropAnimator = ObjectAnimator.ofFloat(volleyballView, "translationY", startY, centerY - 60);
        dropAnimator.setDuration(1800);
        dropAnimator.setInterpolator(new OvershootInterpolator(1.5f));

        // Bounce animation
        ObjectAnimator bounceAnimator = ObjectAnimator.ofFloat(volleyballView, "translationY", centerY - 60, centerY);
        bounceAnimator.setDuration(600);
        bounceAnimator.setInterpolator(new BounceInterpolator());

        AnimatorSet dropSet = new AnimatorSet();
        dropSet.playSequentially(dropAnimator, bounceAnimator);
        dropSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startContinuousAnimations();
            }
        });

        dropSet.start();
    }

    private void startContinuousAnimations() {
        if (volleyballView == null) return;

        // Rotation
        ObjectAnimator rotation = ObjectAnimator.ofFloat(volleyballView, "rotation", 0f, 360f);
        rotation.setDuration(rotationSpeed);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRepeatCount(ObjectAnimator.INFINITE);

        // Floating
        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(volleyballView, "translationY", 0, -40);
        floatAnim.setDuration(2000);
        floatAnim.setRepeatCount(ObjectAnimator.INFINITE);
        floatAnim.setRepeatMode(ObjectAnimator.REVERSE);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        currentAnimator = new AnimatorSet();
        currentAnimator.playTogether(rotation, floatAnim);
        currentAnimator.start();
    }

    private void startExitAnimation() {
        if (volleyballView == null || currentAnimator == null) return;

        // Stop current animations
        currentAnimator.cancel();

        // Get current position
        float currentX = volleyballView.getX();
        float currentY = volleyballView.getY();

        // Calculate exit position
        Point exitPoint = calculateExitPoint();

        // Create exit animation
        AnimatorSet exitAnimatorSet = new AnimatorSet();

        // Movement
        ObjectAnimator exitX = ObjectAnimator.ofFloat(volleyballView, "x", currentX, exitPoint.x);
        ObjectAnimator exitY = ObjectAnimator.ofFloat(volleyballView, "y", currentY, exitPoint.y);

        // Rotation during exit
        ObjectAnimator exitRotation = ObjectAnimator.ofFloat(
                volleyballView, "rotation",
                volleyballView.getRotation(), volleyballView.getRotation() + 720
        );

        // Scale down
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(volleyballView, "scaleX", 1f, 0.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(volleyballView, "scaleY", 1f, 0.3f);

        exitAnimatorSet.playTogether(exitX, exitY, exitRotation, scaleX, scaleY);
        exitAnimatorSet.setDuration(1200);
        exitAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        exitAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeVolleyball();
                if (listener != null) {
                    listener.onVolleyballExited();
                }
            }
        });

        exitAnimatorSet.start();
    }

    private Point calculateExitPoint() {
        int screenWidth = container.getWidth();
        int screenHeight = container.getHeight();
        int ballWidth = volleyballView.getWidth();
        int ballHeight = volleyballView.getHeight();

        switch (exitCorner) {
            case "top_left":
                return new Point(-ballWidth, -ballHeight);
            case "bottom_right":
                return new Point(screenWidth, screenHeight);
            case "bottom_left":
                return new Point(-ballWidth, screenHeight);
            default: // top_right
                return new Point(screenWidth, -ballHeight);
        }
    }

    private void removeVolleyball() {
        if (volleyballView != null) {
            container.removeView(volleyballView);
            volleyballView = null;
        }

        // Save dismissal preference
        if (showOnlyOnce) {
            SharedPreferences prefs = activity.getSharedPreferences("volleyball_prefs", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("volleyball_dismissed", true);
            editor.apply();
        }
    }

    private int dpToPx(int dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void reset() {
        SharedPreferences prefs = activity.getSharedPreferences("volleyball_prefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("volleyball_dismissed", false);
        editor.apply();
    }

    public void destroy() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
            currentAnimator = null;
        }

        if (volleyballView != null) {
            container.removeView(volleyballView);
            volleyballView = null;
        }
    }
}