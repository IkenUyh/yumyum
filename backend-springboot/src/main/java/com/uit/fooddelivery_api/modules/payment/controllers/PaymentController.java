package com.uit.fooddelivery_api.modules.payment.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.common.utils.HMACUtil;
import com.uit.fooddelivery_api.modules.payment.services.ZaloPayService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.uit.fooddelivery_api.modules.payment.services.VNPayService;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ZaloPayService zaloPayService;
    private final VNPayService vnPayService;
    private final WalletRepository walletRepository;

    @Value("${zalopay.key2}")
    private String key2; // ZaloPay dùng Key 2 để ký chữ ký Callback trả về

    // 1. Dành cho Khách hàng (Android gọi API này để lấy URL thanh toán ZaloPay)
    @PostMapping("/zalopay/topup")
    public ApiResponse<Map<String, Object>> createTopUp(
            Authentication authentication,
            @RequestParam Long amount) {

        User currentUser = (User) authentication.getPrincipal();
        if (amount < 10000) {
            throw new RuntimeException("Số tiền nạp tối thiểu là 10.000 VNĐ");
        }

        Map<String, Object> result = zaloPayService.createTopUpOrder(currentUser, amount);
        return ApiResponse.success(result);
    }

    // 2. Dành cho Máy chủ ZaloPay (ZaloPay sẽ tự động gọi API này khi khách thanh toán thành công)
    @PostMapping("/zalopay/callback")
    public Map<String, Object> zalopayCallback(@RequestBody String jsonStr) {
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> cbData = mapper.readValue(jsonStr, Map.class);

            String dataStr = (String) cbData.get("data");
            String reqMac = (String) cbData.get("mac");

            // Xác thực chữ ký xem có đúng là máy chủ ZaloPay gọi không (Tránh Hacker gọi Fake Webhook)
            String mac = HMACUtil.HMacHexString(dataStr, key2);

            if (!reqMac.equals(mac)) {
                // Chữ ký không khớp -> Báo lỗi
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                // Thanh toán thành công -> Lấy userId từ embed_data ra và cộng tiền
                Map<String, Object> data = mapper.readValue(dataStr, Map.class);
                Map<String, Object> embedData = mapper.readValue((String) data.get("embed_data"), Map.class);

                Long userId = ((Number) embedData.get("user_id")).longValue();
                Long amount = ((Number) data.get("amount")).longValue();

                Wallet wallet = walletRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));

                wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
                walletRepository.save(wallet);

                // Trả về tín hiệu cho ZaloPay biết Server mình đã ghi nhận
                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0);
            result.put("return_message", ex.getMessage());
        }

        return result;
    }
    // 3. Truy vấn trạng thái đơn hàng (Dành cho App gọi để kiểm tra)
    @GetMapping("/zalopay/order-status/{appTransId}")
    public ApiResponse<Map<String, Object>> getOrderStatus(@PathVariable String appTransId) {
        Map<String, Object> result = zaloPayService.queryOrder(appTransId);
        return ApiResponse.success(result);
    }

    // 4. Yêu cầu hoàn tiền (Refund)
    @PostMapping("/zalopay/refund")
    public ApiResponse<Map<String, Object>> refundOrder(
            @RequestParam String zpTransId,
            @RequestParam Long amount,
            @RequestParam String description) {
        
        Map<String, Object> result = zaloPayService.refund(zpTransId, amount, description);
        return ApiResponse.success(result);
    }

    // 5. Truy vấn trạng thái hoàn tiền
    @GetMapping("/zalopay/refund-status/{mRefundId}")
    public ApiResponse<Map<String, Object>> getRefundStatus(@PathVariable String mRefundId) {
        Map<String, Object> result = zaloPayService.queryRefundStatus(mRefundId);
        return ApiResponse.success(result);
    }

    // ==========================================
    // VNPay Integration
    // ==========================================

    @PostMapping("/vnpay/topup")
    public ApiResponse<Map<String, Object>> createVNPayTopUp(
            Authentication authentication,
            @RequestParam Long amount) {

        User currentUser = (User) authentication.getPrincipal();
        if (amount < 10000) {
            throw new RuntimeException("Số tiền nạp tối thiểu là 10.000 VNĐ");
        }

        String paymentUrl = vnPayService.createPaymentUrl(currentUser, amount);
        Map<String, Object> result = new HashMap<>();
        result.put("paymentUrl", paymentUrl);
        return ApiResponse.success(result);
    }

    @GetMapping("/vnpay/ipn")
    public String vnpayIpn(@RequestParam Map<String, String> allParams) {
        try {
            if (vnPayService.verifyIpnSignature(allParams)) {
                if ("00".equals(allParams.get("vnp_ResponseCode"))) {
                    String vnp_OrderInfo = allParams.get("vnp_OrderInfo");
                    // Format: TopUp_{userId}_Amount_{amount}
                    String[] parts = vnp_OrderInfo.split("_");
                    Long userId = Long.parseLong(parts[1]);
                    Long amount = Long.parseLong(allParams.get("vnp_Amount")) / 100;

                    Wallet wallet = walletRepository.findByUserId(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));

                    wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
                    walletRepository.save(wallet);
                    
                    return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                } else {
                    return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
                }
            } else {
                return "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
            }
        } catch (Exception e) {
            return "{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}";
        }
    }

    // Lưu tạm các mã giao dịch đã xử lý để tránh cộng tiền 2 lần khi refresh trang
    private final java.util.Set<String> processedVNPayTxnRefs = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    @GetMapping("/vnpay/return")
    public String vnpayReturn(@RequestParam Map<String, String> allParams) {
        if (vnPayService.verifyIpnSignature(allParams)) {
            if ("00".equals(allParams.get("vnp_ResponseCode"))) {
                String vnp_TxnRef = allParams.get("vnp_TxnRef");
                
                // Tránh cộng tiền 2 lần nếu user F5 trang web
                if (!processedVNPayTxnRefs.contains(vnp_TxnRef)) {
                    processedVNPayTxnRefs.add(vnp_TxnRef);
                    
                    try {
                        String vnp_OrderInfo = allParams.get("vnp_OrderInfo");
                        String[] parts = vnp_OrderInfo.split("_");
                        Long userId = Long.parseLong(parts[1]);
                        Long amount = Long.parseLong(allParams.get("vnp_Amount")) / 100;

                        Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));

                        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
                        walletRepository.save(wallet);
                    } catch (Exception e) {
                        return "<h1>Lỗi cộng tiền: " + e.getMessage() + "</h1>";
                    }
                }
                
                return "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'></head><body style='text-align:center; padding: 50px;'><h1 style='color: green;'>Thanh toán thành công!</h1><p>Tiền đã được cộng vào ví UITpay.</p><p>Bạn có thể đóng trình duyệt này và quay lại ứng dụng.</p></body></html>";
            } else {
                return "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'></head><body style='text-align:center; padding: 50px;'><h1 style='color: red;'>Thanh toán thất bại!</h1><p>Giao dịch của bạn đã bị hủy hoặc có lỗi xảy ra.</p></body></html>";
            }
        }
        return "<h1>Lỗi xác thực chữ ký VNPay!</h1>";
    }
}