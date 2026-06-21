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
    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";

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

        initViews();
        updateGlobalCartBadge();

        long restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        String restName = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);

        if (restaurantId != -1) {
            fetchRestaurantDetails(restaurantId);
        } else {
            Toast.makeText(this, "Thiếu ID cửa hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        favoriteRepository = new FavoriteRepository();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnFavorite = findViewById(R.id.btn_favorite);
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                if (restaurant == null || restaurant.getId() == null) return;
                
                // Optimistic UI update
                isFavorited = !isFavorited;
                updateFavoriteHeartIcon();
                
                favoriteRepository.toggleFavorite(restaurant.getId(), new ApiCallback<ToggleFavoriteResponseDTO>() {
                    @Override
                    public void onSuccess(ToggleFavoriteResponseDTO result) {
                        // Keep optimistic update, just show success message if any
                        String msg = (result != null && result.getMessage() != null) ? result.getMessage() : (isFavorited ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích");
                        Toast.makeText(StoreDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Revert on error
                        isFavorited = !isFavorited;
                        updateFavoriteHeartIcon();
                        Toast.makeText(StoreDetailActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        View btnCart = findViewById(R.id.btn_cart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                if (!com.example.uitpayapp.network.SessionManager.getInstance(this).isLoggedIn()) {
                    com.example.uitpayapp.utils.LoginPopupHelper.showLoginRequiredPopup(this);
                    return;
                }
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });
        }
    }

    private void fetchRestaurantDetails(Long id) {
        com.example.uitpayapp.network.RetrofitClient.getRestaurantService().getRestaurantById(id)
            .enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO dto = response.body().getData();
                        double ratingVal = dto.getRatingAverage() != null ? dto.getRatingAverage() : 0.0;
                        int reviewsVal = dto.getReviewCount() != null ? dto.getReviewCount() : 0;
                        restaurant = new Restaurant(dto.getId(), dto.getName(), dto.getName().substring(0, 1), Color.RED, "Danh mục", new ArrayList<>(), R.drawable.ic_food, ratingVal, reviewsVal, 30, dto.getAddress(), dto.getImageUrl());
                        updateStoreUI(dto);
                        checkFavoriteStatus();
                        fetchRestaurantFoods(id);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>> call, Throwable t) {
                }
            });
    }

    private void fetchRestaurantFoods(Long id) {
        com.example.uitpayapp.network.RetrofitClient.getRestaurantService().getRestaurantMenu(id)
            .enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<FoodMenuItem> menuItems = new ArrayList<>();
                        for (com.example.uitpayapp.modules.food.models.responses.FoodResponse food : response.body().getData()) {
                            FoodMenuItem item = new FoodMenuItem(String.valueOf(food.getId()), food.getName(), food.getPrice().longValue(), 0, food.getDescription(), food.getImageUrl());
                            item.setRestaurantId(id);
                            menuItems.add(item);
                        }
                        restaurant.getMenu().clear();
                        restaurant.getMenu().addAll(menuItems);
                        updateMenuUI(menuItems);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>> call, Throwable t) {
                }
            });
    }

    private void updateStoreUI(com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO dto) {
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(dto.getName());
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

        ImageView ivBanner = findViewById(R.id.iv_store_banner);
        TextView tvStoreName = findViewById(R.id.tv_store_name);
        TextView tvStoreAddress = findViewById(R.id.tv_store_address);
        TextView tvStoreRating = findViewById(R.id.tv_store_rating);
        TextView tvDeliveryTime = findViewById(R.id.tv_delivery_time);

        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(this).load(dto.getImageUrl()).into(ivBanner);
        }
        tvStoreName.setText(dto.getName());
        tvStoreAddress.setText(dto.getAddress() != null ? dto.getAddress() : "Không có địa chỉ");
        double ratingVal = dto.getRatingAverage() != null ? dto.getRatingAverage() : 0.0;
        int reviewsVal = dto.getReviewCount() != null ? dto.getReviewCount() : 0;
        if (reviewsVal > 0) {
            tvStoreRating.setText(String.format(java.util.Locale.US, "%.1f (%d+ Bình luận)", ratingVal, reviewsVal));
        } else {
            tvStoreRating.setText(String.format(java.util.Locale.US, "%.1f (Chưa có bình luận)", ratingVal));
        }
        tvDeliveryTime.setText("30 phút");
    }

    private void updateMenuUI(List<FoodMenuItem> menuItems) {
        List<FoodMenuItem> popularFoods = new ArrayList<>(menuItems);
        Collections.shuffle(popularFoods);
        if (popularFoods.size() > 4) popularFoods = popularFoods.subList(0, 4);

        RecyclerView rvPopularFoods = findViewById(R.id.rv_popular_foods);
        if (rvPopularFoods != null) {
            rvPopularFoods.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvPopularFoods.setAdapter(new StorePopularFoodAdapter(popularFoods, this::showFoodItemDetailPopup));
        }

        RecyclerView rvAllMenuFoods = findViewById(R.id.rv_all_menu_foods);
        if (rvAllMenuFoods != null) {
            rvAllMenuFoods.setLayoutManager(new LinearLayoutManager(this));
            rvAllMenuFoods.setAdapter(new StoreMenuFoodAdapter(menuItems, this::showFoodItemDetailPopup));
        }
    }

    private void showFoodItemDetailPopup(FoodMenuItem item) {
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(this, item, null,
                (selectedItem, quantity, selectedToppings) -> {
                    CartItem newItem = new CartItem(selectedItem, quantity, selectedToppings);
                    CartManager.getInstance().addItemSync(StoreDetailActivity.this, newItem, new ApiCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            runOnUiThread(() -> {
                                updateGlobalCartBadge();

                                View btnCart = findViewById(R.id.btn_cart);
                                View rootView = findViewById(android.R.id.content);
                                CartAnimationHelper.animateFlyToCart(StoreDetailActivity.this, rootView, btnCart, () -> {
                                });
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                Toast.makeText(StoreDetailActivity.this, "Không thể thêm vào giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                            });
                        }
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
        btnFavorite.setVisibility(View.VISIBLE);

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
