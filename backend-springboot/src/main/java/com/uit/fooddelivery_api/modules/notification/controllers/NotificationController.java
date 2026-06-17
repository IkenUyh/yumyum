package com.uit.fooddelivery_api.modules.notification.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.notification.dtos.NotificationResponseDTO;
import com.uit.fooddelivery_api.modules.notification.services.NotificationService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // API 1: Frontend gọi API này để mở kết nối nghe (Subscribe).
    // Trả về luồng Text/Event-Stream thay vì JSON
    @GetMapping(value = "/subscribe", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return notificationService.subscribe(currentUser.getId());
    }

    // API 2: Lấy lịch sử thông báo
    @GetMapping("/history")
    public ApiResponse<List<NotificationResponseDTO>> getHistory(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<NotificationResponseDTO> list = notificationService.getMyHistory(currentUser.getId())
                .stream()
                .map(NotificationResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // API 3: Lấy số lượng thông báo chưa đọc
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ApiResponse.success(Map.of("unreadCount", count));
    }
}