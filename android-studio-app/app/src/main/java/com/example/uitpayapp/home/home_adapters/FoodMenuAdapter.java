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
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.FoodMenuItem;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.ViewHolder> {

    public interface OnCartChangedListener {
        void onCartChanged(List<CartItem> cart);
    }

    private final List<FoodMenuItem> menuItems;
    private final List<CartItem> cart;
    private final OnCartChangedListener listener;

    public FoodMenuAdapter(List<FoodMenuItem> menuItems, OnCartChangedListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
        this.cart = new ArrayList<>();
    }

    public List<CartItem> getCart() { return cart; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodMenuItem item = menuItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDescription());
        holder.tvPrice.setText(item.getFormattedPrice());
        holder.ivImage.setImageResource(item.getImageResId());

        // Find current quantity in cart
        int currentQty = getQuantityForItem(item);
        holder.tvQuantity.setText(String.valueOf(currentQty));

        // Update decrease button visibility
        holder.btnDecrease.setTextColor(currentQty > 0 ? Color.parseColor("#D32F2F") : Color.parseColor("#BDBDBD"));

        holder.btnIncrease.setOnClickListener(v -> {
            addToCart(item);
            int qty = getQuantityForItem(item);
            holder.tvQuantity.setText(String.valueOf(qty));
            holder.btnDecrease.setTextColor(Color.parseColor("#D32F2F"));
            if (listener != null) listener.onCartChanged(cart);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            removeFromCart(item);
            int qty = getQuantityForItem(item);
            holder.tvQuantity.setText(String.valueOf(qty));
            holder.btnDecrease.setTextColor(qty > 0 ? Color.parseColor("#D32F2F") : Color.parseColor("#BDBDBD"));
            if (listener != null) listener.onCartChanged(cart);
        });
    }

    private int getQuantityForItem(FoodMenuItem item) {
        for (CartItem ci : cart) {
            if (ci.getMenuItem().getName().equals(item.getName())) {
                return ci.getQuantity();
            }
        }
        return 0;
    }

    private void addToCart(FoodMenuItem item) {
        for (CartItem ci : cart) {
            if (ci.getMenuItem().getName().equals(item.getName())) {
                ci.setQuantity(ci.getQuantity() + 1);
                return;
            }
        }
        cart.add(new CartItem(item, 1));
    }

    private void removeFromCart(FoodMenuItem item) {
        for (int i = 0; i < cart.size(); i++) {
            CartItem ci = cart.get(i);
            if (ci.getMenuItem().getName().equals(item.getName())) {
                if (ci.getQuantity() > 1) {
                    ci.setQuantity(ci.getQuantity() - 1);
                } else {
                    cart.remove(i);
                }
                return;
            }
        }
    }

    @Override
    public int getItemCount() { return menuItems.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDesc, tvPrice, tvQuantity, btnIncrease, btnDecrease;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_food_image);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvDesc = itemView.findViewById(R.id.tv_food_desc);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
