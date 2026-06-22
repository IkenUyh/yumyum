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
        checkNotificationPermission();
        initWebSocket();
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
        adapter = new SellerNotificationAdapter(notificationList, position -> onItemClick(position));
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        com.example.uitpayapp.modules.notification.NotificationRepository repository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repository.getHistory(null, null, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO>>() {
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
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> android.widget.Toast.makeText(SellerNotificationActivity.this, "Lỗi tải thông báo: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void onItemClick(int position) {
        if (notificationList == null || position >= notificationList.size()) return;
        SellerNotification item = notificationList.get(position);
        if (item.isRead()) return;

        Long id = Long.parseLong(item.getId());
        com.example.uitpayapp.modules.notification.NotificationRepository repository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repository.markAsRead(id, new com.example.uitpayapp.network.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                item.setRead(true);
                runOnUiThread(() -> adapter.notifyItemChanged(position));
            }

            @Override
            public void onError(String errorMessage) {
                item.setRead(true);
                runOnUiThread(() -> adapter.notifyItemChanged(position));
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
            // outputFormat dùng device timezone mặc định → hiển thị giờ địa phương (VN)
            java.text.SimpleDateFormat outputFormat =
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());

            if (cleanStr.endsWith("Z")) {
                // Chuỗi kết thúc 'Z' → rõ ràng là UTC, parse UTC, hiển thị giờ device
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            } else if (cleanStr.contains("+") || (cleanStr.lastIndexOf("-") > 10)) {
                // Có offset timezone (vd: +07:00) → dùng XXX pattern, format ra device TZ
                try {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.getDefault());
                    java.util.Date date = inputFormat.parse(cleanStr);
                    return outputFormat.format(date);
                } catch (Exception ex) {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                    // Naive string → parse như UTC (backend serverTimezone=UTC)
                    inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                }
            } else {
                // Naive string (không có TZ suffix) từ backend → backend lưu UTC,
                // parse như UTC rồi để outputFormat chuyển sang giờ thiết bị
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            }
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

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void showNotification(String title, String message) {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        String channelId = "seller_notification_channel";
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId,
                    "Thông báo hệ thống",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        android.content.Intent intent = new android.content.Intent(this, SellerNotificationActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(this, 0, intent,
                android.app.PendingIntent.FLAG_ONE_SHOT | android.app.PendingIntent.FLAG_IMMUTABLE);

        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.yumyum_demo_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private ua.naiksoftware.stomp.StompClient mStompClient;

    @android.annotation.SuppressLint("CheckResult")
    private void initWebSocket() {
        android.content.SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        long currentStoreId = prefs.getLong("current_store_id", -1L);

        String baseUrl = com.example.uitpayapp.network.RetrofitClient.getBaseUrl();
        String wsUrl = baseUrl;
        if (wsUrl.startsWith("http://")) {
            wsUrl = "ws://" + wsUrl.substring(7);
        } else if (wsUrl.startsWith("https://")) {
            wsUrl = "wss://" + wsUrl.substring(8);
        }
        if (!wsUrl.endsWith("/")) {
            wsUrl += "/";
        }
        // URL chỉ đến /ws/chat — NaikSoftware Stomp tự append SockJS path nội bộ
        wsUrl += "ws/chat";

        // Thêm Authorization header vào HTTP WebSocket handshake
        com.example.uitpayapp.network.SessionManager sessionManager = com.example.uitpayapp.network.SessionManager.getInstance(this);
        String authToken = sessionManager.getAuthToken();
        java.util.Map<String, String> connectHttpHeaders = new java.util.HashMap<>();
        if (authToken != null && !authToken.isEmpty()) {
            connectHttpHeaders.put("Authorization", "Bearer " + authToken);
        }
        mStompClient = ua.naiksoftware.stomp.Stomp.over(ua.naiksoftware.stomp.Stomp.ConnectionProvider.OKHTTP, wsUrl, connectHttpHeaders);

        mStompClient.lifecycle()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            android.util.Log.d("WebSocket", "Stomp connection opened in Notifications");
                            break;
                        case ERROR:
                            android.util.Log.e("WebSocket", "Error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            android.util.Log.d("WebSocket", "Stomp connection closed in Notifications");
                            break;
                    }
                });

        if (currentStoreId != -1L) {
            mStompClient.topic("/topic/restaurant/" + currentStoreId + "/notifications")
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        android.util.Log.d("WebSocket", "Received notification: " + topicMessage.getPayload());
                        try {
                            org.json.JSONObject json = new org.json.JSONObject(topicMessage.getPayload());
                            String title = json.optString("title", "Thông báo mới");
                            String message = json.optString("message", json.optString("content", "Bạn có thông báo mới"));
                            showNotification(title, message);
                            loadRealData();
                        } catch (Exception e) {
                            showNotification("Thông báo mới", "Bạn có một thông báo từ hệ thống");
                            loadRealData();
                        }
                    }, throwable -> {
                        android.util.Log.e("WebSocket", "Error on subscribe topic", throwable);
                    });
        }

        // Gửi Authorization token trong STOMP CONNECT frame headers
        java.util.List<ua.naiksoftware.stomp.dto.StompHeader> stompHeaders = new java.util.ArrayList<>();
        String token = com.example.uitpayapp.network.SessionManager.getInstance(this).getAuthToken();
        if (token != null && !token.isEmpty()) {
            stompHeaders.add(new ua.naiksoftware.stomp.dto.StompHeader("Authorization", "Bearer " + token));
        }
        mStompClient.connect(stompHeaders);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStompClient != null) {
            mStompClient.disconnect();
            mStompClient = null;
        }
    }
}
