package com.example.uitpayapp.modules.loyalty;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO;

import com.example.uitpayapp.history.DealHistory;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoyaltyService {

    @GET("api/v1/loyalty/me")
    Call<ApiResponse<LoyaltyResponseDTO>> getMyLoyaltyInfo();

    @POST("api/v1/loyalty/checkin")
    Call<ApiResponse<LoyaltyResponseDTO>> dailyCheckIn();

    @GET("api/v1/loyalty/deals")
    Call<ApiResponse<List<DealHistory>>> getMyDeals();
}
