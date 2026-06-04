package com.example.uitpayapp.home.home_adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.FoodCategory;

import java.util.List;

public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(FoodCategory category);
    }

    private final List<FoodCategory> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public FoodCategoryAdapter(List<FoodCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void clearSelection() {
        int old = selectedPosition;
        selectedPosition = -1;
        if (old >= 0) notifyItemChanged(old);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodCategory category = categories.get(position);
        holder.tvName.setText(category.getName());

        // Switch between icon and emoji
        if (category.hasIcon()) {
            holder.tvEmoji.setVisibility(View.GONE);
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.ivIcon.setImageResource(category.getIconResId());
        } else {
            holder.tvEmoji.setVisibility(View.VISIBLE);
            holder.ivIcon.setVisibility(View.GONE);
            holder.tvEmoji.setText(category.getEmoji());
        }

        // Xử lý item "Tất cả"
        if (category.isSelectAll()) {
            holder.viewBg.setBackgroundTintList(ColorStateList.valueOf(category.getBgColor()));
            if (category.hasIcon()) {
                holder.ivIcon.setColorFilter(Color.parseColor("#5C6BC0"));
            } else {
                holder.tvEmoji.setTextSize(22f);
                holder.tvEmoji.setTypeface(null, Typeface.BOLD);
            }
            holder.tvName.setTextColor(Color.parseColor("#5C6BC0"));
            holder.tvName.setTextSize(11f);
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(null);
            return;
        }

        if (!category.hasIcon()) {
            holder.tvEmoji.setTextSize(28f);
            holder.tvEmoji.setTypeface(null, Typeface.NORMAL);
        }

        boolean isSelected = position == selectedPosition;
        holder.viewBg.setBackgroundTintList(ColorStateList.valueOf(category.getBgColor()));

        if (isSelected) {
            holder.tvName.setTextColor(Color.parseColor("#0034c8"));
            holder.tvName.setTextSize(12.5f);
            holder.itemView.setAlpha(1.0f);
            if (category.hasIcon()) {
                holder.ivIcon.setColorFilter(Color.parseColor("#0034c8"));
            }
        } else {
            holder.tvName.setTextColor(Color.parseColor("#333333"));
            holder.tvName.setTextSize(12f);
            holder.itemView.setAlpha(selectedPosition == -1 ? 1.0f : 0.6f);
            if (category.hasIcon()) {
                holder.ivIcon.setColorFilter(Color.parseColor("#757575"));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            if (selectedPosition == holder.getAdapterPosition()) {
                selectedPosition = -1;
            } else {
                selectedPosition = holder.getAdapterPosition();
            }
            if (oldPos >= 0) notifyItemChanged(oldPos);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) {
                listener.onCategoryClick(selectedPosition == -1 ? null : category);
            }
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewBg;
        TextView tvEmoji, tvName;
        ImageView ivIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewBg = itemView.findViewById(R.id.view_category_bg);
            tvEmoji = itemView.findViewById(R.id.tv_category_emoji);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
