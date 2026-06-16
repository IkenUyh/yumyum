package com.example.uitpayapp.modules.news;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.news.models.NewsDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsService {

    @GET("api/v1/news")
    Call<ApiResponse<List<NewsDTO>>> getActiveNews();
}
