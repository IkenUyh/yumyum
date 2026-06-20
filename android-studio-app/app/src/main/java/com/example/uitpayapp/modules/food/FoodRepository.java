package com.example.uitpayapp.modules.food;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.food.models.requests.CreateFoodRequest;
import com.example.uitpayapp.modules.food.models.requests.CreateOptionGroupRequest;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
import com.example.uitpayapp.modules.food.models.responses.OptionGroupResponse;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodRepository {

    private final FoodService foodService;

    public FoodRepository() {
        this.foodService = RetrofitClient.getFoodService();
    }

    /**
     * Hàm Generic hỗ trợ đóng gói logic Enqueue tuần tự, giảm trùng lặp mã nguồn
     */
    private <T> void handleCall(Call<ApiResponse<T>> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<T> apiResponse = response.body();
                    // Kiểm tra trạng thái dữ liệu bên trong bọc ApiResponse tùy thuộc kiến trúc thực tế của bạn
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

    public void createFood(CreateFoodRequest request, ApiCallback<FoodResponse> callback) {
        handleCall(foodService.createFood(request), callback);
    }

    public void uploadFoodImage(Long foodId, MultipartBody.Part file, ApiCallback<String> callback) {
        handleCall(foodService.uploadFoodImage(foodId, file), callback);
    }

    public void updateFood(Long foodId, CreateFoodRequest request, ApiCallback<FoodResponse> callback) {
        handleCall(foodService.updateFood(foodId, request), callback);
    }

    public void deleteFood(Long foodId, ApiCallback<String> callback) {
        handleCall(foodService.deleteFood(foodId), callback);
    }

    public void addOptionGroup(Long foodId, CreateOptionGroupRequest request, ApiCallback<OptionGroupResponse> callback) {
        handleCall(foodService.addOptionGroup(foodId, request), callback);
    }

    public void getFoodOptions(Long foodId, ApiCallback<List<OptionGroupResponse>> callback) {
        handleCall(foodService.getFoodOptions(foodId), callback);
    }

    public void getRestaurantMenu(Long restaurantId, ApiCallback<List<FoodResponse>> callback) {
        handleCall(foodService.getRestaurantMenu(restaurantId), callback);
    }

    public void updateOptionGroup(Long groupId, CreateOptionGroupRequest request, ApiCallback<OptionGroupResponse> callback) {
        handleCall(foodService.updateOptionGroup(groupId, request), callback);
    }

    public void addOptionItem(Long groupId, com.example.uitpayapp.modules.food.models.requests.CreateOptionItemRequest request, ApiCallback<com.example.uitpayapp.modules.food.models.responses.OptionItemResponse> callback) {
        handleCall(foodService.addOptionItem(groupId, request), callback);
    }

    public void updateOptionItem(Long itemId, com.example.uitpayapp.modules.food.models.requests.CreateOptionItemRequest request, ApiCallback<com.example.uitpayapp.modules.food.models.responses.OptionItemResponse> callback) {
        handleCall(foodService.updateOptionItem(itemId, request), callback);
    }

    public void deleteOptionGroup(Long groupId, ApiCallback<String> callback) {
        handleCall(foodService.deleteOptionGroup(groupId), callback);
    }

    public void deleteOptionItem(Long itemId, ApiCallback<String> callback) {
        handleCall(foodService.deleteOptionItem(itemId), callback);
    }

    public void searchFoodsByKeyword(String keyword, ApiCallback<List<FoodResponse>> callback) {
        handleCall(foodService.searchFoodsByKeyword(keyword), callback);
    }
}