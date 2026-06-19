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
        holder.ivImage.clearAnimation();
        String imageUrl = item.getImageUrl();
        android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0"));
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            android.view.animation.AlphaAnimation blinkAnimation = new android.view.animation.AlphaAnimation(0.5f, 1.0f);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
            blinkAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
            holder.ivImage.startAnimation(blinkAnimation);

            com.bumptech.glide.RequestBuilder<android.graphics.drawable.Drawable> request = com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(grayPlaceholder)
                    .centerCrop()
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            holder.ivImage.clearAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            holder.ivImage.clearAnimation();
                            return false;
                        }
                    });
            
            if (item.getImageResId() != 0) {
                request = request.error(item.getImageResId());
            } else {
                request = request.error(grayPlaceholder);
            }
            request.into(holder.ivImage);
        } else if (item.getImageResId() != 0) {
            holder.ivImage.setImageResource(item.getImageResId());
        } else {
            holder.ivImage.setImageDrawable(grayPlaceholder);
        }

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
        // Find existing cart item to pre-fill
        CartItem existingItem = null;
        for (CartItem ci : cart) {
            if (ci.getMenuItem().getName().equals(item.getName())) {
                existingItem = ci;
                break;
            }
        }

        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(context, item, existingItem, (selectedItem, quantity, selectedToppings) -> {
            // Update cart locally for the adapter
            boolean found = false;
            for (CartItem ci : cart) {
                if (ci.getMenuItem().getName().equals(selectedItem.getName())) {
                    ci.setQuantity(quantity);
                    ci.setSelectedToppings(selectedToppings);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(selectedItem, quantity, selectedToppings));
            }
            
            int newQty = getQuantityForItem(item);
            adapterHolder.tvQuantity.setText(String.valueOf(newQty));
            adapterHolder.btnDecrease.setTextColor(newQty > 0 ? Color.parseColor("#D32F2F") : Color.parseColor("#BDBDBD"));
            if (listener != null) listener.onCartChanged(cart);

            if (context instanceof android.app.Activity) {
                android.app.Activity activity = (android.app.Activity) context;
                View btnCart = activity.findViewById(R.id.btn_cart);
                if (btnCart != null) {
                    com.example.uitpayapp.utils.CartAnimationHelper.animateFlyToCart(activity, adapterHolder.ivImage, btnCart, null);
                }
            }
        });
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
