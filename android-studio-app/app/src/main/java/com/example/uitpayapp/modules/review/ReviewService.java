package com.example.uitpayapp.modules.review;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.review.models.requests.CreateReviewRequest;
import com.example.uitpayapp.modules.review.models.responses.ReviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewService {

    // API Khách hàng gửi đánh giá (Yêu cầu Authentication Token ở Header)
    @POST("api/v1/reviews")
    Call<ApiResponse<ReviewResponse>> submitReview(
            @Body CreateReviewRequest request
    );

    // API Xem tất cả đánh giá của 1 quán ăn (Công khai)
    @GET("api/v1/reviews/restaurant/{id}")
    Call<ApiResponse<List<ReviewResponse>>> getReviewsByRestaurant(
            @Path("id") Long restaurantId
    );

    // API Chủ quán trả lời đánh giá
    @POST("api/v1/reviews/{id}/reply")
    Call<ApiResponse<ReviewResponse>> replyReview(
            @Path("id") Long reviewId,
            @Body com.example.uitpayapp.modules.review.models.requests.ReplyReviewRequest request
    );
}