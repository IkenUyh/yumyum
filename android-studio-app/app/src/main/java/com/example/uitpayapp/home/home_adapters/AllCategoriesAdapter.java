package com.example.uitpayapp.home.home_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.food.models.responses.CategoryFoodCountResponseDTO;

import java.util.List;

public class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryFoodCountResponseDTO category);
    }

    private final List<CategoryFoodCountResponseDTO> categories;
    private final OnCategoryClickListener listener;

    public AllCategoriesAdapter(List<CategoryFoodCountResponseDTO> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void updateData(List<CategoryFoodCountResponseDTO> newCategories) {
        this.categories.clear();
        if (newCategories != null) {
            this.categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_category_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryFoodCountResponseDTO category = categories.get(position);
        
        holder.tvNameCount.setText(category.getCategoryName() + " (" + category.getFoodCount() + ")");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCount = itemView.findViewById(R.id.tv_category_name_count);
        }
    }
}
