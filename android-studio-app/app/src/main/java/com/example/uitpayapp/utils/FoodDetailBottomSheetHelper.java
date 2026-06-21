package com.example.uitpayapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.CartTopping;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.responses.OptionGroupResponse;
import com.example.uitpayapp.modules.food.models.responses.OptionItemResponse;
import com.example.uitpayapp.network.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailBottomSheetHelper {

    public interface OnAddToCartListener {
        void onAddToCart(FoodMenuItem item, int quantity, List<CartTopping> selectedToppings);
    }

    public static void show(Context context, FoodMenuItem item, CartItem existingCartItem, OnAddToCartListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_food_detail, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        ImageView ivFoodImage = view.findViewById(R.id.iv_food_image);
        TextView tvFoodName = view.findViewById(R.id.tv_food_name);
        TextView tvStoreName = view.findViewById(R.id.tv_store_name);
        TextView tvFoodDesc = view.findViewById(R.id.tv_food_desc);
        TextView tvFoodPrice = view.findViewById(R.id.tv_food_price);

        String imageUrl = item.getImageUrl();
        android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(Color.parseColor("#E0E0E0"));
        if (imageUrl != null && !imageUrl.isEmpty()) {
            android.view.animation.AlphaAnimation blinkAnimation = new android.view.animation.AlphaAnimation(0.5f, 1.0f);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
            blinkAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
            ivFoodImage.startAnimation(blinkAnimation);

            com.bumptech.glide.request.RequestOptions options = new com.bumptech.glide.request.RequestOptions().placeholder(grayPlaceholder);
            com.bumptech.glide.Glide.with(context)
                    .load(imageUrl)
                    .apply(options)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            ivFoodImage.clearAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            ivFoodImage.clearAnimation();
                            return false;
                        }
                    })
                    .into(ivFoodImage);
        } else if (item.getImageResId() != 0) {
            ivFoodImage.setImageResource(item.getImageResId());
        } else {
            ivFoodImage.setImageDrawable(grayPlaceholder);
        }
        
        tvFoodName.setText(item.getName());
        tvStoreName = dialog.findViewById(R.id.tv_store_name);
        View layoutStoreInfo = dialog.findViewById(R.id.layout_store_info);
        if (tvStoreName != null && layoutStoreInfo != null) {
            if (item.getRestaurantName() != null && !item.getRestaurantName().isEmpty()) {
                tvStoreName.setText(item.getRestaurantName());
                layoutStoreInfo.setVisibility(View.VISIBLE);
                
                layoutStoreInfo.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (item.getRestaurantId() != null && item.getRestaurantId() != -1) {
                        android.content.Intent intent = new android.content.Intent(context, com.example.uitpayapp.home.StoreDetailActivity.class);
                        intent.putExtra(com.example.uitpayapp.home.StoreDetailActivity.EXTRA_RESTAURANT_ID, item.getRestaurantId());
                        context.startActivity(intent);
                    }
                });
            } else {
                layoutStoreInfo.setVisibility(View.GONE);
            }
        }
        tvFoodDesc.setText(item.getDescription());
        tvFoodPrice.setText(item.getFormattedPrice());

        final int[] popupQty = {existingCartItem != null ? existingCartItem.getQuantity() : 1};
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        View btnDecrease = view.findViewById(R.id.btn_decrease);
        View btnIncrease = view.findViewById(R.id.btn_increase);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);

        tvQuantity.setText(String.valueOf(popupQty[0]));

        final int[] toppingTotal = {0};
        final List<CartTopping> selectedToppings = new ArrayList<>();

        // If editing an existing CartItem, pre-load its toppings
        if (existingCartItem != null && existingCartItem.getSelectedToppings() != null) {
            selectedToppings.addAll(existingCartItem.getSelectedToppings());
            for (CartTopping t : selectedToppings) {
                toppingTotal[0] += t.getPrice();
            }
        }

        updatePopupPrice(view, item.getPrice(), toppingTotal[0], existingCartItem != null);

        btnDecrease.setOnClickListener(v -> {
            if (popupQty[0] > 1) {
                popupQty[0]--;
                tvQuantity.setText(String.valueOf(popupQty[0]));
                updatePopupPrice(view, item.getPrice(), toppingTotal[0], existingCartItem != null);
            }
        });

        btnIncrease.setOnClickListener(v -> {
            popupQty[0]++;
            tvQuantity.setText(String.valueOf(popupQty[0]));
            updatePopupPrice(view, item.getPrice(), toppingTotal[0], existingCartItem != null);
        });

        btnAddToCart.setOnClickListener(v -> {
            com.example.uitpayapp.network.SessionManager sessionManager = com.example.uitpayapp.network.SessionManager.getInstance(context);
            if (!sessionManager.isLoggedIn()) {
                com.example.uitpayapp.utils.LoginPopupHelper.showLoginRequiredPopup(context);
                return;
            }
            if (listener != null) {
                listener.onAddToCart(item, popupQty[0], new ArrayList<>(selectedToppings));
            }
            dialog.dismiss();
        });

        // Load toppings from API
        LinearLayout layoutToppingsContainer = view.findViewById(R.id.layout_toppings_container);
        layoutToppingsContainer.removeAllViews(); // Clear any hardcoded loading UI if present

        // Temporarily add a loading text
        TextView tvLoading = new TextView(context);
        tvLoading.setText("Đang tải danh sách tùy chọn...");
        tvLoading.setTextColor(Color.GRAY);
        layoutToppingsContainer.addView(tvLoading);

        // API ID format for FoodMenuItem is often like "f_1" or "d_1". We need to extract the Long ID.
        Long foodId = extractFoodId(item.getId());

        if (foodId != null) {
            RetrofitClient.getFoodService().getFoodOptions(foodId).enqueue(new Callback<ApiResponse<List<OptionGroupResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<OptionGroupResponse>>> call, Response<ApiResponse<List<OptionGroupResponse>>> response) {
                    layoutToppingsContainer.removeAllViews();
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<OptionGroupResponse> groups = response.body().getData();
                        if (groups.isEmpty()) {
                            // No options available
                            View header = view.findViewById(R.id.scroll_toppings);
                            if (header != null) header.setVisibility(View.GONE);
                            View divider = view.findViewById(R.id.divider);
                            if (divider != null) divider.setVisibility(View.GONE);
                            return;
                        }

                        renderToppingGroups(context, layoutToppingsContainer, groups, selectedToppings, toppingTotal, view, item.getPrice(), existingCartItem != null);
                    } else {
                        TextView tvError = new TextView(context);
                        tvError.setText("Không thể tải tùy chọn món ăn");
                        tvError.setTextColor(Color.RED);
                        layoutToppingsContainer.addView(tvError);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<OptionGroupResponse>>> call, Throwable t) {
                    layoutToppingsContainer.removeAllViews();
                    TextView tvError = new TextView(context);
                    tvError.setText("Lỗi mạng: Không thể tải tùy chọn");
                    tvError.setTextColor(Color.RED);
                    layoutToppingsContainer.addView(tvError);
                }
            });
        } else {
            layoutToppingsContainer.removeAllViews();
            View header = view.findViewById(R.id.scroll_toppings);
            if (header != null) header.setVisibility(View.GONE);
            View divider = view.findViewById(R.id.divider);
            if (divider != null) divider.setVisibility(View.GONE);
        }

        dialog.show();
    }

    private static void renderToppingGroups(Context context, LinearLayout container, List<OptionGroupResponse> groups, 
                                            List<CartTopping> selectedToppings, int[] toppingTotal, View rootView, long itemPrice, boolean isEditMode) {
        
        LayoutInflater inflater = LayoutInflater.from(context);

        // Hide static header from XML
        LinearLayout scrollChild = (LinearLayout) container.getParent();
        if (scrollChild != null) {
            for (int i = 0; i < scrollChild.getChildCount(); i++) {
                View child = scrollChild.getChildAt(i);
                if (child instanceof TextView) {
                    child.setVisibility(View.GONE);
                }
            }
        }

        for (OptionGroupResponse group : groups) {
            // Group Title
            TextView tvGroupTitle = new TextView(context);
            String title = group.getName();
            if (Boolean.TRUE.equals(group.getIsRequired())) {
                title += " (Bắt buộc)";
            } else {
                title += " (Tùy chọn)";
            }
            tvGroupTitle.setText(title);
            tvGroupTitle.setTextColor(Color.parseColor("#333333"));
            tvGroupTitle.setTextSize(16f);
            tvGroupTitle.setTypeface(null, Typeface.BOLD);
            tvGroupTitle.setPadding(0, 16, 0, 4);
            container.addView(tvGroupTitle);

            // Subtitle
            TextView tvSubtitle = new TextView(context);
            if (group.getMaxChoices() != null && group.getMaxChoices() > 0) {
                tvSubtitle.setText("Chọn tối đa " + group.getMaxChoices() + " loại");
            } else {
                tvSubtitle.setText("Chọn nhiều loại");
            }
            tvSubtitle.setTextColor(Color.parseColor("#9E9E9E"));
            tvSubtitle.setTextSize(12f);
            tvSubtitle.setPadding(0, 0, 0, 8);
            container.addView(tvSubtitle);

            // Group Items
            if (group.getItems() != null) {
                for (OptionItemResponse option : group.getItems()) {
                    View toppingView = inflater.inflate(R.layout.item_food_topping, container, false);
                    CheckBox cbTopping = toppingView.findViewById(R.id.cb_topping);
                    TextView tvToppingPrice = toppingView.findViewById(R.id.tv_topping_price);

                    cbTopping.setText(option.getName());

                    int price = option.getAdditionalPrice() != null ? option.getAdditionalPrice().intValue() : 0;
                    if (price > 0) {
                        tvToppingPrice.setText("+" + String.format("%,dđ", price).replace(',', '.'));
                    } else {
                        tvToppingPrice.setText("0đ");
                    }

                    // Pre-select if it exists in selectedToppings
                    boolean isPreSelected = false;
                    for (CartTopping t : selectedToppings) {
                        if (t.getName().equals(option.getName())) {
                            isPreSelected = true;
                            break;
                        }
                    }
                    cbTopping.setChecked(isPreSelected);

                    if (Boolean.FALSE.equals(option.getIsAvailable())) {
                        cbTopping.setEnabled(false);
                        cbTopping.setTextColor(Color.parseColor("#BDBDBD"));
                        tvToppingPrice.setTextColor(Color.parseColor("#BDBDBD"));
                        tvToppingPrice.setText("Hết hàng");
                    }

                    cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        // Handle maxChoices logic
                        if (isChecked && group.getMaxChoices() != null && group.getMaxChoices() > 0) {
                            // Count current selected in this group
                            int count = 0;
                            for (CartTopping t : selectedToppings) {
                                for (OptionItemResponse o : group.getItems()) {
                                    if (t.getName().equals(o.getName())) {
                                        count++;
                                    }
                                }
                            }
                            if (count >= group.getMaxChoices()) {
                                cbTopping.setChecked(false); // Revert
                                Toast.makeText(context, "Chỉ được chọn tối đa " + group.getMaxChoices() + " loại trong nhóm này", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (isChecked) {
                            toppingTotal[0] += price;
                            selectedToppings.add(new CartTopping("opt_" + option.getId(), option.getName(), price));
                        } else {
                            toppingTotal[0] -= price;
                            // Find and remove by name to match CartTopping equals
                            CartTopping toRemove = null;
                            for (CartTopping t : selectedToppings) {
                                if (t.getName().equals(option.getName())) {
                                    toRemove = t;
                                    break;
                                }
                            }
                            if (toRemove != null) {
                                selectedToppings.remove(toRemove);
                            }
                        }
                        updatePopupPrice(rootView, itemPrice, toppingTotal[0], isEditMode);
                    });

                    container.addView(toppingView);
                }
            }
        }
    }

    private static void updatePopupPrice(View view, long itemPrice, int toppingTotal, boolean isEditMode) {
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        int qty = Integer.parseInt(tvQuantity.getText().toString());
        long total = (itemPrice + toppingTotal) * qty;
        
        String actionText = isEditMode ? "Cập nhật - " : "Thêm vào giỏ - ";
        btnAddToCart.setText(actionText + String.format("%,dđ", total).replace(',', '.'));
    }

    private static Long extractFoodId(String stringId) {
        if (stringId == null || stringId.isEmpty()) return null;
        try {
            if (stringId.contains("_")) {
                String[] parts = stringId.split("_");
                return Long.parseLong(parts[parts.length - 1]);
            }
            return Long.parseLong(stringId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
