package com.uit.fooddelivery_api.modules.chat.repositories;

import com.uit.fooddelivery_api.modules.chat.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    // Lấy toàn bộ lịch sử chat của 1 đơn hàng, xếp theo thứ tự thời gian (cũ -> mới)
    List<ChatMessage> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}