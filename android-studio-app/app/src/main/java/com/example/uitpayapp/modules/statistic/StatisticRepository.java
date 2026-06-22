package com.example.uitpayapp.modules.statistic;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantDashboardResponse;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantDailyStatisticResponse;
import com.example.uitpayapp.modules.statistic.models.responses.MerchantMonthlyStatisticResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticRepository {
    private final com.example.uitpayapp.modules.statistic.StatisticService statisticService;

    public StatisticRepository() {
        this.statisticService = RetrofitClient.getStatisticService();
    }

    public void getMerchantDashboard(final ApiCallback<MerchantDashboardResponse> callback) {
        statisticService.getMerchantDashboard().enqueue(new Callback<ApiResponse<MerchantDashboardResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MerchantDashboardResponse>> call, Response<ApiResponse<MerchantDashboardResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<MerchantDashboardResponse> apiResponse = response.body();

                    // Giả định backend trả về mã thành công (ví dụ: 200 hoặc tùy bạn cấu hình ở lớp ApiResponse)
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Dữ liệu trống trống");
                    }
                } else {
                    callback.onError("Lỗi kết nối hệ thống: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MerchantDashboardResponse>> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getMerchantDailyStatistic(Long restaurantId, String date, final ApiCallback<MerchantDailyStatisticResponse> callback) {
        statisticService.getMerchantDailyStatistic(restaurantId, date).enqueue(new Callback<ApiResponse<MerchantDailyStatisticResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MerchantDailyStatisticResponse>> call, Response<ApiResponse<MerchantDailyStatisticResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<MerchantDailyStatisticResponse> apiResponse = response.body();
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Dữ liệu trống");
                    }
                } else {
                    callback.onError("Lỗi kết nối hệ thống: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MerchantDailyStatisticResponse>> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getMerchantMonthlyStatistic(Long restaurantId, int month, int year,
                                             final ApiCallback<MerchantMonthlyStatisticResponse> callback) {
        statisticService.getMerchantMonthlyStatistic(restaurantId, month, year)
                .enqueue(new Callback<ApiResponse<MerchantMonthlyStatisticResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MerchantMonthlyStatisticResponse>> call,
                                           Response<ApiResponse<MerchantMonthlyStatisticResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<MerchantMonthlyStatisticResponse> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                callback.onSuccess(apiResponse.getData());
                            } else {
                                callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Dữ liệu trống");
                            }
                        } else {
                            callback.onError("Lỗi kết nối hệ thống: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MerchantMonthlyStatisticResponse>> call, Throwable t) {
                        callback.onError("Lỗi mạng: " + t.getMessage());
                    }
                });
    }
}