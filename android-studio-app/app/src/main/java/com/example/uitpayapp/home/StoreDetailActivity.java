package com.example.uitpayapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.AppBarLayout;
import android.graphics.Color;
import androidx.core.view.WindowInsetsControllerCompat;


import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.R;
import com.example.uitpayapp.utils.CartAnimationHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.uitpayapp.home.home_adapters.StoreMenuFoodAdapter;
import com.example.uitpayapp.home.home_adapters.StorePopularFoodAdapter;
import com.example.uitpayapp.home.home_adapters.StoreVoucherAdapter;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StoreDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";

    private Restaurant restaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_store_detail);

        String restName = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);
        restaurant = findRestaurantByName(restName);

        if (restaurant == null) {
            Toast.makeText(this, "Không tìm thấy thông tin cửa hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupData();
        updateGlobalCartBadge();

        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        if (tvToolbarTitle != null && restaurant != null) {
            tvToolbarTitle.setText(restaurant.getName());
        }

        AppBarLayout appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                float percentage = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
                if (tvToolbarTitle != null) {
                    tvToolbarTitle.setAlpha(percentage > 0.2f ? (percentage - 0.2f) * 1.25f : 0f);
                }
            });
        }
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        View btnCart = findViewById(R.id.btn_cart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupData() {
        // Top section
        ImageView ivBanner = findViewById(R.id.iv_store_banner);
        TextView tvStoreName = findViewById(R.id.tv_store_name);
        TextView tvStoreAddress = findViewById(R.id.tv_store_address);
        TextView tvStoreRating = findViewById(R.id.tv_store_rating);
        TextView tvDeliveryTime = findViewById(R.id.tv_delivery_time);

        ivBanner.setImageResource(restaurant.getImageResId());
        tvStoreName.setText(restaurant.getName());
        tvStoreAddress.setText(restaurant.getAddress() != null ? restaurant.getAddress() : "Không có địa chỉ");
        tvStoreRating.setText(String.format("%s (%s+ Bình luận)", restaurant.getRating(), restaurant.getReviewCount()));
        tvDeliveryTime.setText(restaurant.getDeliveryTime() + " phút");

        // Vouchers
        RecyclerView rvVouchers = findViewById(R.id.rv_vouchers);
        rvVouchers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> vouchers = Arrays.asList("Giảm 90% | Đơn từ 99k", "Giảm 50% | Đơn từ 39k", "Freeship | Đơn từ 0đ");
        rvVouchers.setAdapter(new StoreVoucherAdapter(vouchers));

        // Popular foods (first 3-4 items, shuffled)
        List<FoodMenuItem> popularFoods = new ArrayList<>(restaurant.getMenu());
        Collections.shuffle(popularFoods);
        if (popularFoods.size() > 4) popularFoods = popularFoods.subList(0, 4);

        RecyclerView rvPopularFoods = findViewById(R.id.rv_popular_foods);
        rvPopularFoods.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPopularFoods.setAdapter(new StorePopularFoodAdapter(popularFoods, this::showFoodItemDetailPopup));

        // All menu foods
        RecyclerView rvAllMenuFoods = findViewById(R.id.rv_all_menu_foods);
        rvAllMenuFoods.setLayoutManager(new LinearLayoutManager(this));
        rvAllMenuFoods.setAdapter(new StoreMenuFoodAdapter(restaurant.getMenu(), this::showFoodItemDetailPopup));
    }

    private Restaurant findRestaurantByName(String name) {
        if (name == null) return null;
        List<Restaurant> all = HomeActivity.HomeRepository.getInstance().getRestaurants();
        
        // Exact match
        for (Restaurant r : all) {
            if (name.equals(r.getName())) {
                return r;
            }
        }
        
        // Partial match (e.g. "Gà Rán Popeyes" vs "Gà Rán Popeyes - Võ Văn Ngân")
        for (Restaurant r : all) {
            if (name.contains(r.getName()) || r.getName().contains(name)) {
                return r;
            }
        }
        
        // If not found, create a mock restaurant dynamically so the UI still works
        Restaurant randomRest = all.get(new java.util.Random().nextInt(all.size()));
        String shortName = name.length() >= 2 ? name.substring(0, 2).toUpperCase() : "ST";
        return new Restaurant(name, shortName, randomRest.getBgColor(), 
                "Đồ ăn", randomRest.getMenu(), randomRest.getImageResId(), 
                4.5, 120, 20, "Địa chỉ mẫu, TP.HCM");
    }

    private void showFoodItemDetailPopup(FoodMenuItem item) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_food_detail, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        ImageView ivFoodImage = view.findViewById(R.id.iv_food_image);
        TextView tvFoodName = view.findViewById(R.id.tv_food_name);
        TextView tvFoodDesc = view.findViewById(R.id.tv_food_desc);
        TextView tvFoodPrice = view.findViewById(R.id.tv_food_price);

        ivFoodImage.setImageResource(item.getImageResId());
        tvFoodName.setText(item.getName());
        tvFoodDesc.setText(item.getDescription());
        tvFoodPrice.setText(item.getFormattedPrice());

        final int[] popupQty = {1};
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        View btnDecrease = view.findViewById(R.id.btn_decrease);
        View btnIncrease = view.findViewById(R.id.btn_increase);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        
        final int[] toppingTotal = {0};

        updatePopupPrice(view, item.getPrice(), toppingTotal[0]);

        btnDecrease.setOnClickListener(v -> {
            if (popupQty[0] > 1) {
                popupQty[0]--;
                tvQuantity.setText(String.valueOf(popupQty[0]));
                updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
            }
        });

        btnIncrease.setOnClickListener(v -> {
            popupQty[0]++;
            tvQuantity.setText(String.valueOf(popupQty[0]));
            updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
        });

        LinearLayout layoutToppings = view.findViewById(R.id.layout_toppings_container);
        String[] mockToppings = {"Thêm trân châu đen", "Thêm phô mai", "Thêm thạch mảng cầu"};
        int[] mockPrices = {5000, 10000, 5000};
        
        final java.util.List<com.example.uitpayapp.home.home_models.CartTopping> selectedToppings = new java.util.ArrayList<>();

        for (int i = 0; i < 3; i++) {
            View toppingView = android.view.LayoutInflater.from(this).inflate(R.layout.item_food_topping, layoutToppings, false);
            android.widget.CheckBox cbTopping = toppingView.findViewById(R.id.cb_topping);
            TextView tvToppingPrice = toppingView.findViewById(R.id.tv_topping_price);
            cbTopping.setText(mockToppings[i]);
            
            if (mockPrices[i] > 0) {
                tvToppingPrice.setText("+" + String.format("%,dđ", mockPrices[i]).replace(',', '.'));
            } else {
                tvToppingPrice.setText("0đ");
            }
            
            final int price = mockPrices[i];
            final String toppingName = mockToppings[i];
            final String toppingId = "tp_" + i;
            cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    toppingTotal[0] += price;
                    selectedToppings.add(new com.example.uitpayapp.home.home_models.CartTopping(toppingId, toppingName, price));
                } else {
                    toppingTotal[0] -= price;
                    selectedToppings.remove(new com.example.uitpayapp.home.home_models.CartTopping(toppingId, toppingName, price));
                }
                updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
            });
            
            layoutToppings.addView(toppingView);
        }

        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addItem(new CartItem(item, popupQty[0], new java.util.ArrayList<>(selectedToppings)));
            
            updateGlobalCartBadge();
            
            View btnCart = findViewById(R.id.btn_cart);
            CartAnimationHelper.animateFlyToCart(this, ivFoodImage, btnCart, () -> {
            });
            
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updatePopupPrice(View view, long itemPrice, int toppingTotal) {
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        int qty = Integer.parseInt(tvQuantity.getText().toString());
        long total = (itemPrice + toppingTotal) * qty;
        btnAddToCart.setText("Thêm vào giỏ - " + String.format("%,dđ", total).replace(',', '.'));
    }

    private void updateGlobalCartBadge() {
        int count = 0;
        for (com.example.uitpayapp.home.home_models.CartItem item : CartManager.getInstance().getCart()) {
            count += item.getQuantity();
        }
        
        TextView tvCartBadge = findViewById(R.id.tv_global_cart_badge);
        if (tvCartBadge != null) {
            if (count > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(String.valueOf(count));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGlobalCartBadge();
    }
}
