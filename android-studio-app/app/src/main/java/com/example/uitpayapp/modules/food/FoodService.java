package com.example.uitpayapp.modules.food;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.requests.CreateFoodRequest;
import com.example.uitpayapp.modules.food.models.requests.CreateOptionGroupRequest;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
import com.example.uitpayapp.modules.food.models.responses.OptionGroupResponse;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FoodService {

    @POST("api/v1/foods")
    Call<ApiResponse<FoodResponse>> createFood(
            @Body CreateFoodRequest request
    );

    @Multipart
    @POST("api/v1/foods/{id}/upload-image")
    Call<ApiResponse<String>> uploadFoodImage(
            @Path("id") Long foodId,
            @Part MultipartBody.Part file
    );

    @PUT("api/v1/foods/{id}")
    Call<ApiResponse<FoodResponse>> updateFood(
            @Path("id") Long foodId,
            @Body CreateFoodRequest request
    );

    @DELETE("api/v1/foods/{id}")
    Call<ApiResponse<String>> deleteFood(
            @Path("id") Long foodId
    );

    @POST("api/v1/foods/{foodId}/options")
    Call<ApiResponse<OptionGroupResponse>> addOptionGroup(
            @Path("foodId") Long foodId,
            @Body CreateOptionGroupRequest request
    );

    @GET("api/v1/foods/{foodId}/options")
    Call<ApiResponse<List<OptionGroupResponse>>> getFoodOptions(
            @Path("foodId") Long foodId
    );
}