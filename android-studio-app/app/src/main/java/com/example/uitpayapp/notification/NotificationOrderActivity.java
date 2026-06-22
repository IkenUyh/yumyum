package com.example.uitpayapp.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationOrderActivity extends AppCompatActivity {

    public interface OnNotificationReadListener {
        void onNotificationRead();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_order);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        TextView tvReadAll = findViewById(R.id.tvReadAll);

        RecyclerView rv = findViewById(R.id.rvOrderNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        com.example.uitpayapp.modules.notification.NotificationRepository notificationRepository = new com.example.uitpayapp.modules.notification.NotificationRepository();
        notificationRepository.getHistory(null, null, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.notification.models.NotificationResponseDTO> data) {
                List<OrderNotification> list = new ArrayList<>();
                int unreadCount = 0;
                for (com.example.uitpayapp.modules.notification.models.NotificationResponseDTO dto : data) {
                    if ("ORDER_UPDATE".equalsIgnoreCase(dto.getType()) || "SYSTEM".equalsIgnoreCase(dto.getType())) {
                        boolean isRead = dto.getIsRead() != null && dto.getIsRead();
                        if (!isRead) {
                            unreadCount++;
                        }
                        list.add(new OrderNotification(
                                String.valueOf(dto.getId()),
                                dto.getTitle(),
                                dto.getMessage(),
                                formatDateTime(dto.getCreatedAt()),
                                android.R.drawable.ic_menu_gallery,
                                isRead
                        ));
                    }
                }

                OrderAdapter adapter = new OrderAdapter(list, () -> {
                    int unread = 0;
                    for (OrderNotification noti : list) {
                        if (!noti.isRead()) {
                            unread++;
                        }
                    }
                    updateReadAllText(tvReadAll, unread);
                });
                rv.setAdapter(adapter);

                final int finalUnreadCount = unreadCount;
                updateReadAllText(tvReadAll, finalUnreadCount);

                tvReadAll.setOnClickListener(v -> {
                    if (list.isEmpty()) return;
                    notificationRepository.markAllAsRead(new com.example.uitpayapp.network.ApiCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            for (OrderNotification noti : list) {
                                noti.setRead(true);
                            }
                            adapter.notifyDataSetChanged();
                            updateReadAllText(tvReadAll, 0);
                            android.widget.Toast.makeText(NotificationOrderActivity.this, "Đã đánh dấu đọc tất cả thông báo", android.widget.Toast.LENGTH_SHORT).show();
                            com.example.uitpayapp.utils.NotificationBadgeHelper.sendUpdateBroadcast(NotificationOrderActivity.this);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            android.widget.Toast.makeText(NotificationOrderActivity.this, "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(NotificationOrderActivity.this, "Lỗi tải thông báo: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReadAllText(TextView tvReadAll, int count) {
        if (tvReadAll == null) return;
        if (count > 0) {
            tvReadAll.setText("Đọc tất cả (" + count + ")");
            tvReadAll.setEnabled(true);
            tvReadAll.setAlpha(1.0f);
        } else {
            tvReadAll.setText("Đọc tất cả");
            tvReadAll.setEnabled(false);
            tvReadAll.setAlpha(0.5f);
        }
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

    private static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        private List<OrderNotification> mList;
        private OnNotificationReadListener mListener;

        public OrderAdapter(List<OrderNotification> list, OnNotificationReadListener listener) {
            this.mList = list;
            this.mListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.activity_notification_order_item, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            OrderNotification m = mList.get(pos);
            h.t1.setText(m.getTitle());
            h.t2.setText(m.getContent());
            h.t3.setText(m.getTimestamp());
            h.iv.setImageResource(m.getShopImageResId());

            // Thay đổi giao diện dựa trên trạng thái đã đọc
            if (m.isRead()) {
                h.t1.setTypeface(null, android.graphics.Typeface.NORMAL);
                h.t1.setTextColor(android.graphics.Color.parseColor("#757575"));
                h.itemView.setBackgroundColor(android.graphics.Color.WHITE);
            } else {
                h.t1.setTypeface(null, android.graphics.Typeface.BOLD);
                h.t1.setTextColor(android.graphics.Color.parseColor("#212121"));
                h.itemView.setBackgroundColor(android.graphics.Color.parseColor("#FFF5F2")); // Nền cam nhạt cho thông báo chưa đọc
            }

            h.itemView.setOnClickListener(v -> {
                if (!m.isRead()) {
                    new com.example.uitpayapp.modules.notification.NotificationRepository().markAsRead(Long.parseLong(m.getId()), new com.example.uitpayapp.network.ApiCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            m.setRead(true);
                            notifyItemChanged(h.getAdapterPosition());
                            if (mListener != null) {
                                mListener.onNotificationRead();
                            }
                            com.example.uitpayapp.utils.NotificationBadgeHelper.sendUpdateBroadcast(v.getContext());
                        }

                        @Override
                        public void onError(String errorMessage) {}
                    });
                }
            });
        }

        @Override
        public int getItemCount() { return mList.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2, t3; ImageView iv;
            public ViewHolder(@NonNull View v) {
                super(v);
                t1 = v.findViewById(R.id.tvOrderTitle);
                t2 = v.findViewById(R.id.tvOrderContent);
                t3 = v.findViewById(R.id.tvOrderTime);
                iv = v.findViewById(R.id.ivShopThumb);
            }
        }
    }
}