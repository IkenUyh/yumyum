package com.uit.fooddelivery_api.modules.chat.services;

import com.uit.fooddelivery_api.modules.chat.dtos.ChatMessageDTO;
import com.uit.fooddelivery_api.modules.chat.entities.ChatMessage;
import com.uit.fooddelivery_api.modules.chat.repositories.ChatRepository;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageDTO saveAndBroadcastMessage(Long orderId, Long senderId, String content) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));

        // Bảo mật cực gắt: Khóa phòng chat nếu đơn đã hoàn thành hoặc hủy
        if (order.getStatus().equals("COMPLETED") || order.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Phòng chat đã bị đóng do đơn hàng đã kết thúc!");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại!"));

        // Chỉ khách đặt đơn hoặc tài xế/quán mới được nhắn
        boolean isCustomer = order.getUser().getId().equals(senderId);
        boolean isDriver = order.getDriver() != null && order.getDriver().getId().equals(senderId);
        boolean isMerchant = order.getRestaurant().getMerchant().getId().equals(senderId);

        if (!isCustomer && !isDriver && !isMerchant) {
            throw new RuntimeException("Bạn không có quyền tham gia phòng chat này!");
        }

        ChatMessage message = ChatMessage.builder()
                .order(order)
                .sender(sender)
                .content(content)
                .build();

        ChatMessage saved = chatRepository.save(message);

        return ChatMessageDTO.builder()
                .id(saved.getId())
                .orderId(order.getId())
                .senderId(sender.getId())
                .senderName(sender.getFullName())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt() != null ? saved.getCreatedAt() : java.time.LocalDateTime.now())
                .build();
    }

    public List<ChatMessageDTO> getChatHistory(Long orderId) {
        return chatRepository.findByOrderIdOrderByCreatedAtAsc(orderId).stream()
                .map(msg -> ChatMessageDTO.builder()
                        .id(msg.getId())
                        .orderId(msg.getOrder().getId())
                        .senderId(msg.getSender().getId())
                        .senderName(msg.getSender().getFullName())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}