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
    private final com.uit.fooddelivery_api.modules.notification.services.NotificationService notificationService;
    private final CartItemRepository cartItemRepository;
    private final UserAddressRepository addressRepository;
    private final com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository voucherRepository;
    private final com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository flashSaleItemRepository;
    private final com.uit.fooddelivery_api.modules.wallet.services.WalletService walletService;

    // Phí ship cơ bản: 5.000 VNĐ / 1 Km
    private static final BigDecimal FEE_PER_KM = BigDecimal.valueOf(5000);
    // Bán kính phục vụ tối đa: 15 Km
    private static final double MAX_DELIVERY_RADIUS_KM = 15.0;

    @Transactional
    public com.uit.fooddelivery_api.modules.order.dtos.OrderPreviewResponseDTO previewOrder(CreateOrderDTO dto, User customer) {
        // Mô phỏng lại việc tính toán của createOrder nhưng KHÔNG GHI VÀO DB
        List<CartItem> cartItems = cartItemRepository.findByUserId(customer.getId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng đang trống!");

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng!"));

        UserAddress address = addressRepository.findByIdAndUserId(dto.getAddressId(), customer.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng!"));

        // KIỂM TRA GIỜ HOẠT ĐỘNG
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

        // TÍNH KHOẢNG CÁCH
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

        // LOGIC LỰA CHỌN TỐC ĐỘ GIAO & ETA
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
        }

        BigDecimal shippingFee = baseShippingFee.add(extraShippingFee);
        int travelTimeMinutes = (int) Math.ceil(distanceKm * minutesPerKm);
        int totalMinutes = prepTimeMinutes + travelTimeMinutes;
        LocalDateTime expectedDeliveryTime = LocalDateTime.now().plusMinutes(totalMinutes);

        // QUÉT ĐỒ ĂN VÀ TÍNH TIỀN
        BigDecimal foodTotal = BigDecimal.ZERO;

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

            BigDecimal currentFoodPrice = item.getFood().getPrice();
            java.util.Optional<com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem> flashSaleOpt =
                    flashSaleItemRepository.findActiveFlashSaleItemByFoodId(item.getFood().getId(), LocalDateTime.now());

            if (flashSaleOpt.isPresent()) {
                com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem fsItem = flashSaleOpt.get();
                if (fsItem.getSoldQuantity() + item.getQuantity() > fsItem.getStockQuantity()) {
                    throw new RuntimeException("Món [" + item.getFood().getName() + "] đã hết suất Flashsale! Vui lòng giảm số lượng hoặc đợi đợt sau.");
                }
                currentFoodPrice = fsItem.getSalePrice();
            }

            BigDecimal pricePerItem = currentFoodPrice.add(optionsPrice);
            BigDecimal itemTotal = pricePerItem.multiply(BigDecimal.valueOf(item.getQuantity()));
            foodTotal = foodTotal.add(itemTotal);
        }

        // XỬ LÝ XẾP CHỒNG VOUCHER
        BigDecimal totalOrderDiscount = BigDecimal.ZERO;
        BigDecimal totalShippingDiscount = BigDecimal.ZERO;
        boolean hasShippingDiscountType = false;
        boolean hasOrderDiscountType = false;

        if (dto.getVoucherCodes() != null && !dto.getVoucherCodes().isEmpty()) {
            for (String code : dto.getVoucherCodes()) {
                if (code == null || code.trim().isEmpty()) continue;
                com.uit.fooddelivery_api.modules.voucher.entities.Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(code)
                        .orElseThrow(() -> new RuntimeException("Mã giảm giá [" + code + "] không tồn tại hoặc đã hết lượt kích hoạt!"));

                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
                    throw new RuntimeException("Mã giảm giá [" + code + "] đã hết hạn hoặc chưa đến thời gian sử dụng!");
                }
                if (voucher.getStockQuantity() <= 0) {
                    throw new RuntimeException("Mã giảm giá [" + code + "] đã hết lượt sử dụng trên hệ thống!");
                }
                if (foodTotal.compareTo(voucher.getMinOrderValue()) < 0) {
                    throw new RuntimeException("Mã [" + code + "] yêu cầu giá trị đơn hàng tối thiểu từ " + voucher.getMinOrderValue() + "đ!");
                }

                if (voucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.SHIPPING_DISCOUNT) {
                    if (hasShippingDiscountType) throw new RuntimeException("Hệ thống chỉ cho phép áp dụng tối đa 1 mã giảm giá phí vận chuyển trên một đơn hàng!");
                    hasShippingDiscountType = true;
                    BigDecimal calc = shippingFee.multiply(BigDecimal.valueOf(voucher.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    totalShippingDiscount = calc.min(voucher.getMaxDiscount()).min(shippingFee);
                } else if (voucher.getType() == com.uit.fooddelivery_api.modules.voucher.entities.VoucherType.ORDER_DISCOUNT) {
                    if (hasOrderDiscountType) throw new RuntimeException("Hệ thống chỉ cho phép áp dụng tối đa 1 mã giảm giá hóa đơn trên một đơn hàng!");
                    hasOrderDiscountType = true;
                    BigDecimal calc = foodTotal.multiply(BigDecimal.valueOf(voucher.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    totalOrderDiscount = calc.min(voucher.getMaxDiscount()).min(foodTotal);
                }
            }
        }

        BigDecimal totalDiscountAmount = totalOrderDiscount.add(totalShippingDiscount);
        BigDecimal finalTotal = foodTotal.add(shippingFee).subtract(totalDiscountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;

        return com.uit.fooddelivery_api.modules.order.dtos.OrderPreviewResponseDTO.builder()
                .foodTotal(foodTotal)
                .shippingFee(shippingFee)
                .totalOrderDiscount(totalOrderDiscount)
                .totalShippingDiscount(totalShippingDiscount)
                .totalDiscountAmount(totalDiscountAmount)
                .finalTotal(finalTotal)
                .distanceKm(distanceKm)
                .expectedDeliveryTime(expectedDeliveryTime)
                .build();
    }

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

            // TÍNH TOÁN TIỀN TOPPING (Giữ nguyên)
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

            // ==========================================
            // LOGIC FLASHSALE & ANTI-OVERSELLING (ISSUE #17)
            // ==========================================
            BigDecimal currentFoodPrice = item.getFood().getPrice(); // Lấy giá gốc làm mặc định
            java.util.Optional<com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem> flashSaleOpt =
                    flashSaleItemRepository.findActiveFlashSaleItemByFoodId(item.getFood().getId(), LocalDateTime.now());

            if (flashSaleOpt.isPresent()) {
                com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem fsItem = flashSaleOpt.get();

                // Kiểm tra xem kho Sale còn đủ suất để bán không?
                if (fsItem.getSoldQuantity() + item.getQuantity() > fsItem.getStockQuantity()) {
                    throw new RuntimeException("Món [" + item.getFood().getName() + "] đã hết suất Flashsale! Vui lòng giảm số lượng hoặc đợi đợt sau.");
                }

                // Đổi sang giá Sale
                currentFoodPrice = fsItem.getSalePrice();

                // Tăng số lượng đã bán. NHỜ CÓ @Version, DATABASE SẼ TỰ ĐỘNG CHẶN NẾU CÓ 2 NGƯỜI CÙNG GHI ĐÈ LÊN SỐ LƯỢNG NÀY
                fsItem.setSoldQuantity(fsItem.getSoldQuantity() + item.getQuantity());
                flashSaleItemRepository.save(fsItem);
            }
            // ==========================================

            // Tiền 1 món = Giá (Gốc hoặc Flashsale) + Tiền Topping
            BigDecimal pricePerItem = currentFoodPrice.add(optionsPrice);
            BigDecimal itemTotal = pricePerItem.multiply(BigDecimal.valueOf(item.getQuantity()));
            foodTotal = foodTotal.add(itemTotal);

            // Lưu vào OrderItem kèm theo JSON topping
            orderItems.add(OrderItem.builder()
                    .order(order)
                    .food(item.getFood())
                    .quantity(item.getQuantity())
                    .price(pricePerItem)
                    .selectedOptions(item.getSelectedOptions())
                    .build());
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

        walletService.processTransaction(customer.getId(), finalTotal.negate(), "PAYMENT", "ORDER_" + order.getId(), "Thanh toán đơn hàng đồ ăn");

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUserId(customer.getId());

        notificationService.pushNotification(
                restaurant.getMerchant().getId(),
                "Đơn hàng mới",
                "Bạn có đơn hàng mới từ " + customer.getFullName() + ". Mã đơn: #" + savedOrder.getId(),
                "ORDER_UPDATE"
        );

        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId, User actionUser, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        boolean isCustomer = order.getUser().getId().equals(actionUser.getId());
        boolean isMerchant = order.getRestaurant().getMerchant().getId().equals(actionUser.getId());

        if (!isCustomer && !isMerchant) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
        }

        // 1. RÀNG BUỘC TRẠNG THÁI (State Machine)
        if (order.getStatus().equals("COMPLETED") || order.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Đơn hàng này đã kết thúc, không thể hủy!");
        }

        if (isCustomer && !order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Quán đã bắt đầu chuẩn bị món, bạn không thể hủy đơn lúc này! Vui lòng liên hệ trực tiếp quán.");
        }

        // 2. ĐỔI TRẠNG THÁI & LƯU LÝ DO
        order.setStatus("CANCELLED");
        order.setCancelReason(reason);

        // ==========================================
        // 3. LOGIC HOÀN TIỀN VÀ TRẢ LẠI VOUCHER
        // ==========================================

        // A. Hoàn tiền tự động vào Ví (Issue #27)
        // Số tiền đưa vào là số DƯƠNG vì ta muốn CỘNG tiền lại cho Khách
        walletService.processTransaction(
                order.getUser().getId(),
                order.getTotalAmount(),
                "REFUND",
                "ORDER_" + order.getId(),
                "Hoàn tiền do hủy đơn hàng: " + reason
        );

        // B. Trả lại lượt sử dụng Voucher vào kho (Issue #16)
        if (order.getVouchers() != null && !order.getVouchers().isEmpty()) {
            for (com.uit.fooddelivery_api.modules.voucher.entities.Voucher v : order.getVouchers()) {
                v.setStockQuantity(v.getStockQuantity() + 1); // Trả lại 1 lượt
                voucherRepository.save(v);
            }
        }

        // ==========================================
        // 4. BẮN THÔNG BÁO REAL-TIME (Issue #11)
        // ==========================================
        if (isCustomer) {
            // Khách tự hủy -> Báo cho Chủ quán biết để ngưng nấu
            notificationService.pushNotification(
                    order.getRestaurant().getMerchant().getId(),
                    "Đơn hàng bị hủy",
                    "Khách hàng đã hủy đơn #" + order.getId() + ". Lý do: " + reason,
                    "ORDER_UPDATE"
            );
        } else if (isMerchant) {
            // Quán hủy (do hết món) -> Xin lỗi khách
            notificationService.pushNotification(
                    order.getUser().getId(),
                    "Đơn hàng bị hủy",
                    "Rất tiếc, Quán đã hủy đơn #" + order.getId() + " của bạn. Lý do: " + reason + ". Tiền đã được hoàn lại vào Ví.",
                    "ORDER_UPDATE"
            );
        }

        // Xóa liên kết tài xế (nếu có tài xế đang nhận) để giải phóng tài xế
        if (order.getDriver() != null) {
            notificationService.pushNotification(
                    order.getDriver().getId(),
                    "Hủy chuyến",
                    "Đơn hàng #" + order.getId() + " đã bị hủy. Bạn có thể tiếp tục nhận đơn mới.",
                    "SYSTEM"
            );
            // Có thể thêm logic gọi DriverDispatchService để set tài xế về ONLINE ở đây.
        }

        return orderRepository.save(order);
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

        notificationService.pushNotification(
                order.getUser().getId(),
                "Giao hàng thành công",
                "Đơn hàng #" + order.getId() + " đã được giao đến bạn. Chúc bạn ngon miệng!",
                "ORDER_UPDATE"
        );

        return orderRepository.save(order);
    }

    // 1. QUÁN ĂN: Bấm xác nhận nấu xong món ăn -> Sinh mã Pickup Code
    @Transactional
    public Order merchantCompletePreparation(Long orderId, User merchant) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!order.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền xử lý đơn hàng của quán khác!");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng phải ở trạng thái CHỜ XỬ LÝ mới có thể xác nhận nấu xong!");
        }

        // Sinh mã lấy đồ ngẫu nhiên gồm 4 chữ số (Ví dụ: 8439)
        String generatedPickupCode = String.format("%04d", new java.util.Random().nextInt(10000));

        order.setStatus("PREPARING"); // Hoặc đặt trạng thái READY_FOR_PICKUP tùy thiết kế của bạn
        order.setPickupCode(generatedPickupCode);
        Order savedOrder = orderRepository.save(order);

        // Bắn thông báo Real-time cho khách biết quán đang chuẩn bị đồ ăn
        notificationService.pushNotification(
                order.getUser().getId(),
                "Quán đang chuẩn bị món \uD83C\uDF73",
                "Đơn hàng từ " + order.getRestaurant().getName() + " đang được chuẩn bị. Mã lấy hàng của tài xế: " + generatedPickupCode,
                "ORDER_UPDATE"
        );

        return savedOrder;
    }

    // 2. TÀI XẾ: Nhập mã Pickup Code từ chủ quán để lấy đồ đi giao
    @Transactional
    public Order driverConfirmPickup(Long orderId, User driver, String inputCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (order.getDriver() == null || !order.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("Bạn không phải là tài xế được điều phối cho đơn hàng này!");
        }

        // XÁC THỰC MÃ LẤY ĐỒ CHỐNG GIAN LẬN
        if (order.getPickupCode() == null || !order.getPickupCode().equals(inputCode)) {
            throw new RuntimeException("Mã lấy hàng không chính xác! Vui lòng kiểm tra lại với chủ quán.");
        }

        // Sinh tiếp mã PIN 4 số gửi cho khách hàng cầm, tài xế không được biết trước
        String generatedDeliveryPin = String.format("%04d", new java.util.Random().nextInt(10000));

        order.setStatus("DELIVERING");
        order.setDeliveryPin(generatedDeliveryPin);
        Order savedOrder = orderRepository.save(order);

        // Bắn thông báo chứa mã PIN cho Khách hàng bảo mật
        notificationService.pushNotification(
                order.getUser().getId(),
                "Tài xế đang giao hàng \uD83D\uDEB4",
                "Tài xế " + driver.getFullName() + " đang mang món ăn đến cho bạn. Hãy đưa mã PIN này cho tài xế khi nhận đồ: " + generatedDeliveryPin,
                "ORDER_UPDATE"
        );

        return savedOrder;
    }

    // 3. TÀI XẾ: Đến nơi, xin mã PIN của khách gõ vào để hoàn thành đơn & Nhận tiền
    @Transactional
    public Order completeOrderSecure(Long orderId, User driver, String inputPin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!"DELIVERING".equals(order.getStatus()) || !order.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("Bạn không có quyền hoàn thành đơn hàng này!");
        }

        // XÁC THỰC MÃ PIN CHỐNG QUỴT ĐƠN / GIAO NHẦM NHÀ
        if (order.getDeliveryPin() == null || !order.getDeliveryPin().equals(inputPin)) {
            throw new RuntimeException("Mã PIN nhận hàng từ khách hàng không chính xác!");
        }

        order.setStatus("COMPLETED");

        // Bắn tiền thu nhập về ví của chủ quán (Merchant) qua Sổ cái kế toán
        walletService.processTransaction(
                order.getRestaurant().getMerchant().getId(),
                order.getTotalAmount().subtract(order.getShippingFee()), // Tiền đồ ăn về quán
                "REVENUE",
                "ORDER_" + order.getId(),
                "Doanh thu bán hàng đơn #" + order.getId()
        );

        // Bắn tiền ship về cho Ví của Tài xế (Driver)
        walletService.processTransaction(
                driver.getId(),
                order.getShippingFee(), // Tiền ship về tài xế
                "REVENUE",
                "ORDER_" + order.getId(),
                "Thù lao giao hàng đơn #" + order.getId()
        );

        // Bắn thông báo chúc mừng cả 2 bên
        notificationService.pushNotification(
                order.getUser().getId(),
                "Đơn hàng hoàn tất \uD83C\uDF89",
                "Cảm ơn bạn đã đặt hàng tại FoodDelivery! Đơn hàng #" + order.getId() + " đã giao thành công.",
                "ORDER_UPDATE"
        );

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

    // Chủ quán xóa 1 món ra khỏi đơn (chỉ khi đơn đang PENDING)
    @Transactional
    public Order removeItemFromOrder(Long orderId, Long foodId, User merchant) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!order.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa đơn hàng của quán khác!");
        }

        if (!order.getStatus().equals("PENDING") && !order.getStatus().equals("PREPARING")) {
            throw new RuntimeException("Chỉ có thể sửa đơn hàng đang chờ xử lý hoặc đang chuẩn bị!");
        }

        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Đơn hàng không có món nào để xóa!");
        }

        // Tìm và xóa item khớp với foodId
        OrderItem toRemove = items.stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn trong đơn hàng!"));

        if (items.size() == 1) {
            throw new RuntimeException("Không thể xóa tất cả món, đơn phải có ít nhất 1 món!");
        }

        items.remove(toRemove);

        // Tính lại tổng tiền món ăn
        BigDecimal newFoodTotal = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Giữ nguyên shippingFee và discountAmount, chỉ thay phần tiền đồ ăn
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        order.setTotalAmount(newFoodTotal.add(shipping).subtract(discount));

        return orderRepository.save(order);
    }
}