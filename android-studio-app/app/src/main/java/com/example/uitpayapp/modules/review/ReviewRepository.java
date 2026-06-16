package com.example.uitpayapp.modules.review;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.modules.review.models.requests.CreateReviewRequest;
import com.example.uitpayapp.modules.review.models.responses.ReviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private final ReviewService reviewService;

    public ReviewRepository(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void submitReview(String token, CreateReviewRequest request, final ApiCallback<ReviewResponse> callback) {
        // Đảm bảo định dạng Bearer Token nếu backend yêu cầu
        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        reviewService.submitReview(authToken, request).enqueue(new Callback<ApiResponse<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponse>> call, Response<ApiResponse<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ReviewResponse> apiResponse = response.body();
                    // Bạn có thể tùy biến kiểm tra apiResponse.getCode() tùy logic dự án
                    callback.onSuccess(apiResponse.getData());
                } else {
                    callback.onError("Gửi đánh giá thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getReviewsByRestaurant(Long restaurantId, final ApiCallback<List<ReviewResponse>> callback) {
        reviewService.getReviewsByRestaurant(restaurantId).enqueue(new Callback<ApiResponse<List<ReviewResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewResponse>>> call, Response<ApiResponse<List<ReviewResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Lấy danh sách đánh giá thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReviewResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}