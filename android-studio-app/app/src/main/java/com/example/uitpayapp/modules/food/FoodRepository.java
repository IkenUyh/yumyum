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
                callback.onError(t.toString());
            }
        });
    }

    public void createFood(String token, CreateFoodRequest request, ApiCallback<FoodResponse> callback) {
        handleCall(foodService.createFood(token, request), callback);
    }

    public void uploadFoodImage(String token, Long foodId, MultipartBody.Part file, ApiCallback<String> callback) {
        handleCall(foodService.uploadFoodImage(token, foodId, file), callback);
    }

    public void updateFood(String token, Long foodId, CreateFoodRequest request, ApiCallback<FoodResponse> callback) {
        handleCall(foodService.updateFood(token, foodId, request), callback);
    }

    public void deleteFood(String token, Long foodId, ApiCallback<String> callback) {
        handleCall(foodService.deleteFood(token, foodId), callback);
    }

    public void addOptionGroup(String token, Long foodId, CreateOptionGroupRequest request, ApiCallback<OptionGroupResponse> callback) {
        handleCall(foodService.addOptionGroup(token, foodId, request), callback);
    }

    public void getFoodOptions(Long foodId, ApiCallback<List<OptionGroupResponse>> callback) {
        handleCall(foodService.getFoodOptions(foodId), callback);
    }
}