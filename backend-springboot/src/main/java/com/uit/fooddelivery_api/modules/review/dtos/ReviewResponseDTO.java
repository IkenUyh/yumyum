package com.uit.fooddelivery_api.modules.review.dtos;

import com.uit.fooddelivery_api.modules.review.entities.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponseDTO {
    private Long id;
    private Long orderId;
    private Long restaurantId;
    private String customerName; // Tên khách hàng để quán còn biết ai khen/chê
    private Integer rating;
    private String comment;
    private String merchantReply;
    private LocalDateTime createdAt;

    public static ReviewResponseDTO fromEntity(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .restaurantId(review.getRestaurant().getId())
                .customerName(review.getOrder().getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .merchantReply(review.getMerchantReply())
                .createdAt(review.getCreatedAt())
                .build();
    }
}