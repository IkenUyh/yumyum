package com.uit.fooddelivery_api.modules.voucher.services;

import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import com.uit.fooddelivery_api.modules.loyalty.repositories.LoyaltyPointRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.voucher.entities.UserVoucher;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.repositories.UserVoucherRepository;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;

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
