package com.example.uitpayapp.home.home_adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class AllBrandsAdapter extends RecyclerView.Adapter<AllBrandsAdapter.ViewHolder> {

    public interface OnBrandClickListener {
        void onBrandClick(Restaurant restaurant);
    }

    private List<Restaurant> restaurants;
    private final OnBrandClickListener listener;

    public AllBrandsAdapter(List<Restaurant> restaurants, OnBrandClickListener listener) {
        this.restaurants = new ArrayList<>(restaurants);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.tvInitial.setText(restaurant.getShortName());
        holder.tvName.setText(restaurant.getName());
        holder.tvRating.setText(String.valueOf(restaurant.getRating()));
        holder.tvCategory.setText(restaurant.getCategory());

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

    public void updateData(List<Restaurant> newRestaurants) {
        this.restaurants.clear();
        this.restaurants.addAll(newRestaurants);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return restaurants.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial;
        TextView tvName;
        TextView tvRating;
        TextView tvCategory;
        View viewBg;

        ViewHolder(View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tv_brand_initial);
            tvName = itemView.findViewById(R.id.tv_brand_name);
            tvRating = itemView.findViewById(R.id.tv_brand_rating);
            tvCategory = itemView.findViewById(R.id.tv_brand_category);
            viewBg = itemView.findViewById(R.id.view_brand_bg);
        }
    }
}
