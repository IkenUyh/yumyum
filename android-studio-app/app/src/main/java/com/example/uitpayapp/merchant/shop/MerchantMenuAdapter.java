package com.example.uitpayapp.merchant.shop;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuCategory;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class MerchantMenuAdapter extends RecyclerView.Adapter<MerchantMenuAdapter.CategoryViewHolder> {
    public interface OnFoodStatusChangeListener {
        void onFoodStatusChanged(Long foodId, boolean isAvailable);
    }

    private List<MerchantMenuCategory> categories;
    private boolean isToppingMode = false;
    private OnFoodStatusChangeListener statusChangeListener;

    public MerchantMenuAdapter(List<MerchantMenuCategory> categories) {
        this.categories = categories;
    }

    public MerchantMenuAdapter(List<MerchantMenuCategory> categories, boolean isToppingMode) {
        this.categories = categories;
        this.isToppingMode = isToppingMode;
    }

    public void setOnFoodStatusChangeListener(OnFoodStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    public void updateList(List<MerchantMenuCategory> newList) {
        this.categories = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_profile_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MerchantMenuCategory category = categories.get(position);
        holder.tvTitle.setText(category.getCategoryName());
        holder.tvTitle.setAllCaps(true);

        holder.tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddMerchantCategoryActivity.class);
            intent.putExtra("is_edit_mode", true);
            intent.putExtra("category_name", category.getCategoryName());
            intent.putExtra("is_topping_group", isToppingMode); // Nếu đang ở chế độ topping thì là nhóm topping
            v.getContext().startActivity(intent);
        });

        holder.container.removeAllViews();

        for (MerchantMenuItem item : category.getItems()) {
            View itemView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_merchant_menu_dish, holder.container, false);
            ImageView ivImage = itemView.findViewById(R.id.iv_food_image);
            TextView tvName = itemView.findViewById(R.id.tv_food_name);
            TextView tvPrice = itemView.findViewById(R.id.tv_food_price);
            SwitchMaterial swEnabled = itemView.findViewById(R.id.sw_enabled);
            TextView tvEdit = itemView.findViewById(R.id.tv_edit);

            itemView.setOnClickListener(v -> {
                if (isToppingMode) {
                    Intent intent = new Intent(v.getContext(), AddMerchantToppingActivity.class);
                    intent.putExtra("is_edit_mode", true);
                    intent.putExtra("topping_data", item);
                    intent.putExtra("category_name", category.getCategoryName());
                    v.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), AddMerchantDishActivity.class);
                    intent.putExtra("is_edit_mode", true);
                    intent.putExtra("dish_data", item);
                    intent.putExtra("category_name", category.getCategoryName());
                    v.getContext().startActivity(intent);
                }
            });

            // Load image using Glide if imageUrl is available, otherwise use drawable imageRes, else hide
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                ivImage.setVisibility(View.VISIBLE);
                com.bumptech.glide.Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.yumyum_demo_logo)
                        .into(ivImage);
            } else if (item.getImageRes() != 0) {
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setImageResource(item.getImageRes());
            } else {
                ivImage.setVisibility(View.GONE);
            }

            tvName.setText(item.getName());
            tvPrice.setText(String.format("%,.0fđ", item.getPrice()));

            if (isToppingMode) {
                swEnabled.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                tvEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), AddMerchantToppingActivity.class);
                    intent.putExtra("is_edit_mode", true);
                    intent.putExtra("topping_data", item);
                    intent.putExtra("category_name", category.getCategoryName());
                    v.getContext().startActivity(intent);
                });
            } else {
                swEnabled.setVisibility(View.VISIBLE);
                tvEdit.setVisibility(View.GONE);
                
                // Cập nhật trạng thái hiện tại (bỏ listener cũ trước khi set để tránh loop khi tái sử dụng view)
                swEnabled.setOnCheckedChangeListener(null);
                swEnabled.setChecked(item.isEnabled());
                
                swEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    item.setEnabled(isChecked);
                    if (statusChangeListener != null) {
                        statusChangeListener.onFoodStatusChanged(item.getId(), isChecked);
                    }
                });
            }
            
            holder.container.addView(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        LinearLayout container;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.group_title);
            container = itemView.findViewById(R.id.items_container);
        }
    }
}
