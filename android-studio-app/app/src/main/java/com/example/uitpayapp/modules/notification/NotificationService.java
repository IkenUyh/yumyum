package com.example.uitpayapp.modules.notification;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface NotificationService {

    @GET("api/v1/notifications/history")
    Call<ApiResponse<List<NotificationResponseDTO>>> getHistory();

    @GET("api/v1/notifications/unread-count")
    Call<ApiResponse<Map<String, Long>>> getUnreadCount();

    @PUT("api/v1/notifications/read-all")
    Call<ApiResponse<String>> markAllAsRead();

    @PUT("api/v1/notifications/{id}/read")
    Call<ApiResponse<String>> markAsRead(@Path("id") Long id);

    @DELETE("api/v1/notifications/all")
    Call<ApiResponse<String>> deleteAllNotifications();
}
