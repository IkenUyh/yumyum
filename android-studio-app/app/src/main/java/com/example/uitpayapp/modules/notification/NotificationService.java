package com.example.uitpayapp.modules.notification;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NotificationService {

    @GET("api/v1/notifications/history")
    Call<ApiResponse<List<NotificationResponseDTO>>> getHistory();

    @GET("api/v1/notifications/unread-count")
    Call<ApiResponse<Map<String, Long>>> getUnreadCount();
}
