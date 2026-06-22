package com.example.uitpayapp.home.checkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.CartItem;

import java.util.List;

public class FoodCheckoutAdapter extends RecyclerView.Adapter<FoodCheckoutAdapter.ViewHolder> {

    private List<CartItem> cartItems;

    public FoodCheckoutAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        holder.tvFoodName.setText(item.getMenuItem().getName());
        String url = item.getMenuItem().getImageUrl();
        com.example.uitpayapp.utils.ImageLoadHelper.loadImageWithFlashingPlaceholder(holder.ivFoodImage, url);
        
        holder.tvFoodQuantity.setText("Số lượng: " + item.getQuantity());
        
        long totalPrice = item.getTotalPrice();
        holder.tvFoodPrice.setText(String.format("%,dđ", totalPrice).replace(',', '.'));

        if (item.getMenuItem().getOriginalPrice() > 0 && item.getMenuItem().getOriginalPrice() > item.getMenuItem().getPrice()) {
            if (holder.tvOriginalPrice != null) {
                holder.tvOriginalPrice.setVisibility(View.VISIBLE);
                
                // Similarly to cart, total original price = (original base price + toppings) * quantity
                long unitPrice = item.getTotalPrice() / item.getQuantity();
                long unitOriginalPrice = item.getMenuItem().getOriginalPrice() + (unitPrice - item.getMenuItem().getPrice());
                long totalOriginalPrice = unitOriginalPrice * item.getQuantity();
                
                holder.tvOriginalPrice.setText(String.format("%,dđ", totalOriginalPrice).replace(',', '.'));
                holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (holder.tvDiscountTag != null) {
                holder.tvDiscountTag.setVisibility(View.VISIBLE);
                if (item.getMenuItem().getDiscountType() != null && !item.getMenuItem().getDiscountType().isEmpty()) {
                    holder.tvDiscountTag.setText(item.getMenuItem().getDiscountType());
                } else if (item.getMenuItem().getDiscountPercent() > 0) {
                    holder.tvDiscountTag.setText("-" + item.getMenuItem().getDiscountPercent() + "%");
                } else {
                    holder.tvDiscountTag.setVisibility(View.GONE);
                }
            }
        } else {
            if (holder.tvOriginalPrice != null) holder.tvOriginalPrice.setVisibility(View.GONE);
            if (holder.tvDiscountTag != null) holder.tvDiscountTag.setVisibility(View.GONE);
        }
        
        String toppings = item.getToppingsString();
        if (toppings.isEmpty()) {
            holder.tvFoodToppings.setVisibility(View.GONE);
        } else {
            holder.tvFoodToppings.setVisibility(View.VISIBLE);
            holder.tvFoodToppings.setText("Topping: " + toppings);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName;
        TextView tvFoodPrice;
        TextView tvOriginalPrice;
        TextView tvDiscountTag;
        TextView tvFoodToppings;
        TextView tvFoodQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvDiscountTag = itemView.findViewById(R.id.tv_discount_tag);
            tvFoodToppings = itemView.findViewById(R.id.tv_food_toppings);
            tvFoodQuantity = itemView.findViewById(R.id.tv_food_quantity);
        }
    }
}
