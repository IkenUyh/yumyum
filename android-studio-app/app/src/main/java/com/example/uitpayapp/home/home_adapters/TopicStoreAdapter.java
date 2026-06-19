package com.example.uitpayapp.home.home_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.FoodMenuItem;

import java.util.List;

public class TopicStoreAdapter extends RecyclerView.Adapter<TopicStoreAdapter.ViewHolder> {

    public interface OnTopicFoodClickListener {
        void onFoodClick(FoodMenuItem item, ViewHolder holder);
    }

    private final List<FoodMenuItem> foods;
    private final OnTopicFoodClickListener listener;

    public TopicStoreAdapter(List<FoodMenuItem> foods, OnTopicFoodClickListener listener) {
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodMenuItem food = foods.get(position);
        holder.tvName.setText(food.getName());
        holder.tvPrice.setText(food.getFormattedPrice());
        
        holder.ivImage.clearAnimation();
        String imageUrl = food.getImageUrl();
        android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0"));
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            android.view.animation.AlphaAnimation blinkAnimation = new android.view.animation.AlphaAnimation(0.5f, 1.0f);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
            blinkAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
            holder.ivImage.startAnimation(blinkAnimation);

            com.bumptech.glide.RequestBuilder<android.graphics.drawable.Drawable> request = com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(grayPlaceholder)
                    .centerCrop()
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            holder.ivImage.clearAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            holder.ivImage.clearAnimation();
                            return false;
                        }
                    });
            
            if (food.getImageResId() != 0) {
                request = request.error(food.getImageResId());
            } else {
                request = request.error(grayPlaceholder);
            }
            request.into(holder.ivImage);
        } else if (food.getImageResId() != 0) {
            holder.ivImage.setImageResource(food.getImageResId());
        } else {
            holder.ivImage.setImageDrawable(grayPlaceholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFoodClick(food, holder);
        });
    }

    @Override
    public int getItemCount() {
        return foods != null ? foods.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        TextView tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_topic_store_image);
            tvName = itemView.findViewById(R.id.tv_topic_food_name);
            tvPrice = itemView.findViewById(R.id.tv_topic_food_price);
        }
    }
}
