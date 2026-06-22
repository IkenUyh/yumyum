package com.uit.fooddelivery_api.modules.notification.dtos;

import com.uit.fooddelivery_api.modules.notification.entities.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponseDTO fromEntity(Notification noti) {
        return NotificationResponseDTO.builder()
                .id(noti.getId())
                .title(noti.getTitle())
                .message(noti.getMessage())
                .type(noti.getType())
                .isRead(noti.getIsRead())
                .createdAt(noti.getCreatedAt())
                .build();
    }
}