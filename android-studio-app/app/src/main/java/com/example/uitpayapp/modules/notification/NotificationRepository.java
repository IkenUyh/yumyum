package com.example.uitpayapp.modules.notification;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {
    private final NotificationService notificationService;

    public NotificationRepository() {
        this.notificationService = RetrofitClient.getNotificationService();
    }

    public void getHistory(String token, final ApiCallback<List<NotificationResponseDTO>> callback) {
        notificationService.getHistory(token).enqueue(new Callback<ApiResponse<List<NotificationResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationResponseDTO>>> call, Response<ApiResponse<List<NotificationResponseDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationResponseDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.getData() != null) {
                callback.onSuccess(apiResponse.getData());
            } else if (apiResponse.getMessage() != null) {
                callback.onSuccess((T) apiResponse.getMessage());
            } else {
                callback.onError("Không nhận được phản hồi dữ liệu hợp lệ.");
            }
        } else {
            callback.onError("Lỗi kết nối hệ thống: " + response.code());
        }
    }
}
