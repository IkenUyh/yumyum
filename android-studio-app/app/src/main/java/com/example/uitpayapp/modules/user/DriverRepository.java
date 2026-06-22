package com.example.uitpayapp.modules.user;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRepository {
    private final DriverService driverService;

    public DriverRepository() {
        this.driverService = RetrofitClient.getDriverService();
    }

    /**
     * Gửi tọa độ GPS lên Server ngầm (Chạy mỗi 10s bên UI bằng Handler hoặc WorkManager)
     */
    public void updateLocation(double lat, double lng, ApiCallback<String> callback) {
        driverService.updateLocation(lat, lng).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError("Lỗi cập nhật vị trí: " + t.getMessage());
            }
        });
    }

    /**
     * Tắt trạng thái nhận chuyến, đưa tài xế về trạng thái ngoại tuyến
     */
    public void goOffline(ApiCallback<String> callback) {
        driverService.goOffline().enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError("Lỗi ngắt kết nối: " + t.getMessage());
            }
        });
    }

    // Bộ bóc dữ liệu tự động cho Generic Type String
    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.getCode() == 200 || apiResponse.getCode() == 0) {
                callback.onSuccess(apiResponse.getData());
            } else {
                callback.onError(apiResponse.getMessage());
            }
        } else {
            callback.onError("Hệ thống phản hồi lỗi: " + response.code());
        }
    }
}