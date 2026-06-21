package com.example.uitpayapp.modules.restaurant;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.restaurant.models.RestaurantDistanceViewDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantSearchService {

    @GET("api/v1/search/nearby")
    Call<ApiResponse<List<RestaurantDistanceViewDTO>>> getNearbyRestaurants(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("radius") double radiusKm
    );

    @GET("api/v1/search/keyword")
    Call<ApiResponse<List<RestaurantDistanceViewDTO>>> searchByKeyword(
            @Query("q") String keyword,
            @Query("lat") Double latitude,
            @Query("lng") Double longitude,
            @Query("radius") Double radiusKm
    );
}
