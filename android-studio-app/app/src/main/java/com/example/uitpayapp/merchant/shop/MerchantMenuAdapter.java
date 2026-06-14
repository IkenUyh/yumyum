package com.example.uitpayapp.merchant.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class MerchantMenuAdapter extends RecyclerView.Adapter<MerchantMenuAdapter.CategoryViewHolder> {

    private List<MerchantMenuCategory> categories;

    public MerchantMenuAdapter(List<MerchantMenuCategory> categories) {
        this.categories = categories;
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
        holder.container.removeAllViews();

        for (MerchantMenuItem item : category.getItems()) {
            View itemView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_merchant_menu_dish, holder.container, false);
            TextView tvName = itemView.findViewById(R.id.tv_food_name);
            TextView tvPrice = itemView.findViewById(R.id.tv_food_price);
            SwitchMaterial swEnabled = itemView.findViewById(R.id.sw_enabled);
            
            tvName.setText(item.getName());
            tvPrice.setText(String.format("%,.0fđ", item.getPrice()));
            swEnabled.setChecked(item.isEnabled());
            
            swEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> item.setEnabled(isChecked));
            
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
