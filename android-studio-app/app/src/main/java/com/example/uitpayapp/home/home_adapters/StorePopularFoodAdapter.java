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
        holder.ivFoodImage.setImageResource(item.getImageResId());
        
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
        TextView tvSoldCountBadge;
        ImageView btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvSoldCountBadge = itemView.findViewById(R.id.tv_sold_count_badge);
            btnAdd = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
