package com.example.uitpayapp.modules.favorite;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.favorite.models.FavoriteRestaurantResponseDTO;
import com.example.uitpayapp.modules.favorite.models.FavoriteStatusResponseDTO;
import com.example.uitpayapp.modules.favorite.models.ToggleFavoriteResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRepository {
    private final FavoriteService favoriteService;

    public FavoriteRepository() {
        this.favoriteService = RetrofitClient.getFavoriteService();
    }

    public void toggleFavorite(Long restaurantId, final ApiCallback<ToggleFavoriteResponseDTO> callback) {
        favoriteService.toggleFavorite(restaurantId).enqueue(new Callback<ApiResponse<ToggleFavoriteResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<ToggleFavoriteResponseDTO>> call, Response<ApiResponse<ToggleFavoriteResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<ToggleFavoriteResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMyFavorites(final ApiCallback<List<FavoriteRestaurantResponseDTO>> callback) {
        favoriteService.getMyFavorites().enqueue(new Callback<ApiResponse<List<FavoriteRestaurantResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FavoriteRestaurantResponseDTO>>> call, Response<ApiResponse<List<FavoriteRestaurantResponseDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FavoriteRestaurantResponseDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getFavoriteStatus(Long restaurantId, final ApiCallback<FavoriteStatusResponseDTO> callback) {
        favoriteService.getFavoriteStatus(restaurantId).enqueue(new Callback<ApiResponse<FavoriteStatusResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<FavoriteStatusResponseDTO>> call, Response<ApiResponse<FavoriteStatusResponseDTO>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<FavoriteStatusResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.getData() != null) {
                callback.onSuccess(apiResponse.getData());
            } else {
                callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Thành công nhưng không có dữ liệu trả về.");
            }
        } else {
            callback.onError("Lỗi kết nối hệ thống: " + response.code());
        }
    }
}
