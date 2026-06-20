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

public class StoreMenuFoodAdapter extends RecyclerView.Adapter<StoreMenuFoodAdapter.ViewHolder> {
    private final List<FoodMenuItem> foods;
    private final OnAddToCartListener listener;

    public interface OnAddToCartListener {
        void onAddToCart(FoodMenuItem item);
    }

    public StoreMenuFoodAdapter(List<FoodMenuItem> foods, OnAddToCartListener listener) {
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_menu_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodMenuItem item = foods.get(position);
        holder.tvFoodName.setText(item.getName());
        holder.tvFoodDesc.setText(item.getDescription());
        holder.tvFoodPrice.setText(item.getFormattedPrice());
        com.example.uitpayapp.utils.ImageLoadHelper.loadImageWithFlashingPlaceholder(holder.ivFoodImage, item.getImageUrl());
        
        int sold = 50 + (item.getName().hashCode() % 800);
        int likes = 1 + (item.getName().hashCode() % 50);
        holder.tvFoodStats.setText(Math.abs(sold) + " đã bán | " + Math.abs(likes) + " lượt thích");

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
        TextView tvFoodDesc;
        TextView tvFoodPrice;
        TextView tvFoodStats;
        ImageView btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodDesc = itemView.findViewById(R.id.tv_food_desc);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvFoodStats = itemView.findViewById(R.id.tv_food_stats);
            btnAdd = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
