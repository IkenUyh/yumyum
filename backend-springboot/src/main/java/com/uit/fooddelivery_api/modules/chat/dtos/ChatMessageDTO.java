package com.uit.fooddelivery_api.modules.chat.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long orderId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}