package com.example.uitpayapp.modules.food;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.food.models.requests.CreateCategoryRequest;
import com.example.uitpayapp.modules.food.models.responses.CategoryResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {

    private final CategoryService categoryService;

    public CategoryRepository() {
        this.categoryService = RetrofitClient.getCategoryService();
    }

    private <T> void handleCall(Call<ApiResponse<T>> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<T> apiResponse = response.body();
                    if (apiResponse != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("Phản hồi hệ thống trống");
                    }
                } else {
                    callback.onError("Lỗi hệ thống: Mã HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAllCategories(ApiCallback<List<CategoryResponse>> callback) {
        handleCall(categoryService.getAllCategories(), callback);
    }

    public void getCategoriesByRestaurant(Long restaurantId, ApiCallback<List<CategoryResponse>> callback) {
        handleCall(categoryService.getCategoriesByRestaurant(restaurantId), callback);
    }

    public void getFoodsByCategory(Long categoryId, ApiCallback<List<FoodResponse>> callback) {
        handleCall(categoryService.getFoodsByCategory(categoryId), callback);
    }

    public void createCategory(CreateCategoryRequest request, ApiCallback<CategoryResponse> callback) {
        handleCall(categoryService.createCategory(request), callback);
    }

    public void updateCategory(Long categoryId, CreateCategoryRequest request, ApiCallback<CategoryResponse> callback) {
        handleCall(categoryService.updateCategory(categoryId, request), callback);
    }
}
