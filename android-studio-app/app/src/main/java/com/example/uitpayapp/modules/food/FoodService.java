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

    @PUT("api/v1/foods/{id}/status")
    Call<ApiResponse<String>> updateFoodStatus(
            @Path("id") Long foodId,
            @Query("isAvailable") boolean isAvailable
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

    @GET("api/v1/restaurants/{id}/foods")
    Call<ApiResponse<List<FoodResponse>>> getRestaurantMenu(
            @Path("id") Long restaurantId
    );

    @GET("api/v1/restaurants/{id}/foods?all=true")
    Call<ApiResponse<List<FoodResponse>>> getRestaurantMenuForMerchant(
            @Path("id") Long restaurantId
    );

    @PUT("api/v1/foods/options/groups/{groupId}")
    Call<ApiResponse<OptionGroupResponse>> updateOptionGroup(
            @Path("groupId") Long groupId,
            @Body CreateOptionGroupRequest request
    );

    @POST("api/v1/foods/options/groups/{groupId}/items")
    Call<ApiResponse<com.example.uitpayapp.modules.food.models.responses.OptionItemResponse>> addOptionItem(
            @Path("groupId") Long groupId,
            @Body com.example.uitpayapp.modules.food.models.requests.CreateOptionItemRequest request
    );

    @PUT("api/v1/foods/options/items/{itemId}")
    Call<ApiResponse<com.example.uitpayapp.modules.food.models.responses.OptionItemResponse>> updateOptionItem(
            @Path("itemId") Long itemId,
            @Body com.example.uitpayapp.modules.food.models.requests.CreateOptionItemRequest request
    );

    @DELETE("api/v1/foods/options/groups/{groupId}")
    Call<ApiResponse<String>> deleteOptionGroup(
            @Path("groupId") Long groupId
    );

    @DELETE("api/v1/foods/options/items/{itemId}")
    Call<ApiResponse<String>> deleteOptionItem(
            @Path("itemId") Long itemId
    );

    @GET("api/v1/foods/keyword")
    Call<ApiResponse<List<FoodResponse>>> searchFoodsByKeyword(
            @Query("q") String keyword
    );
}