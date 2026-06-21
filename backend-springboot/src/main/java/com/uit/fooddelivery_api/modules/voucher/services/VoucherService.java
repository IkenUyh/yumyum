package com.uit.fooddelivery_api.modules.voucher.services;

import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.repositories.LoyaltyPointRepository;
import com.uit.fooddelivery_api.modules.loyalty.services.LoyaltyService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.voucher.dtos.VoucherExchangeRequest;
import com.uit.fooddelivery_api.modules.voucher.entities.UserVoucher;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.entities.VoucherType;
import com.uit.fooddelivery_api.modules.voucher.repositories.UserVoucherRepository;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final LoyaltyService loyaltyService;

    @Transactional
    public Voucher exchangeCoinsForVoucher(User user, VoucherExchangeRequest request) {
        // 1. Lấy thông tin xu/loyalty
        LoyaltyPoint lp = loyaltyService.getMyLoyaltyInfo(user);

        // 2. Xác thực chi phí xu
        int cost = request.getCoinCost() != null ? request.getCoinCost() : 0;
        if (cost <= 0) {
            throw new RuntimeException("Chi phí xu không hợp lệ!");
        }

        if (lp.getCurrentPoints() < cost) {
            throw new RuntimeException("Bạn không đủ xu để đổi quà này! Cần " + cost + " xu, hiện có " + lp.getCurrentPoints() + " xu.");
        }

        // 3. Trừ xu của người dùng
        lp.setCurrentPoints(lp.getCurrentPoints() - cost);
        loyaltyPointRepository.save(lp);

        // 4. Ánh xạ các thuộc tính voucher
        String title = request.getTitle() != null ? request.getTitle() : "";
        String typeStr = request.getType() != null ? request.getType() : "";

        VoucherType type = VoucherType.ORDER_DISCOUNT;
        int discountPercent = 15;
        BigDecimal maxDiscount = BigDecimal.valueOf(30000);
        BigDecimal minOrderValue = BigDecimal.valueOf(50000);

        if ("SHIPPING_FEE".equalsIgnoreCase(typeStr) || "SHIPPING_DISCOUNT".equalsIgnoreCase(typeStr)) {
            type = VoucherType.SHIPPING_DISCOUNT;
            discountPercent = 100;
            maxDiscount = BigDecimal.valueOf(15000);
            minOrderValue = BigDecimal.valueOf(30000);

            if (title.contains("20K")) {
                maxDiscount = BigDecimal.valueOf(20000);
                minOrderValue = BigDecimal.valueOf(40000);
            } else if (title.contains("Extra 15K")) {
                maxDiscount = BigDecimal.valueOf(15000);
                minOrderValue = BigDecimal.valueOf(50000);
            }
        } else {
            // FOOD_DISCOUNT / ORDER_DISCOUNT
            if (title.contains("50K")) {
                discountPercent = 15;
                maxDiscount = BigDecimal.valueOf(50000);
                minOrderValue = BigDecimal.valueOf(100000);
            } else if (title.contains("100K")) {
                discountPercent = 25;
                maxDiscount = BigDecimal.valueOf(100000);
                minOrderValue = BigDecimal.valueOf(200000);
            } else if (title.contains("30K")) {
                discountPercent = 15;
                maxDiscount = BigDecimal.valueOf(30000);
                minOrderValue = BigDecimal.valueOf(70000);
            } else if (title.contains("Phúc Long")) {
                discountPercent = 10;
                maxDiscount = BigDecimal.valueOf(20000);
                minOrderValue = BigDecimal.valueOf(50000);
            }
        }

        // 5. Tạo mã voucher ngẫu nhiên độc nhất
        String code = "EXCHANGED_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Voucher voucher = Voucher.builder()
                .code(code)
                .type(type)
                .discountPercent(discountPercent)
                .maxDiscount(maxDiscount)
                .minOrderValue(minOrderValue)
                .stockQuantity(1) // Voucher cá nhân
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30)) // Hạn dùng 30 ngày
                .isActive(true)
                .user(user)
                .build();

        return voucherRepository.save(voucher);
    }

    @Transactional
    public String exchangeVoucher(User user, Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Voucher"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            throw new RuntimeException("Voucher không nằm trong thời gian áp dụng");
        }

        if (!voucher.getIsActive() || voucher.getStockQuantity() <= 0) {
            throw new RuntimeException("Voucher đã hết hạn hoặc hết số lượng");
        }

        if (voucher.getRequiredPoints() != null && voucher.getRequiredPoints() > 0) {
            // Check if user already has it
            if (userVoucherRepository.existsByUserIdAndVoucherId(user.getId(), voucherId)) {
                throw new RuntimeException("Bạn đã đổi voucher này rồi");
            }

            // Check loyalty points
            LoyaltyPoint loyaltyPoint = loyaltyPointRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Chưa có thông tin điểm thưởng"));

            if (loyaltyPoint.getCurrentPoints() < voucher.getRequiredPoints()) {
                throw new RuntimeException("Bạn không đủ xu để đổi voucher này");
            }

            // Deduct points
            loyaltyPoint.setCurrentPoints(loyaltyPoint.getCurrentPoints() - voucher.getRequiredPoints());
            loyaltyPointRepository.save(loyaltyPoint);
        } else {
            // For public vouchers, optionally allow saving to wallet too
            if (userVoucherRepository.existsByUserIdAndVoucherId(user.getId(), voucherId)) {
                throw new RuntimeException("Bạn đã lưu voucher này rồi");
            }
        }

        // Reduce stock
        voucher.setStockQuantity(voucher.getStockQuantity() - 1);
        voucherRepository.save(voucher);

        // Add to UserVoucher
        UserVoucher userVoucher = UserVoucher.builder()
                .user(user)
                .voucher(voucher)
                .isUsed(false)
                .acquiredAt(now)
                .build();

        userVoucherRepository.save(userVoucher);

        return "Đổi voucher thành công!";
    }

    public List<Voucher> getMyVouchers(Long userId) {
        return userVoucherRepository.findByUserIdAndIsUsedFalse(userId)
                .stream()
                .map(UserVoucher::getVoucher)
                .toList();
    }
}
