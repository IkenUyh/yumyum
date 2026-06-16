package com.example.uitpayapp.modules.notification;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface NotificationService {

    @GET("api/v1/notifications/history")
    Call<ApiResponse<List<NotificationResponseDTO>>> getHistory();
}
