package com.uit.fooddelivery_api.modules.loyalty.services;

import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.repositories.LoyaltyPointRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyPointRepository loyaltyPointRepository;

    // Cấu hình điểm thưởng
    private static final int BASE_POINTS = 100; // Điểm danh cơ bản được 100 Xu
    private static final int STREAK_BONUS = 50; // Mỗi ngày liên tiếp được cộng thêm 50 Xu

    // Lấy thông tin điểm hiện tại (Áp dụng Lazy Initialization: Nếu user chưa có thì tạo mới mức 0)
    public LoyaltyPoint getMyLoyaltyInfo(User user) {
        return loyaltyPointRepository.findByUserId(user.getId())
                .orElseGet(() -> createInitialLoyalty(user));
    }

    // Kiểm tra xem hôm nay đã điểm danh chưa
    public boolean canCheckInToday(LoyaltyPoint loyaltyPoint) {
        if (loyaltyPoint.getLastCheckinDate() == null) return true;
        return !loyaltyPoint.getLastCheckinDate().equals(LocalDate.now());
    }

    // Xử lý logic Điểm danh
    @Transactional
    public LoyaltyPoint dailyCheckIn(User user) {
        LoyaltyPoint loyaltyPoint = getMyLoyaltyInfo(user);

        LocalDate today = LocalDate.now();
        LocalDate lastCheckin = loyaltyPoint.getLastCheckinDate();

        if (lastCheckin != null && lastCheckin.equals(today)) {
            throw new RuntimeException("Hôm nay bạn đã điểm danh rồi! Hãy quay lại vào ngày mai nhé.");
        }

        int currentStreak = loyaltyPoint.getCheckinStreak();

        // Kiểm tra xem có bị đứt chuỗi không (Nếu lần cuối điểm danh là ngày hôm qua thì chuỗi tiếp tục)
        if (lastCheckin != null && lastCheckin.equals(today.minusDays(1))) {
            currentStreak += 1;
        } else {
            // Đứt chuỗi, reset về 1
            currentStreak = 1;
        }

        // Tính toán số xu nhận được = Điểm cơ bản + (Chuỗi * Thưởng thêm)
        // Ví dụ ngày 1: 100 + (1 * 50) = 150 Xu. Ngày 2: 100 + (2 * 50) = 200 Xu.
        int earnedPoints = BASE_POINTS + (currentStreak * STREAK_BONUS);

        loyaltyPoint.setCurrentPoints(loyaltyPoint.getCurrentPoints() + earnedPoints);
        loyaltyPoint.setCheckinStreak(currentStreak);
        loyaltyPoint.setLastCheckinDate(today);

        return loyaltyPointRepository.save(loyaltyPoint);
    }

    private LoyaltyPoint createInitialLoyalty(User user) {
        LoyaltyPoint lp = LoyaltyPoint.builder()
                .user(user)
                .currentPoints(0)
                .checkinStreak(0)
                .build();
        return loyaltyPointRepository.save(lp);
    }
}