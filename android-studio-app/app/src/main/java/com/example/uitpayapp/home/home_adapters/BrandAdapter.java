package com.example.uitpayapp.home.home_adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.Restaurant;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    public interface OnBrandClickListener {
        void onBrandClick(Restaurant restaurant);
    }

    private List<Restaurant> restaurants;
    private final OnBrandClickListener listener;

    public BrandAdapter(List<Restaurant> restaurants, OnBrandClickListener listener) {
        this.restaurants = new ArrayList<>(restaurants);
        this.listener = listener;
    }

    public void updateData(List<Restaurant> newData) {
        this.restaurants = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.tvName.setText(restaurant.getName());

        holder.ivImage.clearAnimation();
        String imageUrl = restaurant.getImageUrl();
        ColorDrawable grayPlaceholder = new ColorDrawable(Color.parseColor("#E0E0E0"));

        if (imageUrl != null && !imageUrl.isEmpty()) {
            AlphaAnimation blinkAnimation = new AlphaAnimation(0.5f, 1.0f);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatMode(Animation.REVERSE);
            blinkAnimation.setRepeatCount(Animation.INFINITE);
            holder.ivImage.startAnimation(blinkAnimation);

            RequestOptions options = new RequestOptions().placeholder(grayPlaceholder);
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.ivImage.clearAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            holder.ivImage.clearAnimation();
                            return false;
                        }
                    })
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageDrawable(grayPlaceholder);
        }

        // Bind star rating
        if (holder.tvRating != null) {
            holder.tvRating.setText(String.valueOf(restaurant.getRating()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBrandClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() { return restaurants.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvRating;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_brand_image);
            tvName = itemView.findViewById(R.id.tv_brand_name);
            tvRating = itemView.findViewById(R.id.tv_brand_rating);
        }
    }
}
