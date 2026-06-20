package com.example.uitpayapp.modules.statistic;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantDashboardResponse;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantDailyStatisticResponse;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantMonthlyStatisticResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StatisticService {

    // API lấy dữ liệu vẽ biểu đồ trang chủ của Chủ quán
    // Lưu ý: Token JWT sẽ được tự động đính kèm qua OkHttpClient Interceptor (nếu có)
    @GET("api/v1/statistics/merchant/dashboard")
    Call<ApiResponse<MerchantDashboardResponse>> getMerchantDashboard();

    @GET("api/v1/statistics/merchant/daily")
    Call<ApiResponse<MerchantDailyStatisticResponse>> getMerchantDailyStatistic(
            @Query("restaurantId") Long restaurantId,
            @Query("date") String date
    );

    @GET("api/v1/statistics/merchant/monthly")
    Call<ApiResponse<MerchantMonthlyStatisticResponse>> getMerchantMonthlyStatistic(
            @Query("restaurantId") Long restaurantId,
            @Query("month") int month,
            @Query("year") int year
    );
}