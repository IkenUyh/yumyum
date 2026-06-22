package com.uit.fooddelivery_api.modules.review.repositories;

import com.uit.fooddelivery_api.modules.review.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Kiểm tra xem đơn hàng này đã được đánh giá chưa
    boolean existsByOrderId(Long orderId);

    // Lấy tất cả đánh giá của một quán ăn (xếp mới nhất lên đầu)
    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
}