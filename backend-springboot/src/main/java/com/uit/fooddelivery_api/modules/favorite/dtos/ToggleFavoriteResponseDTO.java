package com.uit.fooddelivery_api.modules.favorite.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response khi user bấm tim - cho biết trạng thái yêu thích hiện tại
 */
@Getter
@Setter
@Builder
public class ToggleFavoriteResponseDTO {
    private boolean isFavorited;    // true = đã thêm vào yêu thích, false = đã bỏ yêu thích
    private String message;         // "Đã thêm vào yêu thích!" hoặc "Đã bỏ yêu thích!"
    private Long restaurantId;
}
