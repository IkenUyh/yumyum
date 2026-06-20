package com.uit.fooddelivery_api.modules.notification.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.notification.dtos.NotificationResponseDTO;
import com.uit.fooddelivery_api.modules.notification.services.NotificationService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/history")
    public ApiResponse<List<NotificationResponseDTO>> getHistory(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<NotificationResponseDTO> list = notificationService.getMyHistory(currentUser.getId(), month, year)
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

    // API 4: Đánh dấu 1 thông báo là đã đọc
    @PatchMapping("/mark-read/{id}")
    public ApiResponse<String> markAsRead(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markAsRead(id, currentUser.getId());
        return ApiResponse.success("Cập nhật thành công");
    }

    // API 5: Đánh dấu tất cả là đã đọc
    @PatchMapping("/mark-all-read")
    public ApiResponse<String> markAllAsRead(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markAllAsRead(currentUser.getId());
        return ApiResponse.success("Đã đánh dấu tất cả là đã đọc");
    }

    // API 6: Xóa 1 thông báo
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNotification(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.deleteNotification(id, currentUser.getId());
        return ApiResponse.success("Đã xóa thông báo thành công");
    }

    // API 7: Xóa tất cả thông báo
    @DeleteMapping("/all")
    public ApiResponse<String> deleteAllNotifications(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.deleteAllNotifications(currentUser.getId());
        return ApiResponse.success("Đã xóa toàn bộ thông báo thành công");
    }
}