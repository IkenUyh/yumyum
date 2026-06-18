package com.example.uitpayapp.home.network;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryApiService {
    @GET("api/v1/categories/{id}/foods")
    Call<ApiResponse<List<FoodResponse>>> getFoodsByCategory(@Path("id") Long categoryId);
}
