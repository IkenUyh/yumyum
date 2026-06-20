package com.uit.fooddelivery_api.modules.notification.services;

import com.uit.fooddelivery_api.modules.notification.dtos.NotificationResponseDTO;
import com.uit.fooddelivery_api.modules.notification.entities.Notification;
import com.uit.fooddelivery_api.modules.notification.repositories.NotificationRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Lưu trữ các kết nối đang mở của Client. Dùng ConcurrentHashMap để Thread-safe (Chống đụng độ luồng)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 1. Khách hàng gọi hàm này để mở kết nối (Subscribe) và giữ nguyên đường truyền
    public SseEmitter subscribe(Long userId) {
        // Tạo Emitter với timeout 30 phút. Nếu quá 30 phút mà ko có ai làm gì nó tự đóng để đỡ tốn RAM
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);

        // Xử lý dọn dẹp bộ nhớ khi kết nối bị ngắt
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            // Gửi một sự kiện rác ban đầu để báo cho Frontend biết kết nối đã thành công
            emitter.send(SseEmitter.event().name("INIT").data("Kết nối Real-time thành công!"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    // 2. Hàm lõi: Lưu DB và Bắn Real-time
    @Transactional
    public void pushNotification(Long userId, String title, String message, String type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        // Bước A: Lưu vào Database để không bao giờ mất thông báo
        Notification noti = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        Notification savedNoti = notificationRepository.save(noti);

        // Bước B: Nếu khách hàng ĐANG ONLINE (Có Emitter trong Map), bắn qua Mạng ngay lập tức!
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                // Đóng gói DTO và bắn đi
                NotificationResponseDTO payload = NotificationResponseDTO.fromEntity(savedNoti);
                emitter.send(SseEmitter.event().name("NEW_NOTIFICATION").data(payload));
            } catch (IOException e) {
                // Nếu đang bắn mà rớt mạng thì xóa connection
                emitters.remove(userId);
            }
        }
    }

    /**
     * Lấy lịch sử thông báo.
     * Nếu month và year được truyền vào thì chỉ lấy thông báo trong tháng đó.
     * Nếu không truyền (null) thì lấy thông báo trong 30 ngày gần nhất mặc định.
     */
    public List<Notification> getMyHistory(Long userId, Integer month, Integer year) {
        if (month != null && year != null) {
            // Lấy theo tháng cụ thể
            YearMonth ym = YearMonth.of(year, month);
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to = ym.atEndOfMonth().atTime(23, 59, 59);
            return notificationRepository.findByUserIdInDateRange(userId, from, to);
        }
        // Mặc định: lấy 30 ngày gần nhất
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now();
        return notificationRepository.findByUserIdInDateRange(userId, from, to);
    }

    // Lấy số lượng thông báo chưa đọc
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Đánh dấu 1 thông báo là đã đọc
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification noti = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo!"));
        if (!noti.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền thao tác thông báo này!");
        }
        noti.setIsRead(true);
        notificationRepository.save(noti);
    }

    // Đánh dấu TẤT CẢ thông báo của user là đã đọc
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadList = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification noti : unreadList) {
            noti.setIsRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }
}