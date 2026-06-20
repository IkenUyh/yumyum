package com.uit.fooddelivery_api.modules.notification.repositories;

import com.uit.fooddelivery_api.modules.notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy toàn bộ thông báo của 1 user (không giới hạn)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Lấy thông báo trong khoảng thời gian (dùng khi lọc theo tháng)
    @Query("SELECT n FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "AND n.createdAt >= :from AND n.createdAt <= :to " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdInDateRange(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Lấy danh sách thông báo chưa đọc (dùng để mark-all-read)
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    // Đếm số lượng thông báo chưa đọc để hiển thị Badge trên App
    long countByUserIdAndIsReadFalse(Long userId);
}