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
        int resId = food.getImageResId();
        if (resId == 0) {
            String name = food.getName() != null ? food.getName().toLowerCase() : "";
            if (name.contains("trà sữa") || name.contains("thái") || name.contains("trà đào")) resId = R.drawable.img_food_bubbletea;
            else if (name.contains("cà phê") || name.contains("cafe") || name.contains("phin")) resId = R.drawable.img_food_coffee;
            else if (name.contains("pizza")) resId = R.drawable.img_food_pizza;
            else resId = R.drawable.img_food_chicken;
        }
        holder.ivImage.setImageResource(resId);

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
