package com.example.uitpayapp.modules.news;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.news.models.NewsDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private final NewsService newsService;

    public NewsRepository() {
        this.newsService = RetrofitClient.getNewsService();
    }

    public void getActiveNews(final ApiCallback<List<NewsDTO>> callback) {
        newsService.getActiveNews().enqueue(new Callback<ApiResponse<List<NewsDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NewsDTO>>> call, Response<ApiResponse<List<NewsDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NewsDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.getData() != null) {
                callback.onSuccess(apiResponse.getData());
            } else if (apiResponse.getMessage() != null) {
                callback.onSuccess((T) apiResponse.getMessage());
            } else {
                callback.onError("Không nhận được phản hồi dữ liệu hợp lệ.");
            }
        } else {
            callback.onError("Lỗi kết nối hệ thống: " + response.code());
        }
    }
}
