package com.example.uitpayapp.recommendeddeal;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class RecommendedDealDetailActivity extends AppCompatActivity {

    private ImageView ivDealImage, btnBack, btnShare, ivStoreLogo;
    private TextView tvDiscountPrice, tvOriginalPrice, tvDealName, tvStoreName, tvRating, tvDistance, tvDeliveryTime, tvDiscountPriceFooter,tvSaving;
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
        }
    }
}
