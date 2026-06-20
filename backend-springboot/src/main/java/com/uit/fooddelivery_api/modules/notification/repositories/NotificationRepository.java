package com.uit.fooddelivery_api.modules.notification.repositories;

import com.uit.fooddelivery_api.modules.notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Lấy danh sách thông báo của 1 user, xếp cái mới nhất lên đầu
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

    // Đếm số lượng thông báo chưa đọc để hiển thị chấm đỏ (Badge) trên App
    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.user.id = :userId")
    void markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    void deleteByUserId(Long userId);
}