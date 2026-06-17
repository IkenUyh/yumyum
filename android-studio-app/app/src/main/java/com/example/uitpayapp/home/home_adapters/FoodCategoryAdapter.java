package com.example.uitpayapp.home.home_adapters;

import android.graphics.Color;
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

        // Always show icon
        int resId = category.getIconResId();
        if (resId == 0) {
            String name = category.getName() != null ? category.getName().trim() : "";
            if (name.contains("Cơm")) resId = R.drawable.ic_cat_com;
            else if (name.contains("Bún") || name.contains("Phở")) resId = R.drawable.ic_cat_bun_pho;
            else if (name.contains("Bánh mì")) resId = R.drawable.ic_cat_banh_mi;
            else if (name.contains("Fastfood") || name.contains("nhanh")) resId = R.drawable.ic_cat_fastfood;
            else if (name.contains("Lẩu")) resId = R.drawable.ic_cat_lau;
            else if (name.contains("nướng")) resId = R.drawable.ic_cat_bbq;
            else if (name.contains("Cafe") || name.contains("Cà phê")) resId = R.drawable.ic_cat_ca_phe;
            else if (name.contains("Trà sữa")) resId = R.drawable.ic_cat_tra_sua;
            else if (name.contains("Ăn vặt") || name.contains("bánh ngọt")) resId = R.drawable.ic_cat_an_vat;
            else resId = R.drawable.ic_cat_all;
        }
        holder.ivIcon.setImageResource(resId);

        // Xử lý item "Danh mục"
        if (category.isSelectAll()) {
            holder.ivIcon.setColorFilter(Color.parseColor("#5C6BC0"));
            holder.tvName.setTextColor(Color.parseColor("#5C6BC0"));
            holder.tvName.setTextSize(11f);
            holder.itemView.setAlpha(1.0f);

            int padding = (int) (6 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.ivIcon.setPadding(padding, padding, padding, padding);
        } else {
            // Reset padding cho các mục bình thường
            holder.ivIcon.setPadding(0, 0, 0, 0);
            holder.tvName.setTextColor(Color.parseColor("#333333"));
            holder.tvName.setTextSize(12f);
            holder.itemView.setAlpha(1.0f);
            holder.ivIcon.setColorFilter(category.getBgColor());
        }

        // Tất cả các category đều có thể click, không đổi màu
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
