package com.uit.fooddelivery_api.modules.loyalty.services;

import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.repositories.LoyaltyPointRepository;
import com.uit.fooddelivery_api.modules.system.entities.SystemParameter;
import com.uit.fooddelivery_api.modules.system.repositories.SystemParameterRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final SystemParameterRepository systemParameterRepository;

    public LoyaltyPoint getMyLoyaltyInfo(User user) {
        return loyaltyPointRepository.findByUserId(user.getId())
                .orElseGet(() -> createInitialLoyalty(user));
    }

    public boolean canCheckInToday(LoyaltyPoint loyaltyPoint) {
        if (loyaltyPoint.getLastCheckinDate() == null) return true;
        return !loyaltyPoint.getLastCheckinDate().equals(LocalDate.now());
    }

    @Transactional
    public LoyaltyPoint dailyCheckIn(User user) {
        LoyaltyPoint loyaltyPoint = getMyLoyaltyInfo(user);
        LocalDate today = LocalDate.now();
        LocalDate lastCheckin = loyaltyPoint.getLastCheckinDate();

        if (lastCheckin != null && lastCheckin.equals(today)) {
            throw new RuntimeException("Hôm nay bạn đã điểm danh rồi! Hãy quay lại vào ngày mai nhé.");
        }

        int currentStreak = loyaltyPoint.getCheckinStreak();
        if (lastCheckin != null && lastCheckin.equals(today.minusDays(1))) {
            currentStreak += 1;
        } else {
            currentStreak = 1;
        }

        // LẤY CONFIG TỪ DB NGAY TRONG HÀM ĐỂ TRÁNH LỖI KHỞI TẠO BEAN
        int basePoints = Integer.parseInt(systemParameterRepository.findByParamKey("LOYALTY_BASE_POINTS")
                .map(SystemParameter::getParamValue).orElse("100"));
        int streakBonus = Integer.parseInt(systemParameterRepository.findByParamKey("LOYALTY_STREAK_BONUS")
                .map(SystemParameter::getParamValue).orElse("50"));

        int earnedPoints = basePoints + (currentStreak * streakBonus);

        loyaltyPoint.setCurrentPoints(loyaltyPoint.getCurrentPoints() + earnedPoints);
        loyaltyPoint.setCheckinStreak(currentStreak);
        loyaltyPoint.setLastCheckinDate(today);

        return loyaltyPointRepository.save(loyaltyPoint);
    }

    @Transactional
    public void rewardPointsForReview(User user) {
        int rewardPoints = Integer.parseInt(systemParameterRepository.findByParamKey("REVIEW_COIN_REWARD")
                .map(SystemParameter::getParamValue).orElse("500"));

        LoyaltyPoint loyaltyPoint = getMyLoyaltyInfo(user);
        loyaltyPoint.setCurrentPoints(loyaltyPoint.getCurrentPoints() + rewardPoints);
        loyaltyPointRepository.save(loyaltyPoint);
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