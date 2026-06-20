package com.example.uitpayapp.utils;

import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.uitpayapp.R;

public class ImageLoadHelper {

    public static void loadImageWithFlashingPlaceholder(ImageView imageView, String url) {
        if (imageView == null) return;

        // Start flashing animation
        AlphaAnimation blinkAnimation = new AlphaAnimation(0.5f, 1.0f);
        blinkAnimation.setDuration(500);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(blinkAnimation);

        android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0"));

        if (url != null && !url.isEmpty() && url.startsWith("http")) {
            com.bumptech.glide.request.RequestOptions options = new com.bumptech.glide.request.RequestOptions().placeholder(grayPlaceholder).error(grayPlaceholder);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            imageView.clearAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageView.clearAnimation();
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            // No URL provided, clear animation immediately and show static placeholder
            imageView.clearAnimation();
            imageView.setImageDrawable(grayPlaceholder);
        }
    }
}
