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
        loadRealData();
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

    private void loadRealData() {
        notificationList = new ArrayList<>();
        adapter = new SellerNotificationAdapter(notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        com.example.uitpayapp.modules.notification.NotificationRepository repository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repository.getHistory(new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO> data) {
                notificationList.clear();
                for (com.example.uitpayapp.modules.notification.models.NotificationResponseDTO dto : data) {
                    int typeVal = 3; // default System
                    if ("ORDER_UPDATE".equalsIgnoreCase(dto.getType())) {
                        typeVal = 1;
                    } else if ("PROMOTION".equalsIgnoreCase(dto.getType())) {
                        typeVal = 2;
                    } else if ("REVIEW".equalsIgnoreCase(dto.getType())) {
                        typeVal = 4;
                    }
                    notificationList.add(new SellerNotification(
                            String.valueOf(dto.getId()),
                            dto.getTitle(),
                            dto.getMessage(),
                            formatDateTime(dto.getCreatedAt()),
                            dto.getIsRead() != null && dto.getIsRead(),
                            typeVal
                    ));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(SellerNotificationActivity.this, "Lỗi tải thông báo: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAllAsRead() {
        if (notificationList.isEmpty()) return;
        com.example.uitpayapp.modules.notification.NotificationRepository repository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repository.markAllAsRead(new com.example.uitpayapp.network.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                for (SellerNotification n : notificationList) {
                    n.setRead(true);
                }
                adapter.notifyDataSetChanged();
                android.widget.Toast.makeText(SellerNotificationActivity.this, "Đã đánh dấu đọc tất cả", android.widget.Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(SellerNotificationActivity.this, "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAll() {
        if (notificationList.isEmpty()) return;
        com.example.uitpayapp.modules.notification.NotificationRepository repository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repository.deleteAllNotifications(new com.example.uitpayapp.network.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                notificationList.clear();
                adapter.notifyDataSetChanged();
                android.widget.Toast.makeText(SellerNotificationActivity.this, "Đã xóa tất cả thông báo", android.widget.Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(SellerNotificationActivity.this, "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDateTime(String isoString) {
        if (isoString == null) return "";
        try {
            String cleanStr = isoString;
            if (cleanStr.contains(".")) {
                int dotIdx = cleanStr.indexOf(".");
                int tIdx = cleanStr.indexOf("+");
                if (tIdx == -1) tIdx = cleanStr.indexOf("-", dotIdx);
                if (tIdx == -1) tIdx = cleanStr.indexOf("Z", dotIdx);
                if (tIdx != -1) {
                    cleanStr = cleanStr.substring(0, dotIdx) + cleanStr.substring(tIdx);
                } else {
                    cleanStr = cleanStr.substring(0, dotIdx);
                }
            }
            java.text.SimpleDateFormat inputFormat;
            if (cleanStr.endsWith("Z")) {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            } else if (cleanStr.contains("+") || (cleanStr.lastIndexOf("-") > 10)) {
                try {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.getDefault());
                    java.util.Date date = inputFormat.parse(cleanStr);
                    java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                    return outputFormat.format(date);
                } catch (Exception ex) {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                }
            } else {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            }
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(cleanStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoString;
        }
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
