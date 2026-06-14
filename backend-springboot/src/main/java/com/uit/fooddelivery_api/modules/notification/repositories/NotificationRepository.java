package com.uit.fooddelivery_api.modules.notification.repositories;

import com.uit.fooddelivery_api.modules.notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Lấy danh sách thông báo của 1 user, xếp cái mới nhất lên đầu
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Đếm số lượng thông báo chưa đọc để hiển thị chấm đỏ (Badge) trên App
    long countByUserIdAndIsReadFalse(Long userId);
}