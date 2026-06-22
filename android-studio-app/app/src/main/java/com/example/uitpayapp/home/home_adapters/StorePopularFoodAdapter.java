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
import com.bumptech.glide.Glide;

public class StorePopularFoodAdapter extends RecyclerView.Adapter<StorePopularFoodAdapter.ViewHolder> {
    private final List<FoodMenuItem> foods;
    private final OnAddToCartListener listener;

    public interface OnAddToCartListener {
        void onAddToCart(FoodMenuItem item);
    }

    public StorePopularFoodAdapter(List<FoodMenuItem> foods, OnAddToCartListener listener) {
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_popular_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodMenuItem item = foods.get(position);
        holder.tvFoodName.setText(item.getName());
        holder.tvFoodPrice.setText(item.getFormattedPrice());

        if (item.getOriginalPrice() > 0 && item.getOriginalPrice() > item.getPrice()) {
            holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            holder.tvDiscountTag.setVisibility(View.VISIBLE);
            
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            holder.tvOriginalPrice.setText(formatter.format(item.getOriginalPrice()) + "đ");
            holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            
            if (item.getDiscountType() != null && !item.getDiscountType().isEmpty()) {
                holder.tvDiscountTag.setText(item.getDiscountType());
            } else if (item.getDiscountPercent() > 0) {
                holder.tvDiscountTag.setText("-" + item.getDiscountPercent() + "%");
            } else {
                holder.tvDiscountTag.setVisibility(View.GONE);
            }
        } else {
            holder.tvOriginalPrice.setVisibility(View.GONE);
            holder.tvDiscountTag.setVisibility(View.GONE);
        }
        com.example.uitpayapp.utils.ImageLoadHelper.loadImageWithFlashingPlaceholder(holder.ivFoodImage, item.getImageUrl());
        
        // Random badge
        int sold = 100 + (item.getName().hashCode() % 400);
        holder.tvSoldCountBadge.setText(Math.abs(sold) + "+ đã bán");

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(item);
        });
    }

    @Override
    public int getItemCount() {
        return foods != null ? foods.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName;
        TextView tvFoodPrice;
        TextView tvOriginalPrice;
        TextView tvDiscountTag;
        TextView tvSoldCountBadge;
        ImageView btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvDiscountTag = itemView.findViewById(R.id.tv_discount_tag);
            tvSoldCountBadge = itemView.findViewById(R.id.tv_sold_count_badge);
            btnAdd = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
