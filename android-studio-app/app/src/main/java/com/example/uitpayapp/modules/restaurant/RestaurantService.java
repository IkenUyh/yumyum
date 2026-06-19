package com.example.uitpayapp.modules.restaurant;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
import com.example.uitpayapp.modules.restaurant.models.CreateRestaurantDTO;
import com.example.uitpayapp.modules.restaurant.models.DashboardResponseDTO;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.modules.restaurant.models.RestaurantSettingsDTO;
import com.example.uitpayapp.modules.restaurant.models.UpdateRestaurantInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestaurantService {

    @POST("api/v1/restaurants")
    Call<ApiResponse<RestaurantResponseDTO>> createRestaurant(
            @Body CreateRestaurantDTO dto
    );

    @GET("api/v1/restaurants")
    Call<ApiResponse<List<RestaurantResponseDTO>>> getAllRestaurants();

    @GET("api/v1/restaurants/{id}")
    Call<ApiResponse<RestaurantResponseDTO>> getRestaurantById(
            @Path("id") Long restaurantId
    );

    @GET("api/v1/restaurants/{id}/foods")
    Call<ApiResponse<List<FoodResponse>>> getRestaurantMenu(
            @Path("id") Long restaurantId
    );

    @PUT("api/v1/restaurants/{id}/settings")
    Call<ApiResponse<RestaurantResponseDTO>> updateRestaurantSettings(
            @Path("id") Long restaurantId,
            @Body RestaurantSettingsDTO dto
    );

    @GET("api/v1/restaurants/{id}")
    Call<ApiResponse<RestaurantResponseDTO>> getRestaurantById(
            @Path("id") Long restaurantId
    );

    @PUT("api/v1/restaurants/{id}/info")
    Call<ApiResponse<RestaurantResponseDTO>> updateRestaurantInfo(
            @Path("id") Long restaurantId,
            @Body UpdateRestaurantInfoDTO dto
    );

    @GET("api/v1/dashboard/merchant")
    Call<ApiResponse<DashboardResponseDTO>> getMerchantDashboard();
}
