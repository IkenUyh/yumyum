package com.example.uitpayapp.modules.loyalty;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;

import com.example.uitpayapp.history.DealHistory;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoyaltyRepository {
    private final LoyaltyService loyaltyService;

    public LoyaltyRepository() {
        this.loyaltyService = RetrofitClient.getLoyaltyService();
    }

    public void getMyLoyaltyInfo(final ApiCallback<LoyaltyResponseDTO> callback) {
        loyaltyService.getMyLoyaltyInfo().enqueue(new Callback<ApiResponse<LoyaltyResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoyaltyResponseDTO>> call, Response<ApiResponse<LoyaltyResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<LoyaltyResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void dailyCheckIn(final ApiCallback<LoyaltyResponseDTO> callback) {
        loyaltyService.dailyCheckIn().enqueue(new Callback<ApiResponse<LoyaltyResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoyaltyResponseDTO>> call, Response<ApiResponse<LoyaltyResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<LoyaltyResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMyDeals(final ApiCallback<List<DealHistory>> callback) {
        loyaltyService.getMyDeals().enqueue(new Callback<ApiResponse<List<DealHistory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DealHistory>>> call, Response<ApiResponse<List<DealHistory>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DealHistory>>> call, Throwable t) {
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
