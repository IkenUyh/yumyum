package com.uit.fooddelivery_api.modules.favorite.repositories;

import com.uit.fooddelivery_api.modules.favorite.entities.FavoriteRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, Long> {

    // Lấy tất cả nhà hàng yêu thích của 1 user
    List<FavoriteRestaurant> findByUserId(Long userId);

    // Kiểm tra xem user đã thích nhà hàng này chưa
    Optional<FavoriteRestaurant> findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    // Kiểm tra tồn tại (dùng cho isFavorited response)
    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    // Xóa yêu thích theo user + restaurant
    void deleteByUserIdAndRestaurantId(Long userId, Long restaurantId);
}
