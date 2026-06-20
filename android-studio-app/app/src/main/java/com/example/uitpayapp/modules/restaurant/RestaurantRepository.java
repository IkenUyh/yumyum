package com.example.uitpayapp.modules.restaurant;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
import com.example.uitpayapp.modules.restaurant.models.CreateRestaurantDTO;
import com.example.uitpayapp.modules.restaurant.models.DashboardResponseDTO;
import com.example.uitpayapp.modules.restaurant.models.RestaurantDistanceViewDTO;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.modules.restaurant.models.RestaurantSettingsDTO;
import com.example.uitpayapp.modules.restaurant.models.UpdateRestaurantInfoDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {
    private final RestaurantService restaurantService;
    private final RestaurantSearchService restaurantSearchService;

    public RestaurantRepository() {
        this.restaurantService = RetrofitClient.getRestaurantService();
        this.restaurantSearchService = RetrofitClient.getRestaurantSearchService();
    }

    // CRUD & Dashboard
    public void createRestaurant(CreateRestaurantDTO dto, final ApiCallback<RestaurantResponseDTO> callback) {
        restaurantService.createRestaurant(dto).enqueue(new Callback<ApiResponse<RestaurantResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<RestaurantResponseDTO>> call, Response<ApiResponse<RestaurantResponseDTO>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<RestaurantResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAllRestaurants(final ApiCallback<List<RestaurantResponseDTO>> callback) {
        restaurantService.getAllRestaurants().enqueue(new Callback<ApiResponse<List<RestaurantResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RestaurantResponseDTO>>> call, Response<ApiResponse<List<RestaurantResponseDTO>>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<List<RestaurantResponseDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getRestaurantMenu(Long restaurantId, final ApiCallback<List<FoodResponse>> callback) {
        restaurantService.getRestaurantMenu(restaurantId).enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodResponse>>> call, Response<ApiResponse<List<FoodResponse>>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<List<FoodResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateRestaurantSettings(Long restaurantId, RestaurantSettingsDTO dto, final ApiCallback<RestaurantResponseDTO> callback) {
        restaurantService.updateRestaurantSettings(restaurantId, dto).enqueue(new Callback<ApiResponse<RestaurantResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<RestaurantResponseDTO>> call, Response<ApiResponse<RestaurantResponseDTO>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<RestaurantResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMerchantDashboard(final ApiCallback<DashboardResponseDTO> callback) {
        restaurantService.getMerchantDashboard().enqueue(new Callback<ApiResponse<DashboardResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardResponseDTO>> call, Response<ApiResponse<DashboardResponseDTO>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<DashboardResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getRestaurantById(Long restaurantId, final ApiCallback<RestaurantResponseDTO> callback) {
        restaurantService.getRestaurantById(restaurantId).enqueue(new Callback<ApiResponse<RestaurantResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<RestaurantResponseDTO>> call, Response<ApiResponse<RestaurantResponseDTO>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<RestaurantResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateRestaurantInfo(Long restaurantId, UpdateRestaurantInfoDTO dto, final ApiCallback<RestaurantResponseDTO> callback) {
        restaurantService.updateRestaurantInfo(restaurantId, dto).enqueue(new Callback<ApiResponse<RestaurantResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<RestaurantResponseDTO>> call, Response<ApiResponse<RestaurantResponseDTO>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<RestaurantResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Search
    public void getNearbyRestaurants(double lat, double lng, double radiusKm, final ApiCallback<List<RestaurantDistanceViewDTO>> callback) {
        restaurantSearchService.getNearbyRestaurants(lat, lng, radiusKm).enqueue(new Callback<ApiResponse<List<RestaurantDistanceViewDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RestaurantDistanceViewDTO>>> call, Response<ApiResponse<List<RestaurantDistanceViewDTO>>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<List<RestaurantDistanceViewDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void searchByKeyword(String keyword, final ApiCallback<List<RestaurantDistanceViewDTO>> callback) {
        restaurantSearchService.searchByKeyword(keyword).enqueue(new Callback<ApiResponse<List<RestaurantDistanceViewDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RestaurantDistanceViewDTO>>> call, Response<ApiResponse<List<RestaurantDistanceViewDTO>>> response) {
                handleResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiResponse<List<RestaurantDistanceViewDTO>>> call, Throwable t) {
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
            String errorMessage = "Lỗi kết nối hệ thống: " + response.code();
            try {
                if (response.errorBody() != null) {
                    String errorBodyStr = response.errorBody().string();
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    ApiResponse<?> errorResponse = gson.fromJson(errorBodyStr, ApiResponse.class);
                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        errorMessage = errorResponse.getMessage();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.onError(errorMessage);
        }
    }
}
