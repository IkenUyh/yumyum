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

/**
 * Simple adapter for category food list.
 * Displays food name, description, price. Clicking opens a popup via listener.
 * Designed to be modular and easy to swap with API-backed data source later.
 */
public class CategoryFoodAdapter extends RecyclerView.Adapter<CategoryFoodAdapter.ViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(FoodMenuItem item, ImageView imageView);
    }

    private final List<FoodMenuItem> foods;
    private final OnFoodClickListener listener;

    public CategoryFoodAdapter(List<FoodMenuItem> foods, OnFoodClickListener listener) {
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodMenuItem item = foods.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDescription());
        holder.tvPrice.setText(item.getFormattedPrice());

        // Ưu tiên load ảnh từ URL (server), fallback sang imageResId (local)
        holder.ivImage.clearAnimation();
        String imageUrl = item.getImageUrl();
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
            
            if (item.getImageResId() != 0) {
                request = request.error(item.getImageResId());
            } else {
                request = request.error(grayPlaceholder);
            }
            request.into(holder.ivImage);
        } else if (item.getImageResId() != 0) {
            holder.ivImage.setImageResource(item.getImageResId());
        } else {
            holder.ivImage.setImageDrawable(grayPlaceholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodClick(item, holder.ivImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDesc, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_food_image);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvDesc = itemView.findViewById(R.id.tv_food_desc);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
        }
    }
}
