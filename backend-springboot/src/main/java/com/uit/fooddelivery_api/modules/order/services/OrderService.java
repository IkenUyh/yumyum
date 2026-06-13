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
        // 1. Lấy Giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findByUserId(customer.getId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng đang trống!");

        // 2. Tìm Nhà hàng TRƯỚC TIÊN
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng!"));

        // 3. Tìm Địa chỉ
        UserAddress address = addressRepository.findByIdAndUserId(dto.getAddressId(), customer.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng!"));

        // ==========================================
        // 4. KIỂM TRA GIỜ HOẠT ĐỘNG VÀ QUÁ TẢI BẾP (ISSUE #28)
        // ==========================================
        // Lúc này biến 'restaurant' đã có dữ liệu nên không bị lỗi nữa
        if (restaurant.getIsAcceptingOrders() != null && !restaurant.getIsAcceptingOrders()) {
            throw new RuntimeException("Quán hiện đang tạm ngưng nhận đơn mới. Vui lòng thông cảm!");
        }

        java.time.LocalTime nowTime = java.time.LocalTime.now();
        java.time.LocalTime open = restaurant.getOpenTime();
        java.time.LocalTime close = restaurant.getCloseTime();

        if (open != null && close != null) {
            boolean isOpen;
            if (open.isBefore(close)) {
                isOpen = !nowTime.isBefore(open) && !nowTime.isAfter(close);
            } else {
                isOpen = !nowTime.isBefore(open) || !nowTime.isAfter(close);
            }
            if (!isOpen) {
                throw new RuntimeException("Quán đã đóng cửa! Giờ hoạt động: " + open + " - " + close);
            }
        }

        Long activeOrders = orderRepository.countActiveOrdersByRestaurant(restaurant.getId());
        Integer maxOrders = restaurant.getMaxPendingOrders() != null ? restaurant.getMaxPendingOrders() : 20;

        if (activeOrders >= maxOrders) {
            restaurant.setIsAcceptingOrders(false);
            restaurantRepository.save(restaurant);
            throw new RuntimeException("Bếp đang quá tải (Kẹt " + activeOrders + " đơn). Hệ thống đã tạm ngưng nhận đơn để bảo đảm chất lượng. Vui lòng quay lại sau ít phút!");
        }
        // ==========================================

        // 5. TÍNH KHOẢNG CÁCH
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

        // 6. LOGIC LỰA CHỌN TỐC ĐỘ GIAO & ETA (ISSUE #9)
        String deliveryMode = (dto.getDeliveryMode() != null) ? dto.getDeliveryMode().toUpperCase() : "STANDARD";
        BigDecimal extraShippingFee = BigDecimal.ZERO;
        int prepTimeMinutes = 15;
        double minutesPerKm = 3.0;

        if ("FAST".equals(deliveryMode)) {
            extraShippingFee = BigDecimal.valueOf(10000);
            prepTimeMinutes = 10;
            minutesPerKm = 2.0;
        } else if ("EXPRESS".equals(deliveryMode)) {
            extraShippingFee = BigDecimal.valueOf(25000);
            prepTimeMinutes = 5;
            minutesPerKm = 1.5;
        } else {
            deliveryMode = "STANDARD";
        }

        BigDecimal shippingFee = baseShippingFee.add(extraShippingFee);
        int travelTimeMinutes = (int) Math.ceil(distanceKm * minutesPerKm);
        int totalMinutes = prepTimeMinutes + travelTimeMinutes;
        LocalDateTime expectedDeliveryTime = LocalDateTime.now().plusMinutes(totalMinutes);

        // 7. QUÉT ĐỒ ĂN VÀ TÍNH TIỀN TOPPING
        BigDecimal foodTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .user(customer)
                .restaurant(restaurant)
                .status("PENDING")
                .shippingFee(shippingFee)
                .deliveryMode(deliveryMode)
                .expectedDeliveryTime(expectedDeliveryTime)
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

        // ==========================================
        // 8. XỬ LÝ XẾP CHỒNG VOUCHER
        // ==========================================
        BigDecimal totalOrderDiscount = BigDecimal.ZERO;     // Tổng tiền giảm của nhóm món ăn
        BigDecimal totalShippingDiscount = BigDecimal.ZERO;  // Tổng tiền giảm của nhóm ship

        List<com.uit.fooddelivery_api.modules.voucher.entities.Voucher> appliedVouchers = new ArrayList<>();
        boolean hasShippingDiscountType = false;
        boolean hasOrderDiscountType = false;

        if (dto.getVoucherCodes() != null && !dto.getVoucherCodes().isEmpty()) {
            for (String code : dto.getVoucherCodes()) {
                if (code == null || code.trim().isEmpty()) continue;

                com.uit.fooddelivery_api.modules.voucher.entities.Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(code)
                        .orElseThrow(() -> new RuntimeException("Mã giảm giá [" + code + "] không tồn tại hoặc đã hết lượt kích hoạt!"));

                // Kiểm tra thời hạn hiệu lực của mã
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
                    throw new RuntimeException("Mã giảm giá [" + code + "] đã hết hạn hoặc chưa đến thời gian sử dụng!");
                }

                // Kiểm tra kho số lượng phát hành
                if (voucher.getStockQuantity() <= 0) {
                    throw new RuntimeException("Mã giảm giá [" + code + "] đã hết lượt sử dụng trên hệ thống!");
                }

                // Kiểm tra điều kiện giá trị đơn hàng tối thiểu
                if (foodTotal.compareTo(voucher.getMinOrderValue()) < 0) {
                    throw new RuntimeException("Mã [" + code + "] yêu cầu giá trị đơn hàng tối thiểu từ " + voucher.getMinOrderValue() + "đ!");
                }

                // Phân loại xử lý dựa trên loại Voucher
                if (voucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.SHIPPING_DISCOUNT) {
                    // Chặn việc áp dụng từ 2 mã giảm ship trở lên
                    if (hasShippingDiscountType) {
                        throw new RuntimeException("Hệ thống chỉ cho phép áp dụng tối đa 1 mã giảm giá phí vận chuyển trên một đơn hàng!");
                    }
                    hasShippingDiscountType = true;

                    BigDecimal calc = shippingFee.multiply(BigDecimal.valueOf(voucher.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    totalShippingDiscount = calc.min(voucher.getMaxDiscount()).min(shippingFee);

                } else if (voucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.ORDER_DISCOUNT) {
                    // Chặn việc áp dụng từ 2 mã giảm đơn hàng trở lên
                    if (hasOrderDiscountType) {
                        throw new RuntimeException("Hệ thống chỉ cho phép áp dụng tối đa 1 mã giảm giá hóa đơn trên một đơn hàng!");
                    }
                    hasOrderDiscountType = true;

                    BigDecimal calc = foodTotal.multiply(BigDecimal.valueOf(voucher.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    totalOrderDiscount = calc.min(voucher.getMaxDiscount()).min(foodTotal);
                }

                // Trừ đi một lượt sử dụng trong kho và đưa vào danh sách lưu vết đơn hàng
                voucher.setStockQuantity(voucher.getStockQuantity() - 1);
                voucherRepository.save(voucher);
                appliedVouchers.add(voucher);
            }
        }

        // Tính toán tổng số tiền giảm cuối cùng của cả đơn hàng
        BigDecimal totalDiscountAmount = totalOrderDiscount.add(totalShippingDiscount);

        // 9. CHỐT TỔNG TIỀN VÀ TRỪ VÍ
        BigDecimal finalTotal = foodTotal.add(shippingFee).subtract(totalDiscountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        order.setTotalAmount(finalTotal);
        order.setDiscountAmount(totalDiscountAmount);
        order.setVouchers(appliedVouchers); // Lưu danh sách các mã đã dùng vào bảng trung gian
        order.setOrderItems(orderItems);

        Wallet wallet = walletRepository.findByUserId(customer.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin ví điện tử cá nhân!"));

        if (wallet.getBalance().compareTo(finalTotal) < 0) {
            throw new RuntimeException("Số dư tài khoản ví không đủ để thực hiện thanh toán! Tổng tiền: " + finalTotal + "đ.");
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