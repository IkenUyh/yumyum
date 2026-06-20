package com.uit.fooddelivery_api.modules.loyalty.services;

import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.repositories.LoyaltyPointRepository;
import com.uit.fooddelivery_api.modules.system.entities.SystemParameter;
import com.uit.fooddelivery_api.modules.system.repositories.SystemParameterRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.loyalty.dtos.DealHistoryResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final SystemParameterRepository systemParameterRepository;
    private final OrderRepository orderRepository;

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

    public List<DealHistoryResponseDTO> getMyDeals(User user) {
        List<DealHistoryResponseDTO> list = new ArrayList<>();

        // 1. Lấy toàn bộ đơn hàng của khách hàng
        List<com.uit.fooddelivery_api.modules.order.entities.Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // 2. Duyệt qua đơn hàng để trích xuất voucher đã dùng làm Deal đã mua
        for (com.uit.fooddelivery_api.modules.order.entities.Order order : orders) {
            if (order.getVouchers() != null && !order.getVouchers().isEmpty()) {
                for (com.uit.fooddelivery_api.modules.voucher.entities.Voucher voucher : order.getVouchers()) {
                    String dateStr = "Hôm nay";
                    if (order.getCreatedAt() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        dateStr = order.getCreatedAt().format(formatter);
                    }
                    
                    String expiryStr = "HSD: ";
                    if (voucher.getEndDate() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        expiryStr += voucher.getEndDate().format(formatter);
                    } else {
                        expiryStr += "Không thời hạn";
                    }

                    String dealTitleText = voucher.getCode();
                    if (voucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.SHIPPING_DISCOUNT) {
                        dealTitleText = "Giảm giá phí vận chuyển " + voucher.getDiscountPercent() + "%";
                    } else if (voucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.ORDER_DISCOUNT) {
                        dealTitleText = "Giảm giá hóa đơn " + voucher.getDiscountPercent() + "%";
                    }

                    list.add(DealHistoryResponseDTO.builder()
                            .dealId(order.getId() + "_" + voucher.getId())
                            .merchantName(order.getRestaurant() != null ? order.getRestaurant().getName() : "Hệ thống")
                            .purchaseDate(dateStr)
                            .dealTitle(dealTitleText)
                            .price("Dùng ví")
                            .expiryText(expiryStr)
                            .quantityText("x1")
                            .statusText("Đã dùng")
                            .appliedOrderId(String.valueOf(order.getId()))
                            .build());
                }
            }
        }

        // 3. Thêm một số deal mock chưa dùng/đã dùng
        list.add(DealHistoryResponseDTO.builder()
                .dealId("DEAL_MOCK_1")
                .merchantName("Trà Sữa An Viên - Đường 30 Tháng 4")
                .purchaseDate("10/06/2026")
                .dealTitle("Voucher Giảm 50% Trà Sữa")
                .price("500 xu")
                .expiryText("HSD: 30/12/2026")
                .quantityText("x1")
                .statusText("Chưa dùng")
                .appliedOrderId(null)
                .build());

        list.add(DealHistoryResponseDTO.builder()
                .dealId("DEAL_MOCK_2")
                .merchantName("Bánh Mì Huỳnh Hoa")
                .purchaseDate("08/06/2026")
                .dealTitle("Khao 30K Bánh Mì Đặc Biệt")
                .price("300 xu")
                .expiryText("HSD: 31/12/2026")
                .quantityText("x1")
                .statusText("Chưa dùng")
                .appliedOrderId(null)
                .build());

        return list;
    }
}