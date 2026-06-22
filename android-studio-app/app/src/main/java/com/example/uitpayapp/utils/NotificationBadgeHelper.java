package com.example.uitpayapp.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.uitpayapp.R;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;
import com.example.uitpayapp.modules.notification.NotificationRepository;
import java.util.Map;

public class NotificationBadgeHelper {
    public static final String ACTION_UPDATE_NOTIFICATION_BADGE = "com.example.uitpayapp.ACTION_UPDATE_NOTIFICATION_BADGE";

    public static void sendUpdateBroadcast(Context context) {
        if (context != null) {
            Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION_BADGE);
            context.sendBroadcast(intent);
        }
    }

    public static BroadcastReceiver registerBadgeReceiver(Activity activity, Runnable onReceiveUpdate) {
        if (activity == null) return null;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (onReceiveUpdate != null) {
                    onReceiveUpdate.run();
                }
            }
        };
        ContextCompat.registerReceiver(
                activity,
                receiver,
                new IntentFilter(ACTION_UPDATE_NOTIFICATION_BADGE),
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
        return receiver;
    }

    public static void updateBadge(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        final TextView tvNotificationBadge = activity.findViewById(R.id.tv_notification_badge);
        if (tvNotificationBadge == null) return;

        if (!SessionManager.getInstance(activity).isLoggedIn()) {
            activity.runOnUiThread(() -> tvNotificationBadge.setVisibility(View.GONE));
            return;
        }

        NotificationRepository repo = new NotificationRepository();
        repo.getUnreadCount(new ApiCallback<Map<String, Long>>() {
            @Override
            public void onSuccess(Map<String, Long> countData) {
                long unreadCount = 0;
                if (countData != null && countData.containsKey("unreadCount")) {
                    Object val = countData.get("unreadCount");
                    if (val instanceof Number) {
                        unreadCount = ((Number) val).longValue();
                    }
                }
                final long finalCount = unreadCount;
                activity.runOnUiThread(() -> {
                    if (finalCount > 0) {
                        tvNotificationBadge.setText(String.valueOf(finalCount));
                        tvNotificationBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvNotificationBadge.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                activity.runOnUiThread(() -> {
                    tvNotificationBadge.setVisibility(View.GONE);
                });
            }
        });
    }
}
