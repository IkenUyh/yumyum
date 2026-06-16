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

    // THAY ĐỔI: Bỏ tham số 'String token' vì Interceptor đã tự động chèn vào Header ngầm
    public void submitReview(CreateReviewRequest request, final ApiCallback<ReviewResponse> callback) {

        // Gọi thẳng Service và truyền một mình 'request', không cần bận tâm về Token nữa
        reviewService.submitReview(request).enqueue(new Callback<ApiResponse<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponse>> call, Response<ApiResponse<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ReviewResponse> apiResponse = response.body();
                    // Callback trả dữ liệu sạch (ReviewResponse) về cho Activity hiển thị
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

    // API này không dùng token nên giữ nguyên cấu trúc chuẩn của bạn
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