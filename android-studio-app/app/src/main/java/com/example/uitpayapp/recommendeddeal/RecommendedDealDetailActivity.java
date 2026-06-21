package com.example.uitpayapp.recommendeddeal;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.CartActivity;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.utils.CartAnimationHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class RecommendedDealDetailActivity extends AppCompatActivity {

    private ImageView ivDealImage, btnBack, ivStoreLogo, btnCart;
    private TextView tvDiscountPrice, tvOriginalPrice, tvDealName, tvStoreName, tvRating, tvDistance, tvDeliveryTime, tvDiscountPriceFooter, tvSaving, tvCartBadge;
    private View btnBuyNow;
    private final DecimalFormat currencyFormatter;

    public RecommendedDealDetailActivity() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        currencyFormatter = new DecimalFormat("#,###đ", symbols);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_deal_detail);

        initViews();
        displayData();

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) return;
        int count = CartManager.getInstance().getTotalItemCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        View topBar=findViewById(R.id.top_bar_recommend_deal_detail);
        View mainContainer=findViewById(R.id.recommend_deal_detail_container);
        ivDealImage = findViewById(R.id.iv_deal_image);
        btnBack = findViewById(R.id.btn_back);
        tvDiscountPrice = findViewById(R.id.tv_discount_price);
        tvOriginalPrice = findViewById(R.id.tv_original_price);
        tvDealName = findViewById(R.id.tv_deal_name);
        ivStoreLogo = findViewById(R.id.iv_store_logo);
        tvStoreName = findViewById(R.id.tv_store_name);
        tvDistance = findViewById(R.id.tv_distance);
        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        tvDiscountPriceFooter = findViewById(R.id.tv_discount_price_footer);
        tvSaving = findViewById(R.id.tv_saving_label);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnCart = findViewById(R.id.btn_cart);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        
        btnCart.setOnClickListener(v -> {
            if (!com.example.uitpayapp.network.SessionManager.getInstance(this).isLoggedIn()) {
                com.example.uitpayapp.utils.LoginPopupHelper.showLoginRequiredPopup(this);
                return;
            }
            startActivity(new android.content.Intent(this, CartActivity.class));
        });

        //Gach ngang gia cu
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top,systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void displayData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String foodTitle = extras.getString("food_title", "");
            String storeName = extras.getString("store_name", "");
            double discountPrice = extras.getDouble("discount_price", 0);
            double originalPrice = extras.getDouble("original_price", 0);
            double distance = extras.getDouble("distance", 0);
            int deliveryTime = extras.getInt("delivery_time", 0);
            int foodImage = extras.getInt("food_image", 0);
            String imageUrl = extras.getString("image_url", "");
            double rating = extras.getDouble("rating", 4.5);
            long restaurantId = extras.getLong("restaurant_id", -1L);
            long foodId = extras.getLong("food_id", -1L);

            tvDealName.setText(foodTitle);
            tvStoreName.setText(storeName);
            tvDiscountPrice.setText(currencyFormatter.format(discountPrice));
            tvDiscountPriceFooter.setText(currencyFormatter.format(discountPrice));
            tvOriginalPrice.setText(currencyFormatter.format(originalPrice));
            tvDistance.setText(distance + "km");
            tvDeliveryTime.setText(deliveryTime + " phút");
            if (tvRating != null) {
                tvRating.setText(String.valueOf(rating));
            }
            tvSaving.setText(currencyFormatter.format(originalPrice - discountPrice));

            android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0"));
            if (imageUrl != null && !imageUrl.isEmpty()) {
                android.view.animation.AlphaAnimation blinkAnimation = new android.view.animation.AlphaAnimation(0.5f, 1.0f);
                blinkAnimation.setDuration(500);
                blinkAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
                blinkAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
                ivDealImage.startAnimation(blinkAnimation);

                com.bumptech.glide.request.RequestOptions options = new com.bumptech.glide.request.RequestOptions().placeholder(grayPlaceholder);
                com.bumptech.glide.Glide.with(this)
                        .load(imageUrl)
                        .apply(options)
                        .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                ivDealImage.clearAnimation();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                ivDealImage.clearAnimation();
                                return false;
                            }
                        })
                        .into(ivDealImage);
            } else if (foodImage != 0) {
                ivDealImage.setImageResource(foodImage);
            } else {
                ivDealImage.setImageDrawable(grayPlaceholder);
            }

            FoodMenuItem item;
            if (foodId != -1L) {
                item = new FoodMenuItem(String.valueOf(foodId), foodTitle, (long) discountPrice, foodImage, "Khuyến mãi từ " + storeName, imageUrl);
            } else {
                item = new FoodMenuItem("deal_" + System.currentTimeMillis(), foodTitle, (long) discountPrice, foodImage, "Khuyến mãi từ " + storeName, imageUrl);
            }
            if (restaurantId != -1L) {
                item.setRestaurantId(restaurantId);
            }
            item.setRestaurantName(storeName);
            btnBuyNow.setOnClickListener(v -> showFoodItemDetailPopup(item));

            View cvStoreInfo = findViewById(R.id.cv_store_info);
            if (cvStoreInfo != null) {
                cvStoreInfo.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.home.StoreDetailActivity.class);
                    intent.putExtra(com.example.uitpayapp.home.StoreDetailActivity.EXTRA_RESTAURANT_NAME, storeName);
                    if (restaurantId != -1L) {
                        intent.putExtra(com.example.uitpayapp.home.StoreDetailActivity.EXTRA_RESTAURANT_ID, restaurantId);
                    }
                    startActivity(intent);
                });
            }
        }
    }

    private void showFoodItemDetailPopup(FoodMenuItem item) {
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(this, item, null, (selectedItem, quantity, selectedToppings) -> {
            CartItem newItem = new CartItem(selectedItem, quantity, selectedToppings);
            CartManager.getInstance().addItemSync(RecommendedDealDetailActivity.this, newItem, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        CartAnimationHelper.animateFlyToCart(RecommendedDealDetailActivity.this, ivDealImage, btnCart, () -> {
                            updateCartBadge();
                        });
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(RecommendedDealDetailActivity.this, "Không thể thêm vào giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}
