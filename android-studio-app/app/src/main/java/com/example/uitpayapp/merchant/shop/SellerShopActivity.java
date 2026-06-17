package com.example.uitpayapp.merchant.shop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.SellerHomeActivity;
import com.example.uitpayapp.merchant.marketing.SellerMarketingActivity;
import com.example.uitpayapp.merchant.notification.SellerNotificationActivity;
import com.example.uitpayapp.profile.ProfileActivity;
import com.example.uitpayapp.profile.ProfileWebView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SellerShopActivity extends AppCompatActivity {

    private TextView tvShopName, tvRevenue, tvTransactions, tvToday;
    private View navOrders, navNotification, navShop, navMarketing, btnReview, btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_shop);

        initViews();
        setupBottomNavigation();
    }

    private void initViews() {
        tvShopName = findViewById(R.id.tv_shop_name);
        tvRevenue = findViewById(R.id.tv_total_revenue);
        tvTransactions = findViewById(R.id.tv_total_transactions);
        tvToday = findViewById(R.id.tv_today);

        navOrders = findViewById(R.id.navOrders);
        navNotification = findViewById(R.id.navNotification);
        navShop = findViewById(R.id.navShop);
        navMarketing = findViewById(R.id.navMarketing);
        btnReview = findViewById(R.id.btn_review);
        btnMenu = findViewById(R.id.btn_menu);

        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        tvToday.setText(formattedDate);
        tvRevenue.setText("2.450.000đ");
        tvTransactions.setText("24");

        if (btnReview != null) {
            btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(this, SellerReviewActivity.class);
                startActivity(intent);
            });
        }

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                Intent intent = new Intent(this, MerchantMenuActivity.class);
                startActivity(intent);
            });
        }
        findViewById(R.id.btn_support).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileWebView.class);
            intent.putExtra("URL_KEY","https://merchant.shopeefood.vn/edu/collection/trung-cap");
            startActivity(intent);
        });
        findViewById(R.id.ll_account_selector).setOnClickListener(v -> {
            Intent intentAccount = new Intent(this, ProfileActivity.class);
            intentAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentAccount);
        });
    }

    private void setupBottomNavigation() {
        ImageView ivShop = findViewById(R.id.iv_nav_shop);
        TextView tvShop = findViewById(R.id.tv_nav_shop);
        if (ivShop != null) ivShop.setColorFilter(Color.parseColor("#f24405"));
        if (tvShop != null) tvShop.setTextColor(Color.parseColor("#f24405"));

        navOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellerNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        
        navShop.setOnClickListener(v -> {
        });
        navMarketing.setOnClickListener(v ->{
            Intent intent = new Intent(this, SellerMarketingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
