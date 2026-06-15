package com.example.uitpayapp.modules.statistic;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantDashboardResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatisticService {

    // API lấy dữ liệu vẽ biểu đồ trang chủ của Chủ quán
    // Lưu ý: Token JWT sẽ được tự động đính kèm qua OkHttpClient Interceptor (nếu có)
    @GET("api/v1/statistics/merchant/dashboard")
    Call<ApiResponse<MerchantDashboardResponse>> getMerchantDashboard();
}