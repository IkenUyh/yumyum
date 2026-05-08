package com.example.uitpayapp.home.food_order;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

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
        holder.tvInitial.setText(restaurant.getShortName());
        holder.tvName.setText(restaurant.getName());

        // Set brand color as circle background
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(16 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
        bg.setColor(restaurant.getBgColor());
        holder.viewBg.setBackground(bg);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBrandClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() { return restaurants.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewBg;
        TextView tvInitial, tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewBg = itemView.findViewById(R.id.view_brand_bg);
            tvInitial = itemView.findViewById(R.id.tv_brand_initial);
            tvName = itemView.findViewById(R.id.tv_brand_name);
        }
    }
}
