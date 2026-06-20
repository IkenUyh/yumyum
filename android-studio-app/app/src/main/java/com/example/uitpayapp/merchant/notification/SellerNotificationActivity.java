package com.example.uitpayapp.merchant.notification;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.SellerHomeActivity;
import com.example.uitpayapp.merchant.marketing.SellerMarketingActivity;
import com.example.uitpayapp.merchant.shop.SellerShopActivity;
import com.example.uitpayapp.modules.notification.NotificationRepository;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;
import com.example.uitpayapp.network.ApiCallback;

import java.util.ArrayList;
import java.util.List;

public class SellerNotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private SellerNotificationAdapter adapter;
    private List<SellerNotification> notificationList;
    private List<NotificationResponseDTO> rawDtoList; // giữ id gốc để gọi API
    private NotificationRepository notificationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_notification);

        notificationRepository = new NotificationRepository();

        initViews();
        setupBottomNav();
        loadNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rv_notifications);

        findViewById(R.id.tv_mark_all_read).setOnClickListener(v -> markAllAsRead());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_notification_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadNotifications() {
        notificationRepository.getHistory(null, null, new ApiCallback<List<NotificationResponseDTO>>() {
            @Override
            public void onSuccess(List<NotificationResponseDTO> data) {
                rawDtoList = data;
                notificationList = convertToDisplayList(data);
                runOnUiThread(() -> {
                    adapter = new SellerNotificationAdapter(notificationList, position -> onItemClick(position));
                    rvNotifications.setLayoutManager(new LinearLayoutManager(SellerNotificationActivity.this));
                    rvNotifications.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(SellerNotificationActivity.this,
                                "Lỗi tải thông báo: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Chuyển đổi DTO từ server sang model hiển thị SellerNotification.
     * type được map từ String (ORDER, PROMO, SYSTEM, REVIEW) sang int.
     */
    private List<SellerNotification> convertToDisplayList(List<NotificationResponseDTO> dtoList) {
        List<SellerNotification> result = new ArrayList<>();
        if (dtoList == null) return result;
        for (NotificationResponseDTO dto : dtoList) {
            int typeInt = mapTypeStringToInt(dto.getType());
            boolean isRead = dto.getIsRead() != null && dto.getIsRead();
            String time = formatTime(dto.getCreatedAt());
            result.add(new SellerNotification(
                    String.valueOf(dto.getId()),
                    dto.getTitle(),
                    dto.getMessage(),
                    time,
                    isRead,
                    typeInt
            ));
        }
        return result;
    }

    private int mapTypeStringToInt(String type) {
        if (type == null) return 3;
        switch (type.toUpperCase()) {
            case "ORDER":   return 1;
            case "PROMO":   return 2;
            case "REVIEW":  return 4;
            default:        return 3; // SYSTEM
        }
    }

    /**
     * Format ISO timestamp thành chuỗi hiển thị thân thiện.
     * Ví dụ: "2024-06-20T10:30:00" → "20/06/2024 10:30"
     */
    private String formatTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "";
        try {
            // createdAt dạng "yyyy-MM-dd'T'HH:mm:ss" hoặc tương tự
            String datePart = createdAt.substring(0, 10);   // "yyyy-MM-dd"
            String timePart = createdAt.length() >= 16 ? createdAt.substring(11, 16) : "";
            String[] dateParts = datePart.split("-");
            if (dateParts.length == 3) {
                return dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0]
                        + (timePart.isEmpty() ? "" : " " + timePart);
            }
        } catch (Exception ignored) {}
        return createdAt;
    }

    /** Gọi khi người dùng bấm vào 1 item — đánh dấu đã đọc trên server và UI */
    private void onItemClick(int position) {
        if (notificationList == null || position >= notificationList.size()) return;
        SellerNotification item = notificationList.get(position);
        if (item.isRead()) return;

        Long id = Long.parseLong(item.getId());
        notificationRepository.markAsRead(id, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                item.setRead(true);
                runOnUiThread(() -> adapter.notifyItemChanged(position));
            }

            @Override
            public void onError(String message) {
                // Đánh dấu local trước, bỏ qua lỗi mạng nhẹ
                item.setRead(true);
                runOnUiThread(() -> adapter.notifyItemChanged(position));
            }
        });
    }

    private void markAllAsRead() {
        notificationRepository.markAllAsRead(new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (notificationList != null) {
                    for (SellerNotification n : notificationList) {
                        n.setRead(true);
                    }
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(SellerNotificationActivity.this,
                                "Lỗi: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
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
