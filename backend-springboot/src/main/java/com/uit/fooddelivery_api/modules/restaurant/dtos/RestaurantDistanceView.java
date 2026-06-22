package com.uit.fooddelivery_api.modules.restaurant.dtos;

import java.math.BigDecimal;

// Đây là Interface đặc biệt, Spring Data JPA sẽ tự động map kết quả SQL vào các hàm get này
public interface RestaurantDistanceView {
    Long getId();
    String getName();
    String getAddress();
    String getImageUrl();
    BigDecimal getRatingAverage();
    Integer getReviewCount();
    Double getDistance(); // Khoảng cách tính bằng Km trả về từ MySQL
}