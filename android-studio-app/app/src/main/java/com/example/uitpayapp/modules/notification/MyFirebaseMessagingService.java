package com.example.uitpayapp.modules.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "YumYum_Notification_Channel";
    private static final String CHANNEL_NAME = "YumYum Notifications";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed FCM token: " + token);
        
        SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
        sessionManager.saveFcmToken(token);
        sessionManager.setFcmTokenSynced(false);

        if (sessionManager.isLoggedIn()) {
            syncTokenWithServer(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains notification payload
        String title = null;
        String body = null;
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        } else if (remoteMessage.getData().containsKey("title") && remoteMessage.getData().containsKey("body")) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        }

        if (title != null && body != null) {
            sendNotification(title, body);
            com.example.uitpayapp.utils.NotificationBadgeHelper.sendUpdateBroadcast(getApplicationContext());
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Gắn cờ FLAG_IMMUTABLE hoặc FLAG_MUTABLE tùy theo API level
        int pendingFlags = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingFlags);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notifications_24px) // Dùng icon thông báo đã có sẵn
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Khởi tạo channel cho Android Oreo (SDK 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for YumYum food delivery status updates");
            channel.enableLights(true);
            channel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }

    private void syncTokenWithServer(String token) {
        NotificationRepository repo = new NotificationRepository();
        repo.registerFcmToken(token, new ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Log.d(TAG, "Successfully synced FCM token with backend server.");
                SessionManager.getInstance(getApplicationContext()).setFcmTokenSynced(true);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to sync FCM token with backend server: " + errorMessage);
                SessionManager.getInstance(getApplicationContext()).setFcmTokenSynced(false);
            }
        });
    }
}
