package com.uit.fooddelivery_api.modules.job;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SystemCronJob {

    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;
    private final WalletRepository walletRepository;

    // 1. JOB HỦY ĐƠN VÀ HOÀN TIỀN: Chạy lặp lại mỗi 1 phút (60000 mili-giây)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelStaleOrders() {
        // Tìm các đơn đã tạo từ 15 phút trước
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<Order> staleOrders = orderRepository.findStalePendingOrders(cutoffTime);

        if (!staleOrders.isEmpty()) {
            System.out.println("🔄 CronJob: Đang tự động hủy " + staleOrders.size() + " đơn hàng quá hạn (không có tài xế nhận)...");

            for (Order order : staleOrders) {
                // Đổi trạng thái thành Đã Hủy
                order.setStatus("CANCELLED");

                // Tìm ví của người mua để Hoàn tiền
                Wallet customerWallet = walletRepository.findByUserId(order.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ví khách hàng để hoàn tiền!"));

                customerWallet.setBalance(customerWallet.getBalance().add(order.getTotalAmount()));
                walletRepository.save(customerWallet);
            }

            // Lưu cập nhật tất cả đơn hàng
            orderRepository.saveAll(staleOrders);
            System.out.println("✅ CronJob: Đã hủy và hoàn trả tiền vào ví khách hàng thành công!");
        }
    }

    // 2. JOB KHÓA VOUCHER: Chạy lặp lại mỗi 1 giờ (3600000 mili-giây)
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void autoExpireVouchers() {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> expiredVouchers = voucherRepository.findExpiredActiveVouchers(now);

        if (!expiredVouchers.isEmpty()) {
            System.out.println("🔄 CronJob: Đang khóa " + expiredVouchers.size() + " voucher đã hết hạn sử dụng...");

            for (Voucher voucher : expiredVouchers) {
                voucher.setIsActive(false);
            }
            voucherRepository.saveAll(expiredVouchers);
            System.out.println("✅ CronJob: Đã khóa voucher thành công!");
        }
    }
}