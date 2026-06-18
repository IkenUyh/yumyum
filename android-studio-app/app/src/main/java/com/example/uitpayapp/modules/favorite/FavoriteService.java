package com.example.uitpayapp.modules.favorite;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.favorite.models.FavoriteRestaurantResponseDTO;
import com.example.uitpayapp.modules.favorite.models.FavoriteStatusResponseDTO;
import com.example.uitpayapp.modules.favorite.models.ToggleFavoriteResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FavoriteService {

    @POST("api/v1/favorites/restaurants/{restaurantId}/toggle")
    Call<ApiResponse<ToggleFavoriteResponseDTO>> toggleFavorite(
            @Path("restaurantId") Long restaurantId
    );

    @GET("api/v1/favorites/restaurants")
    Call<ApiResponse<List<FavoriteRestaurantResponseDTO>>> getMyFavorites();

    @GET("api/v1/favorites/restaurants/{restaurantId}/status")
    Call<ApiResponse<FavoriteStatusResponseDTO>> getFavoriteStatus(
            @Path("restaurantId") Long restaurantId
    );
}
