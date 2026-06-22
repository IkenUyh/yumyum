package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
import com.example.uitpayapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {

    private static CategoryRepository instance;
    private final CategoryApiService apiService;

    private CategoryRepository() {
        this.apiService = RetrofitClient.getCategoryApiService();
    }

    public static CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    public interface CategoryFoodsCallback {
        void onSuccess(List<FoodMenuItem> foods);
        void onEmpty();
        void onError(String message);
    }

    public void getFoodsByCategory(Long categoryId, Double lat, Double lng, CategoryFoodsCallback callback) {
        apiService.getFoodsByCategory(categoryId, lat, lng).enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodResponse>>> call, Response<ApiResponse<List<FoodResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<FoodResponse> foodResponses = response.body().getData();
                    if (foodResponses.isEmpty()) {
                        callback.onEmpty();
                    } else {
                        List<FoodMenuItem> foods = new ArrayList<>();
                        for (FoodResponse fr : foodResponses) {
                            FoodMenuItem item = new FoodMenuItem(
                                    fr.getId() != null ? String.valueOf(fr.getId()) : "",
                                    fr.getName() != null ? fr.getName() : "",
                                    fr.getPrice() != null ? fr.getPrice().longValue() : 0L,
                                    0,
                                    fr.getDescription() != null ? fr.getDescription() : "",
                                    fr.getImageUrl() != null ? fr.getImageUrl() : ""
                            );
                            item.setRestaurantId(fr.getRestaurantId());
                            item.setRestaurantName(fr.getRestaurantName());
                            item.setDistance(fr.getDistance());
                            item.setReviewCount(fr.getReviewCount());
                            item.setRatingAverage(fr.getRatingAverage());
                            foods.add(item);
                        }
                        callback.onSuccess(foods);
                    }
                } else {
                    callback.onError("Không kết nối được server (Lỗi: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodResponse>>> call, Throwable t) {
                callback.onError("Không kết nối được server (" + t.getMessage() + ")");
            }
        });
    }
}
