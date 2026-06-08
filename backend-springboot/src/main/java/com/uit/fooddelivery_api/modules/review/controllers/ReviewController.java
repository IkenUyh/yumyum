package com.uit.fooddelivery_api.modules.review.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.review.dtos.CreateReviewDTO;
import com.uit.fooddelivery_api.modules.review.dtos.ReviewResponseDTO;
import com.uit.fooddelivery_api.modules.review.entities.Review;
import com.uit.fooddelivery_api.modules.review.services.ReviewService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // API: Khách hàng gửi đánh giá
    @PostMapping
    public ApiResponse<ReviewResponseDTO> submitReview(
            Authentication authentication,
            @RequestBody CreateReviewDTO dto) {

        User customer = (User) authentication.getPrincipal();
        Review savedReview = reviewService.createReview(dto, customer);

        return ApiResponse.success(ReviewResponseDTO.fromEntity(savedReview));
    }

    // API: Xem tất cả đánh giá của 1 quán ăn (Công khai, không cần Token)
    @GetMapping("/restaurant/{id}")
    public ApiResponse<List<ReviewResponseDTO>> getReviewsByRestaurant(@PathVariable("id") Long restaurantId) {
        List<ReviewResponseDTO> list = reviewService.getRestaurantReviews(restaurantId)
                .stream()
                .map(ReviewResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }
}