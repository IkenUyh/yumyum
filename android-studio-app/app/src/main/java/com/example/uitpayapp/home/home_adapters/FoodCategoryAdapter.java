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

    public void updateData(List<FoodCategory> newCategories) {
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
                .inflate(R.layout.item_food_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodCategory category = categories.get(position);
        holder.tvName.setText(category.getName());

        // Always show icon
        int resId = category.getIconResId();
        int bgColor = category.getBgColor();

        if (resId == 0 || bgColor == 0) {
            String name = category.getName() != null ? category.getName().trim().toLowerCase() : "";
            if (name.contains("cơm")) {
                if (resId == 0) resId = R.drawable.ic_cat_com;
                if (bgColor == 0) bgColor = Color.parseColor("#E65100");
            } else if (name.contains("bún") || name.contains("phở")) {
                if (resId == 0) resId = R.drawable.ic_cat_bun_pho;
                if (bgColor == 0) bgColor = Color.parseColor("#00838F");
            } else if (name.contains("bánh mì") || name.contains("bánh mỳ")) {
                if (resId == 0) resId = R.drawable.ic_cat_banh_mi;
                if (bgColor == 0) bgColor = Color.parseColor("#BF360C");
            } else if (name.contains("fastfood") || name.contains("nhanh")) {
                if (resId == 0) resId = R.drawable.ic_cat_fastfood;
                if (bgColor == 0) bgColor = Color.parseColor("#C62828");
            } else if (name.contains("lẩu")) {
                if (resId == 0) resId = R.drawable.ic_cat_lau;
                if (bgColor == 0) bgColor = Color.parseColor("#D84315");
            } else if (name.contains("nướng") || name.contains("bbq")) {
                if (resId == 0) resId = R.drawable.ic_cat_bbq;
                if (bgColor == 0) bgColor = Color.parseColor("#B71C1C");
            } else if (name.contains("cafe") || name.contains("cà phê")) {
                if (resId == 0) resId = R.drawable.ic_cat_ca_phe;
                if (bgColor == 0) bgColor = Color.parseColor("#4E342E");
            } else if (name.contains("trà sữa")) {
                if (resId == 0) resId = R.drawable.ic_cat_tra_sua;
                if (bgColor == 0) bgColor = Color.parseColor("#8D6E63");
            } else if (name.contains("ăn vặt") || name.contains("bánh ngọt")) {
                if (resId == 0) resId = R.drawable.ic_cat_an_vat;
                if (bgColor == 0) bgColor = Color.parseColor("#6A1B9A");
            } else {
                if (resId == 0) resId = R.drawable.ic_cat_all;
                if (bgColor == 0) bgColor = Color.parseColor("#283593");
            }
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
            holder.ivIcon.setColorFilter(bgColor);
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
