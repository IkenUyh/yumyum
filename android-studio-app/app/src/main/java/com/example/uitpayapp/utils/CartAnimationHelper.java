package com.example.uitpayapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

public class CartAnimationHelper {

    public static void animateFlyToCart(Activity activity, View startView, View endView, final Runnable onAnimationEnd) {
        if (activity == null || startView == null || endView == null) {
            if (onAnimationEnd != null) onAnimationEnd.run();
            return;
        }

        // Get coordinates
        int[] startLoc = new int[2];
        startView.getLocationOnScreen(startLoc);

        int[] endLoc = new int[2];
        endView.getLocationOnScreen(endLoc);

        // Create an animated view overlay
        final ImageView animatedView = new ImageView(activity);
        
        // Try to capture bitmap from startView
        Bitmap bitmap = getBitmapFromView(startView);
        if (bitmap != null) {
            animatedView.setImageBitmap(bitmap);
        } else {
            // fallback
            Drawable background = startView.getBackground();
            if (background != null) {
                animatedView.setBackground(background);
            } else if (startView instanceof ImageView) {
                animatedView.setImageDrawable(((ImageView) startView).getDrawable());
            }
        }

        int startWidth = startView.getWidth();
        int startHeight = startView.getHeight();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(startWidth, startHeight);
        animatedView.setLayoutParams(params);

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(animatedView);

        animatedView.setX(startLoc[0]);
        animatedView.setY(startLoc[1]);

        // Calculate end positions (center of the target view)
        float endX = endLoc[0] + (endView.getWidth() / 2f) - (startWidth / 2f);
        float endY = endLoc[1] + (endView.getHeight() / 2f) - (startHeight / 2f);

        // Create animators
        ObjectAnimator animX = ObjectAnimator.ofFloat(animatedView, "x", startLoc[0], endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(animatedView, "y", startLoc[1], endY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animatedView, "scaleX", 1.0f, 0.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animatedView, "scaleY", 1.0f, 0.1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(animatedView, "alpha", 1.0f, 0.5f);

        animY.setInterpolator(new AccelerateInterpolator());

        // Corner radius animation
        animatedView.setClipToOutline(true);
        final float maxRadius = Math.min(startWidth, startHeight) / 2f;
        android.animation.ValueAnimator radiusAnim = android.animation.ValueAnimator.ofFloat(0f, maxRadius);
        radiusAnim.addUpdateListener(animation -> {
            float currentRadius = (float) animation.getAnimatedValue();
            animatedView.setOutlineProvider(new android.view.ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), currentRadius);
                }
            });
            animatedView.invalidateOutline();
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY, scaleX, scaleY, alpha, radiusAnim);
        animatorSet.setDuration(600);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                decorView.removeView(animatedView);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }
        });
        animatorSet.start();
    }

    private static Bitmap getBitmapFromView(View view) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) return null;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
