package com.uit.fooddelivery_api.modules.job;

import com.uit.fooddelivery_api.modules.flashsale.services.FlashSaleRotationService;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import jakarta.annotation.PostConstruct;
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
    private final FlashSaleRotationService flashSaleRotationService;
    private final com.uit.fooddelivery_api.modules.order.services.OrderService orderService;
    private final com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository flashSaleItemRepository;

    // 0. KHỞI TẠO: Đảm bảo luôn có Flashsale hoạt động khi server khởi động
    @PostConstruct
    public void initFlashSaleOnStartup() {
        try {
            System.out.println("🚀 SystemCronJob: Kiểm tra & tạo Flashsale khi khởi động...");
            flashSaleRotationService.rotateFlashSale();
        } catch (Exception e) {
            System.out.println("⚠️ SystemCronJob: Lỗi khi tạo Flashsale lúc khởi động: " + e.getMessage());
        }
    }

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
                try {
                    orderService.cancelOrder(order.getId(), order.getUser(), "Hệ thống tự động hủy do quá hạn không có tài xế nhận");
                } catch (Exception e) {
                    System.out.println("⚠️ CronJob Error: Không thể tự động hủy đơn #" + order.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    // 4. JOB HỦY ĐƠN CHƯA THANH TOÁN: Chạy lặp lại mỗi 1 phút (60000 mili-giây)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelUnpaidOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrders = orderRepository.findStaleUnpaidOrders(cutoffTime);

        if (!unpaidOrders.isEmpty()) {
            System.out.println("🔄 CronJob: Đang tự động hủy " + unpaidOrders.size() + " đơn hàng chưa thanh toán quá hạn...");

            for (Order order : unpaidOrders) {
                try {
                    order.setStatus("CANCELLED");
                    order.setPaymentStatus("EXPIRED");
                    order.setCancelReason("Hết hạn chờ thanh toán");

                    // Trả lại Voucher
                    if (order.getVouchers() != null) {
                        for (com.uit.fooddelivery_api.modules.voucher.entities.Voucher v : order.getVouchers()) {
                            v.setStockQuantity(v.getStockQuantity() + 1);
                            voucherRepository.save(v);
                        }
                    }

                    // Trả lại Flash Sale stock
                    if (order.getOrderItems() != null) {
                        for (com.uit.fooddelivery_api.modules.order.entities.OrderItem item : order.getOrderItems()) {
                            java.util.Optional<com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem> flashSaleOpt = flashSaleItemRepository
                                    .findActiveFlashSaleItemByFoodId(item.getFood().getId(), LocalDateTime.now());
                            if (flashSaleOpt.isPresent()) {
                                com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem fsItem = flashSaleOpt.get();
                                fsItem.setSoldQuantity(Math.max(0, fsItem.getSoldQuantity() - item.getQuantity()));
                                flashSaleItemRepository.save(fsItem);
                            }
                        }
                    }

                    orderRepository.save(order);
                } catch (Exception e) {
                    System.out.println("⚠️ CronJob Error: Không thể tự động hủy đơn unpaid #" + order.getId() + ": " + e.getMessage());
                }
            }
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

    // 3. JOB XOAY VÒNG FLASHSALE: Chạy mỗi giờ, đúng phút 0 (VD: 10:00, 11:00, 12:00, ...)
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void rotateHourlyFlashSale() {
        try {
            System.out.println("⚡ CronJob: Bắt đầu xoay vòng Flashsale hàng giờ...");
            flashSaleRotationService.rotateFlashSale();
        } catch (Exception e) {
            System.out.println("❌ CronJob: Lỗi khi xoay vòng Flashsale: " + e.getMessage());
            e.printStackTrace();
        }
    }
}