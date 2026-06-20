package com.example.uitpayapp.modules.notification;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    @GET("api/v1/notifications/history")
    Call<ApiResponse<List<NotificationResponseDTO>>> getHistory(
            @Query("month") Integer month,
            @Query("year") Integer year
    );

    @GET("api/v1/notifications/unread-count")
    Call<ApiResponse<Map<String, Long>>> getUnreadCount();

    @PATCH("api/v1/notifications/mark-all-read")
    Call<ApiResponse<String>> markAllAsRead();

    @PATCH("api/v1/notifications/mark-read/{id}")
    Call<ApiResponse<String>> markAsRead(@Path("id") Long id);

    @DELETE("api/v1/notifications/all")
    Call<ApiResponse<String>> deleteAllNotifications();

    @DELETE("api/v1/notifications/{id}")
    Call<ApiResponse<String>> deleteNotification(@Path("id") Long id);
}
