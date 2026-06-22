package com.example.uitpayapp.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvPromotions;
    private PromoAdapter promoAdapter;
    private List<PromoNotification> promoList;
    private android.content.BroadcastReceiver badgeUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            // Thiết lập phần padding bottom của bottom_container bằng chính độ cao thanh điều hướng
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(findViewById(R.id.bottom_container) != null ? R.id.bottom_container : R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutHeader), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Ánh xạ và chuyển hướng màn hình con chính xác như mẫu
        findViewById(R.id.btnNavNews).setOnClickListener(v -> {
            startActivity(new Intent(NotificationActivity.this, NotificationNewsActivity.class));
        });

        findViewById(R.id.btnNavOrders).setOnClickListener(v -> {
            startActivity(new Intent(NotificationActivity.this, NotificationOrderActivity.class));
        });

        rvPromotions = findViewById(R.id.rvPromotions);
        promoList = new ArrayList<>();
        promoAdapter = new PromoAdapter(promoList, () -> {
            updateNotificationBadge();
        });
        rvPromotions.setAdapter(promoAdapter);
        rvPromotions.setLayoutManager(new LinearLayoutManager(this));

        loadRealData();
        setupBottomNavigation();
        badgeUpdateReceiver = com.example.uitpayapp.utils.NotificationBadgeHelper.registerBadgeReceiver(this, () -> {
            updateNotificationBadge();
        });
    }

    private void loadRealData() {
        // A. Load Latest News for "Tin tức" row sub-text
        com.example.uitpayapp.modules.news.NewsRepository newsRepository = new com.example.uitpayapp.modules.news.NewsRepository();
        newsRepository.getActiveNews(new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.news.models.NewsDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.news.models.NewsDTO> data) {
                if (data != null && !data.isEmpty()) {
                    TextView tvLatestNews = findViewById(R.id.tvLatestNews);
                    if (tvLatestNews != null) {
                        tvLatestNews.setText(data.get(0).getTitle());
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Keep default placeholder on error
            }
        });

        // B. Load Notification History for "Khuyến mãi" list, "Đơn hàng" row sub-text, and unread badge
        com.example.uitpayapp.modules.notification.NotificationRepository notificationRepository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        notificationRepository.getHistory(null, null, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO> data) {
                List<PromoNotification> promos = new ArrayList<>();
                int unreadOrderCount = 0;
                String latestOrderTitle = null;

                for (com.example.uitpayapp.modules.notification.models.NotificationResponseDTO dto : data) {
                    if ("PROMOTION".equalsIgnoreCase(dto.getType())) {
                        promos.add(new PromoNotification(
                                String.valueOf(dto.getId()),
                                dto.getTitle(),
                                dto.getMessage(),
                                formatDateTime(dto.getCreatedAt()),
                                R.drawable.ic_discount_voucher,
                                dto.getIsRead() != null && dto.getIsRead()
                        ));
                    } else if ("ORDER_UPDATE".equalsIgnoreCase(dto.getType()) || "SYSTEM".equalsIgnoreCase(dto.getType())) {
                        if (latestOrderTitle == null) {
                            latestOrderTitle = dto.getTitle();
                        }
                        if (dto.getIsRead() == null || !dto.getIsRead()) {
                            unreadOrderCount++;
                        }
                    }
                }

                // Update Promotions RecyclerView
                if (!promos.isEmpty()) {
                    promoList.clear();
                    promoList.addAll(promos);
                    promoAdapter.notifyDataSetChanged();
                }

                // Update latest order text snippet
                if (latestOrderTitle != null) {
                    TextView tvLatestOrderNoti = findViewById(R.id.tvLatestOrderNoti);
                    if (tvLatestOrderNoti != null) {
                        tvLatestOrderNoti.setText(latestOrderTitle);
                    }
                }

                // Update order notifications badge count directly using computed local count
                TextView tvOrderBadge = findViewById(R.id.tvOrderBadge);
                if (tvOrderBadge != null) {
                    if (unreadOrderCount > 0) {
                        tvOrderBadge.setText(String.valueOf(unreadOrderCount));
                        tvOrderBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvOrderBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Keep default/dummy data on failure
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

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navAccount = findViewById(R.id.navAccount);
        LinearLayout navFavorite = findViewById(R.id.navFavorite);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.home.HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        navFavorite.setOnClickListener(v->{
            Intent intent = new Intent(this, com.example.uitpayapp.favorite.FavoriteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.history.TransactionHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
        loadRealData(); // Refresh notifications on resume
    }

    private void updateNotificationBadge() {
        com.example.uitpayapp.utils.NotificationBadgeHelper.updateBadge(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (badgeUpdateReceiver != null) {
            unregisterReceiver(badgeUpdateReceiver);
        }
    }
}