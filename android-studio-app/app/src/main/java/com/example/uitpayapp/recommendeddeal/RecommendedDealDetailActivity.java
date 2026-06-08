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

    private ImageView ivDealImage, btnBack, btnShare, ivStoreLogo, btnCart;
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
        btnShare = findViewById(R.id.btn_share);
        tvDiscountPrice = findViewById(R.id.tv_discount_price);
        tvOriginalPrice = findViewById(R.id.tv_original_price);
        tvDealName = findViewById(R.id.tv_deal_name);
        ivStoreLogo = findViewById(R.id.iv_store_logo);
        tvStoreName = findViewById(R.id.tv_store_name);
        tvRating = findViewById(R.id.tv_rating);
        tvDistance = findViewById(R.id.tv_distance);
        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        tvDiscountPriceFooter = findViewById(R.id.tv_discount_price_footer);
        tvSaving = findViewById(R.id.tv_saving_label);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnCart = findViewById(R.id.btn_cart);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        
        btnCart.setOnClickListener(v -> {
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
            int foodImage = extras.getInt("food_image", R.drawable.img_food_chicken);

            tvDealName.setText(foodTitle);
            tvStoreName.setText(storeName);
            tvDiscountPrice.setText(currencyFormatter.format(discountPrice));
            tvDiscountPriceFooter.setText(currencyFormatter.format(discountPrice));
            tvOriginalPrice.setText(currencyFormatter.format(originalPrice));
            tvDistance.setText(distance + "km");
            tvDeliveryTime.setText(deliveryTime + " phút");
            ivDealImage.setImageResource(foodImage);
            tvSaving.setText(currencyFormatter.format(originalPrice - discountPrice));

            FoodMenuItem item = new FoodMenuItem("deal_" + System.currentTimeMillis(), foodTitle, (long) discountPrice, foodImage, "Khuyến mãi từ " + storeName);
            btnBuyNow.setOnClickListener(v -> showFoodItemDetailPopup(item));
        }
    }

    private void showFoodItemDetailPopup(FoodMenuItem item) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_food_detail, null);
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
        int[] mockPrices = {5000, 10000, 5000, 0, 0};
        
        final int[] toppingTotal = {0};
        final java.util.List<com.example.uitpayapp.home.home_models.CartTopping> selectedToppings = new java.util.ArrayList<>();

        for (int i = 0; i < 5; i++) {
            View toppingView = LayoutInflater.from(this).inflate(R.layout.item_food_topping, layoutToppings, false);
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

        TextView btnDecrease = view.findViewById(R.id.btn_decrease);
        TextView btnIncrease = view.findViewById(R.id.btn_increase);
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);

        final int[] popupQty = {1};
        tvQuantity.setText(String.valueOf(popupQty[0]));
        
        // Initial text
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

        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addItem(new CartItem(item, popupQty[0], new java.util.ArrayList<>(selectedToppings)));
            
            // Bay animation instead of Toast
            CartAnimationHelper.animateFlyToCart(this, ivFoodImage, btnCart, () -> {
                updateCartBadge();
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
}
