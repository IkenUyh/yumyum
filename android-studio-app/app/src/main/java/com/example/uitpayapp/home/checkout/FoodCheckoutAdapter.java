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
        holder.ivFoodImage.setImageResource(item.getMenuItem().getImageResId());
        
        holder.tvFoodQuantity.setText("Số lượng: " + item.getQuantity());
        
        long totalPrice = item.getTotalPrice();
        holder.tvFoodPrice.setText(String.format("%,dđ", totalPrice).replace(',', '.'));
        
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
        TextView tvFoodToppings;
        TextView tvFoodQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvFoodToppings = itemView.findViewById(R.id.tv_food_toppings);
            tvFoodQuantity = itemView.findViewById(R.id.tv_food_quantity);
        }
    }
}
