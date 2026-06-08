package com.example.uitpayapp.home.home_adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

        holder.itemView.setOnClickListener(v -> {
            showFoodItemDetailPopup(holder.itemView.getContext(), item, holder);
        });

        holder.btnIncrease.setOnClickListener(v -> {
            showFoodItemDetailPopup(holder.itemView.getContext(), item, holder);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            removeFromCart(item);
            int qty = getQuantityForItem(item);
            holder.tvQuantity.setText(String.valueOf(qty));
            holder.btnDecrease.setTextColor(qty > 0 ? Color.parseColor("#D32F2F") : Color.parseColor("#BDBDBD"));
            if (listener != null) listener.onCartChanged(cart);
        });
    }

    private void showFoodItemDetailPopup(Context context, FoodMenuItem item, ViewHolder adapterHolder) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_food_detail, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        ImageView ivFoodImage = view.findViewById(R.id.iv_food_image);
        TextView tvFoodName = view.findViewById(R.id.tv_food_name);
        TextView tvFoodDesc = view.findViewById(R.id.tv_food_desc);
        TextView tvFoodPrice = view.findViewById(R.id.tv_food_price);
        ImageView btnClose = view.findViewById(R.id.btn_close);

        ivFoodImage.setImageResource(item.getImageResId());
        tvFoodName.setText(item.getName());
        tvFoodDesc.setText(item.getDescription());
        tvFoodPrice.setText(item.getFormattedPrice());

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Add mock toppings
        LinearLayout layoutToppings = view.findViewById(R.id.layout_toppings_container);
        String[] mockToppings = {"Thêm trân châu đen", "Thêm phô mai", "Thêm thạch mảng cầu", "Không đá", "Ít đường"};
        String[] mockPrices = {"+5.000đ", "+10.000đ", "+5.000đ", "0đ", "0đ"};

        for (int i = 0; i < 3; i++) {
            View toppingView = LayoutInflater.from(context).inflate(R.layout.item_food_topping, layoutToppings, false);
            android.widget.CheckBox cbTopping = toppingView.findViewById(R.id.cb_topping);
            TextView tvToppingPrice = toppingView.findViewById(R.id.tv_topping_price);
            cbTopping.setText(mockToppings[i]);
            tvToppingPrice.setText(mockPrices[i]);
            layoutToppings.addView(toppingView);
        }

        TextView btnDecrease = view.findViewById(R.id.btn_decrease);
        TextView btnIncrease = view.findViewById(R.id.btn_increase);
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);

        // We use an array to hold the mutable quantity selected in the popup
        final int[] popupQty = {Math.max(1, getQuantityForItem(item))};
        tvQuantity.setText(String.valueOf(popupQty[0]));

        btnDecrease.setOnClickListener(v -> {
            if (popupQty[0] > 1) {
                popupQty[0]--;
                tvQuantity.setText(String.valueOf(popupQty[0]));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            popupQty[0]++;
            tvQuantity.setText(String.valueOf(popupQty[0]));
        });

        btnAddToCart.setOnClickListener(v -> {
            // Add or update cart
            setQuantityForCartItem(item, popupQty[0]);
            int newQty = getQuantityForItem(item);
            adapterHolder.tvQuantity.setText(String.valueOf(newQty));
            adapterHolder.btnDecrease.setTextColor(newQty > 0 ? Color.parseColor("#D32F2F") : Color.parseColor("#BDBDBD"));
            if (listener != null) listener.onCartChanged(cart);

            if (context instanceof android.app.Activity) {
                android.app.Activity activity = (android.app.Activity) context;
                View btnCart = activity.findViewById(R.id.btn_cart);
                if (btnCart != null) {
                    com.example.uitpayapp.utils.CartAnimationHelper.animateFlyToCart(activity, ivFoodImage, btnCart, null);
                }
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setQuantityForCartItem(FoodMenuItem item, int quantity) {
        for (CartItem ci : cart) {
            if (ci.getMenuItem().getName().equals(item.getName())) {
                ci.setQuantity(quantity);
                return;
            }
        }
        cart.add(new CartItem(item, quantity));
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
