package com.example.uitpayapp.home.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.example.uitpayapp.models.ApiResponse;

public interface HomeApiService {
    @GET("api/v1/home/core")
    Call<ApiResponse<HomeCoreResponse>> getHomeCore(@Query("addressId") String addressId);

    @GET("api/v1/home/brands")
    Call<ApiResponse<BrandResponse>> getPopularBrands(@Query("addressId") String addressId);

    @GET("api/v1/home/deals")
    Call<DealResponse> getRecommendedDeals(
            @Query("addressId") String addressId,
            @Query("tabId") int tabId,
            @Query("page") int page,
            @Query("size") int size
    );
}
