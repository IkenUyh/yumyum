package com.uit.fooddelivery_api.modules.restaurant.repositories;

import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // Công thức Haversine chuẩn: Bán kính Trái Đất = 6371 km.
    // Lọc ra các quán Đang mở cửa (isActive = true) và Đang nhận đơn (isAccepting = true),
    // khoảng cách < radius, và xếp gần nhất lên đầu.
    @Query(value = "SELECT id, name, address, image_url AS imageUrl, " +
            "rating_average AS ratingAverage, review_count AS reviewCount, " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:userLng)) + " +
            "sin(radians(:userLat)) * sin(radians(latitude)))) AS distance " +
            "FROM restaurants " +
            "WHERE is_active = true AND is_accepting_orders = true " +
            "HAVING distance <= :radius " +
            "ORDER BY distance ASC", nativeQuery = true)
    List<com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantDistanceView> findNearbyRestaurants(
            @Param("userLat") double userLat,
            @Param("userLng") double userLng,
            @Param("radius") double radiusKm);

    // -----------------------------------------------------
    // TÌM KIẾM THEO TỪ KHÓA CÓ HỖ TRỢ GÕ SAI (TYPO TOLERANCE)
    // -----------------------------------------------------
    @org.springframework.data.jpa.repository.Query(value = "SELECT id, name, address, image_url AS imageUrl, " +
            "rating_average AS ratingAverage, review_count AS reviewCount, " +
            "0.0 AS distance " + // Trả về 0.0 vì ta tìm theo tên, không có tọa độ người dùng ở đây
            "FROM restaurants " +
            "WHERE is_active = true AND is_accepting_orders = true " +
            "AND MATCH(name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
            "ORDER BY MATCH(name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) DESC", nativeQuery = true)
    java.util.List<com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantDistanceView>
    searchRestaurantsByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword);

    @org.springframework.data.jpa.repository.Query(value = "SELECT id, name, address, image_url AS imageUrl, " +
            "rating_average AS ratingAverage, review_count AS reviewCount, " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:userLng)) + " +
            "sin(radians(:userLat)) * sin(radians(latitude)))) AS distance " +
            "FROM restaurants " +
            "WHERE is_active = true AND is_accepting_orders = true " +
            "AND MATCH(name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
            "HAVING distance <= :radius " +
            "ORDER BY MATCH(name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) DESC, distance ASC", nativeQuery = true)
    java.util.List<com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantDistanceView> searchRestaurantsByKeywordAndLocation(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("userLat") double userLat,
            @org.springframework.data.repository.query.Param("userLng") double userLng,
            @org.springframework.data.repository.query.Param("radius") double radiusKm);

    @org.springframework.data.jpa.repository.Query(value = "SELECT id, name, address, image_url AS imageUrl, " +
            "rating_average AS ratingAverage, review_count AS reviewCount, " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:userLng)) + " +
            "sin(radians(:userLat)) * sin(radians(latitude)))) AS distance " +
            "FROM restaurants " +
            "WHERE id IN (:ids) AND is_active = true AND is_accepting_orders = true " +
            "HAVING distance <= :radius " +
            "ORDER BY distance ASC", nativeQuery = true)
    java.util.List<com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantDistanceView> findRestaurantsByIdsAndLocation(
            @org.springframework.data.repository.query.Param("ids") java.util.List<Long> ids,
            @org.springframework.data.repository.query.Param("userLat") double userLat,
            @org.springframework.data.repository.query.Param("userLng") double userLng,
            @org.springframework.data.repository.query.Param("radius") double radiusKm);

    @org.springframework.data.jpa.repository.Query(value = "SELECT id, name, address, image_url AS imageUrl, " +
            "rating_average AS ratingAverage, review_count AS reviewCount, " +
            "0.0 AS distance " +
            "FROM restaurants " +
            "WHERE id IN (:ids) AND is_active = true AND is_accepting_orders = true", nativeQuery = true)
    java.util.List<com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantDistanceView> findRestaurantsByIds(
            @org.springframework.data.repository.query.Param("ids") java.util.List<Long> ids);
}