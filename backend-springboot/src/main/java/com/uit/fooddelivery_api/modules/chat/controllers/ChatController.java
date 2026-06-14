package com.uit.fooddelivery_api.modules.chat.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.chat.dtos.ChatMessageDTO;
import com.uit.fooddelivery_api.modules.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1. API WEBSOCKET: Bắt tin nhắn và phát sóng (Broadcast) ngay lập tức
    // Frontend gửi tin nhắn vào đích: /app/chat.sendMessage/{orderId}
    @MessageMapping("/chat.sendMessage/{orderId}")
    @SendTo("/topic/order/{orderId}") // Trả thẳng về kênh này để tất cả máy đang lắng nghe nhận được
    public ChatMessageDTO sendMessage(
            @DestinationVariable Long orderId,
            @Payload ChatMessageDTO chatMessageRequest) {

        // Lưu tin nhắn vào DB và trả về đối tượng DTO hoàn chỉnh (Kèm ID và thời gian)
        return chatService.saveAndBroadcastMessage(orderId, chatMessageRequest.getSenderId(), chatMessageRequest.getContent());
    }

    // 2. REST API BÌNH THƯỜNG: Lấy lịch sử chat
    // Frontend gọi API này lúc vừa tải xong giao diện Chat để hiển thị các tin nhắn cũ
    @GetMapping("/api/v1/chat/history/{orderId}")
    public ApiResponse<List<ChatMessageDTO>> getChatHistory(@PathVariable Long orderId) {
        List<ChatMessageDTO> history = chatService.getChatHistory(orderId);
        return ApiResponse.success(history);
    }
}