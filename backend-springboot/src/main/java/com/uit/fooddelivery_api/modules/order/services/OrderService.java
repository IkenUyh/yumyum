package com.uit.fooddelivery_api.modules.order.services;

import com.uit.fooddelivery_api.common.utils.DistanceUtil;
import com.uit.fooddelivery_api.modules.cart.entities.CartItem;
import com.uit.fooddelivery_api.modules.cart.repositories.CartItemRepository;
import com.uit.fooddelivery_api.modules.order.dtos.CreateOrderDTO;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.entities.OrderItem;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.entities.UserAddress;
import com.uit.fooddelivery_api.modules.user.repositories.UserAddressRepository;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final WalletRepository walletRepository;
    private final CartItemRepository cartItemRepository;
    private final UserAddressRepository addressRepository;
    private final com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository voucherRepository;

    // Phí ship cơ bản: 5.000 VNĐ / 1 Km
    private static final BigDecimal FEE_PER_KM = BigDecimal.valueOf(5000);
    // Bán kính phục vụ tối đa: 15 Km
    private static final double MAX_DELIVERY_RADIUS_KM = 15.0;

    @Transactional
    public Order createOrder(CreateOrderDTO dto, User customer) {
        // 1 & 2 & 3. Lấy Giỏ hàng, Nhà hàng, Địa chỉ (Giữ nguyên)
        List<CartItem> cartItems = cartItemRepository.findByUserId(customer.getId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng đang trống!");

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng!"));

        UserAddress address = addressRepository.findByIdAndUserId(dto.getAddressId(), customer.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng!"));

        // 4. TÍNH KHOẢNG CÁCH (Giữ nguyên)
        if (restaurant.getLatitude() == null || restaurant.getLongitude() == null ||
                address.getLatitude() == null || address.getLongitude() == null) {
            throw new RuntimeException("Hệ thống chưa cập nhật đủ tọa độ để tính phí ship!");
        }
        double distanceKm = DistanceUtil.calculateDistance(
                restaurant.getLatitude().doubleValue(), restaurant.getLongitude().doubleValue(),
                address.getLatitude().doubleValue(), address.getLongitude().doubleValue()
        );
        if (distanceKm > MAX_DELIVERY_RADIUS_KM) {
            throw new RuntimeException("Quá xa (" + String.format("%.1f", distanceKm) + "km). Vượt quá bán kính giao hàng!");
        }

        BigDecimal baseShippingFee = BigDecimal.valueOf(distanceKm).multiply(FEE_PER_KM).setScale(0, RoundingMode.HALF_UP);

        // ==========================================
        // 5. LOGIC LỰA CHỌN TỐC ĐỘ GIAO & ETA (ISSUE #9)
        // ==========================================
        String deliveryMode = (dto.getDeliveryMode() != null) ? dto.getDeliveryMode().toUpperCase() : "STANDARD";
        BigDecimal extraShippingFee = BigDecimal.ZERO;
        int prepTimeMinutes = 15; // Thời gian mặc định quán nấu món
        double minutesPerKm = 3.0; // Tốc độ di chuyển: 3 phút/1km (Khoảng 20km/h trong thành phố)

        if ("FAST".equals(deliveryMode)) {
            extraShippingFee = BigDecimal.valueOf(10000); // Phụ phí 10k
            prepTimeMinutes = 10; // Bếp ưu tiên làm nhanh hơn
            minutesPerKm = 2.0; // Tài xế chạy 30km/h
        } else if ("EXPRESS".equals(deliveryMode)) {
            extraShippingFee = BigDecimal.valueOf(25000); // Phụ phí 25k (Giao hỏa tốc)
            prepTimeMinutes = 5; // Bếp làm cực gấp
            minutesPerKm = 1.5; // Tài xế chạy 40km/h
        } else {
            deliveryMode = "STANDARD";
        }

        // Chốt Phí Ship cuối cùng
        BigDecimal shippingFee = baseShippingFee.add(extraShippingFee);

        // Tính Thời gian dự kiến giao đến nơi (ETA)
        int travelTimeMinutes = (int) Math.ceil(distanceKm * minutesPerKm);
        int totalMinutes = prepTimeMinutes + travelTimeMinutes;
        LocalDateTime expectedDeliveryTime = LocalDateTime.now().plusMinutes(totalMinutes);
        // ==========================================


        // 6. Quét đồ ăn và tính tiền Topping (Giữ nguyên)
        BigDecimal foodTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .user(customer)
                .restaurant(restaurant)
                .status("PENDING")
                .shippingFee(shippingFee)
                .deliveryMode(deliveryMode) // Lưu Mode vào DB
                .expectedDeliveryTime(expectedDeliveryTime) // Lưu ETA vào DB
                .build();

        for (CartItem item : cartItems) {
            if (!item.getFood().getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException("Món " + item.getFood().getName() + " không thuộc nhà hàng đang đặt!");
            }

            BigDecimal optionsPrice = BigDecimal.ZERO;
            if (item.getSelectedOptions() != null && !item.getSelectedOptions().isEmpty() && !item.getSelectedOptions().equals("[]")) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<java.util.Map<String, Object>> parsedOpts = mapper.readValue(item.getSelectedOptions(), new com.fasterxml.jackson.core.type.TypeReference<List<java.util.Map<String, Object>>>() {});
                    for (java.util.Map<String, Object> opt : parsedOpts) {
                        optionsPrice = optionsPrice.add(new BigDecimal(opt.get("price").toString()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi đọc dữ liệu Topping!");
                }
            }

            BigDecimal pricePerItem = item.getFood().getPrice().add(optionsPrice);
            BigDecimal itemTotal = pricePerItem.multiply(BigDecimal.valueOf(item.getQuantity()));
            foodTotal = foodTotal.add(itemTotal);

            orderItems.add(OrderItem.builder().order(order).food(item.getFood()).quantity(item.getQuantity()).price(pricePerItem).selectedOptions(item.getSelectedOptions()).build());
        }

        // 7. XỬ LÝ VOUCHER (Giữ nguyên)
        BigDecimal discountAmount = BigDecimal.ZERO;
        com.uit.fooddelivery_api.modules.voucher.entities.Voucher appliedVoucher = null;

        if (dto.getVoucherCode() != null && !dto.getVoucherCode().trim().isEmpty()) {
            appliedVoucher = voucherRepository.findByCodeAndIsActiveTrue(dto.getVoucherCode())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại hoặc đã bị khóa!"));

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (now.isBefore(appliedVoucher.getStartDate()) || now.isAfter(appliedVoucher.getEndDate())) {
                throw new RuntimeException("Mã giảm giá đã hết hạn hoặc chưa tới thời gian sử dụng!");
            }
            if (appliedVoucher.getStockQuantity() <= 0) {
                throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");
            }
            if (foodTotal.compareTo(appliedVoucher.getMinOrderValue()) < 0) {
                throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + appliedVoucher.getMinOrderValue() + "đ để dùng mã này!");
            }

            BigDecimal calculatedDiscount = BigDecimal.ZERO;
            if (appliedVoucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.SHIPPING_DISCOUNT) {
                calculatedDiscount = shippingFee.multiply(BigDecimal.valueOf(appliedVoucher.getDiscountPercent())).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                discountAmount = calculatedDiscount.min(appliedVoucher.getMaxDiscount()).min(shippingFee);
            } else if (appliedVoucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.ORDER_DISCOUNT) {
                calculatedDiscount = foodTotal.multiply(BigDecimal.valueOf(appliedVoucher.getDiscountPercent())).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                discountAmount = calculatedDiscount.min(appliedVoucher.getMaxDiscount()).min(foodTotal);
            }

            appliedVoucher.setStockQuantity(appliedVoucher.getStockQuantity() - 1);
            voucherRepository.save(appliedVoucher);
        }

        // 8. Chốt Tổng Tiền và Trừ Ví (Giữ nguyên)
        BigDecimal finalTotal = foodTotal.add(shippingFee).subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        order.setTotalAmount(finalTotal);
        order.setDiscountAmount(discountAmount);
        order.setVoucher(appliedVoucher);
        order.setOrderItems(orderItems);

        Wallet wallet = walletRepository.findByUserId(customer.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của bạn!"));

        if (wallet.getBalance().compareTo(finalTotal) < 0) {
            throw new RuntimeException("Ví không đủ tiền! Tổng thanh toán: " + finalTotal + "đ.");
        }

        wallet.setBalance(wallet.getBalance().subtract(finalTotal));
        walletRepository.save(wallet);

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUserId(customer.getId());

        return savedOrder;
    }

    // 1. Lấy danh sách đơn đang chờ tài xế
    public List<Order> getAvailableOrders() {
        return orderRepository.findByStatus("PENDING");
    }

    // 2. Tài xế nhận đơn
    @Transactional
    public Order acceptOrder(Long orderId, User driver) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Đơn hàng này đã có người nhận hoặc bị hủy!");
        }

        order.setDriver(driver);
        order.setStatus("DELIVERING");
        return orderRepository.save(order);
    }

    // 3. Tài xế giao xong -> Chuyển tiền cho chủ quán
    @Transactional
    public Order completeOrder(Long orderId, User driver) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!order.getStatus().equals("DELIVERING") || !order.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("Anh không có quyền hoàn thành đơn hàng này!");
        }

        // Đổi trạng thái đơn
        order.setStatus("COMPLETED");

        // Bắn tiền cho chủ quán (Merchant)
        Wallet merchantWallet = walletRepository.findByUserId(order.getRestaurant().getMerchant().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của chủ quán!"));

        merchantWallet.setBalance(merchantWallet.getBalance().add(order.getTotalAmount()));
        walletRepository.save(merchantWallet);

        return orderRepository.save(order);
    }

    // Lấy lịch sử đơn hàng cho Khách hàng
    public List<Order> getCustomerOrderHistory(User customer) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(customer.getId());
    }

    // Lấy lịch sử đơn hàng cho Chủ quán (Merchant)
    public List<Order> getMerchantOrderHistory(User merchant) {
        return orderRepository.findByRestaurantMerchantIdOrderByCreatedAtDesc(merchant.getId());
    }
}