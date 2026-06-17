package com.example.uitpayapp.merchant.notification;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.SellerHomeActivity;
import com.example.uitpayapp.merchant.marketing.SellerMarketingActivity;
import com.example.uitpayapp.merchant.shop.SellerShopActivity;
import java.util.ArrayList;
import java.util.List;

public class SellerNotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private SellerNotificationAdapter adapter;
    private List<SellerNotification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_notification);

        initViews();
        setupBottomNav();
        loadDummyData();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rv_notifications);
        findViewById(R.id.tv_mark_all_read).setOnClickListener(v -> markAllAsRead());
        findViewById(R.id.tv_delete_all).setOnClickListener(v -> deleteAll());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_notification_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadDummyData() {
        notificationList = new ArrayList<>();
        notificationList.add(new SellerNotification("1", "Đơn hàng mới #12345", "Bạn có đơn hàng mới cần xác nhận", "5 phút trước", false, 1));
        notificationList.add(new SellerNotification("2", "Khuyến mãi mới", "Giảm giá 50% cho tất cả sản phẩm trong tuần này", "1 giờ trước", false, 2));
        notificationList.add(new SellerNotification("3", "Cập nhật hệ thống", "Phiên bản mới đã được cập nhật với nhiều tính năng mới", "2 giờ trước", false, 3));
        notificationList.add(new SellerNotification("4", "Đánh giá từ khách hàng", "Khách hàng đã để lại đánh giá 5 sao cho cửa hàng", "Hôm qua", true, 4));

        adapter = new SellerNotificationAdapter(notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void markAllAsRead() {
        for (SellerNotification n : notificationList) {
            n.setRead(true);
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteAll() {
        notificationList.clear();
        adapter.notifyDataSetChanged();
    }

    private void setupBottomNav() {
        ImageView ivNotification = findViewById(R.id.iv_nav_notification);
        TextView tvNotification = findViewById(R.id.tv_nav_notification);
        ivNotification.setColorFilter(Color.parseColor("#f24405"));
        tvNotification.setTextColor(Color.parseColor("#f24405"));

        findViewById(R.id.navOrders).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerHomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
            finish();
        });

        findViewById(R.id.navShop).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerShopActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
            finish();
        });

        findViewById(R.id.navMarketing).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerMarketingActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
