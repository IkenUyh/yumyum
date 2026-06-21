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

import java.math.BigDecimal;
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
                .totalSpending(BigDecimal.ZERO)
                .build();
        return loyaltyPointRepository.save(lp);
    }

    public String getRankName(BigDecimal totalSpending) {
        if (totalSpending == null) return "NEW";
        long spent = totalSpending.longValue();
        if (spent >= 15000000) return "DIAMOND";
        if (spent >= 5000000) return "GOLD";
        if (spent >= 1000000) return "SILVER";
        return "NEW";
    }

    public double getCoinMultiplier(String rank) {
        switch(rank) {
            case "DIAMOND": return 1.5;
            case "GOLD": return 1.2;
            case "SILVER": return 1.1;
            default: return 1.0;
        }
    }

    public BigDecimal getRankShippingDiscount(String rank) {
        switch(rank) {
            case "DIAMOND": return BigDecimal.valueOf(30000);
            case "GOLD": return BigDecimal.valueOf(15000);
            default: return BigDecimal.ZERO;
        }
    }

    @Transactional
    public void addPointsAndSpending(User user, BigDecimal orderTotal) {
        if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) return;

        LoyaltyPoint lp = getMyLoyaltyInfo(user);
        BigDecimal currentSpending = lp.getTotalSpending() != null ? lp.getTotalSpending() : BigDecimal.ZERO;

        // Xếp hạng hiện tại
        String currentRank = getRankName(currentSpending);
        
        // Cập nhật tổng chi tiêu
        BigDecimal newSpending = currentSpending.add(orderTotal);
        lp.setTotalSpending(newSpending);

        // Hạng mới sau khi cộng chi tiêu
        String newRank = getRankName(newSpending);

        // Tích Xu: 100đ = 1 Xu. Nhân với hệ số hạng MỚI.
        double multiplier = getCoinMultiplier(newRank);
        int earnedCoins = (int) (orderTotal.longValue() / 100 * multiplier);

        lp.setCurrentPoints(lp.getCurrentPoints() + earnedCoins);
        loyaltyPointRepository.save(lp);
    }

    public List<DealHistoryResponseDTO> getMyDeals(User user) {
        List<DealHistoryResponseDTO> list = new ArrayList<>();

        // 1. Lấy toàn bộ đơn hàng của khách hàng
        List<com.uit.fooddelivery_api.modules.order.entities.Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // 2. Duyệt qua đơn hàng để trích xuất các món ăn được mua với giá ưu đãi (discount price)
        for (com.uit.fooddelivery_api.modules.order.entities.Order order : orders) {
            if (order.getOrderItems() != null) {
                for (com.uit.fooddelivery_api.modules.order.entities.OrderItem item : order.getOrderItems()) {
                    BigDecimal originalPrice = item.getFood().getPrice();
                    BigDecimal purchasedPrice = item.getPrice();
                    
                    // Coi món ăn là Deal nếu giá mua nhỏ hơn giá bán lẻ gốc,
                    // hoặc nếu toàn đơn hàng có áp dụng giảm giá.
                    boolean isDiscounted = (originalPrice != null && purchasedPrice.compareTo(originalPrice) < 0) 
                            || (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
                    
                    if (isDiscounted) {
                        String dateStr = "Hôm nay";
                        if (order.getCreatedAt() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            dateStr = order.getCreatedAt().format(formatter);
                        }
                        
                        String originalPriceStr = "Giá gốc: " + (originalPrice != null ? formatCurrency(originalPrice) : "0đ");
                        String dealTitleText = item.getFood().getName();
                        String statusText = translateStatus(order.getStatus());
                        
                        list.add(DealHistoryResponseDTO.builder()
                                .dealId(order.getId() + "_" + item.getId())
                                .merchantName(order.getRestaurant() != null ? order.getRestaurant().getName() : "Hệ thống")
                                .purchaseDate(dateStr)
                                .dealTitle(dealTitleText)
                                .price(formatCurrency(purchasedPrice))
                                .expiryText(originalPriceStr)
                                .quantityText("x" + item.getQuantity())
                                .statusText(statusText)
                                .appliedOrderId(String.valueOf(order.getId()))
                                .imageUrl(item.getFood().getImageUrl())
                                .build());
                    }
                }
            }
        }

        // 3. Thêm các deal mock là món ăn mua ở dạng ưu đãi
        list.add(DealHistoryResponseDTO.builder()
                .dealId("DEAL_MOCK_1")
                .merchantName("Trà Sữa An Viên - Đường 30 Tháng 4")
                .purchaseDate("10/06/2026")
                .dealTitle("Trà Sữa Trân Châu Đường Đen")
                .price("25.000đ")
                .expiryText("Giá gốc: 50.000đ")
                .quantityText("x1")
                .statusText("Đã hoàn thành")
                .appliedOrderId("1")
                .imageUrl("https://picsum.photos/seed/milktea/200/200")
                .build());

        list.add(DealHistoryResponseDTO.builder()
                .dealId("DEAL_MOCK_2")
                .merchantName("Bánh Mì Huỳnh Hoa")
                .purchaseDate("08/06/2026")
                .dealTitle("Bánh Mì Đặc Biệt")
                .price("35.000đ")
                .expiryText("Giá gốc: 65.000đ")
                .quantityText("x1")
                .statusText("Đang chuẩn bị")
                .appliedOrderId("2")
                .imageUrl("https://picsum.photos/seed/banhmi/200/200")
                .build());

        return list;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0đ";
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(new java.util.Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###đ", symbols);
        return formatter.format(amount.doubleValue());
    }

    private String translateStatus(String status) {
        if (status == null) return "Đang xử lý";
        switch (status.toUpperCase()) {
            case "PENDING": return "Đang chờ";
            case "PREPARING": return "Đang chuẩn bị";
            case "DELIVERING": return "Đang giao hàng";
            case "COMPLETED": return "Đã hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}