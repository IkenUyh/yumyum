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

import com.example.uitpayapp.modules.favorite.FavoriteRepository;
import com.example.uitpayapp.modules.favorite.models.FavoriteStatusResponseDTO;
import com.example.uitpayapp.modules.favorite.models.ToggleFavoriteResponseDTO;
import com.example.uitpayapp.network.ApiCallback;

public class StoreDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";

    private Restaurant restaurant;
    private ImageView btnFavorite;
    private FavoriteRepository favoriteRepository;
    private boolean isFavorited = false;

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
        favoriteRepository = new FavoriteRepository();
        checkFavoriteStatus();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        btnFavorite = findViewById(R.id.btn_favorite);
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                if (restaurant == null || restaurant.getId() == null) return;
                favoriteRepository.toggleFavorite(restaurant.getId(), new ApiCallback<ToggleFavoriteResponseDTO>() {
                    @Override
                    public void onSuccess(ToggleFavoriteResponseDTO result) {
                        isFavorited = result.isFavorited();
                        updateFavoriteHeartIcon();
                        Toast.makeText(StoreDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(StoreDetailActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

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
        if (popularFoods.size() > 4)
            popularFoods = popularFoods.subList(0, 4);

        RecyclerView rvPopularFoods = findViewById(R.id.rv_popular_foods);
        rvPopularFoods.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPopularFoods.setAdapter(new StorePopularFoodAdapter(popularFoods, this::showFoodItemDetailPopup));

        // All menu foods
        RecyclerView rvAllMenuFoods = findViewById(R.id.rv_all_menu_foods);
        rvAllMenuFoods.setLayoutManager(new LinearLayoutManager(this));
        rvAllMenuFoods.setAdapter(new StoreMenuFoodAdapter(restaurant.getMenu(), this::showFoodItemDetailPopup));
    }

    private Restaurant findRestaurantByName(String name) {
        if (name == null)
            return null;
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
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(this, item, null,
                (selectedItem, quantity, selectedToppings) -> {
                    CartManager.getInstance().addItem(new CartItem(selectedItem, quantity, selectedToppings));

                    updateGlobalCartBadge();

                    View btnCart = findViewById(R.id.btn_cart);
                    // Fallback for image view animation context (can just use root view if needed)
                    View rootView = findViewById(android.R.id.content);
                    CartAnimationHelper.animateFlyToCart(this, rootView, btnCart, () -> {
                    });
                });
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
        checkFavoriteStatus();
    }

    private void checkFavoriteStatus() {
        if (restaurant == null || restaurant.getId() == null || btnFavorite == null) {
            if (btnFavorite != null) btnFavorite.setVisibility(View.GONE);
            return;
        }

        favoriteRepository.getFavoriteStatus(restaurant.getId(), new ApiCallback<FavoriteStatusResponseDTO>() {
            @Override
            public void onSuccess(FavoriteStatusResponseDTO status) {
                isFavorited = status.isFavorited();
                updateFavoriteHeartIcon();
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }

    private void updateFavoriteHeartIcon() {
        if (btnFavorite == null) return;
        if (isFavorited) {
            btnFavorite.setImageResource(R.drawable.favorite_filled_24px);
            btnFavorite.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EE4D2D")));
        } else {
            btnFavorite.setImageResource(R.drawable.favorite_border_24px);
            btnFavorite.setImageTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
        }
    }
}
