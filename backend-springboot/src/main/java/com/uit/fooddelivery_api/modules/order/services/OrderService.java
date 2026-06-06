package com.uit.fooddelivery_api.modules.order.services;

import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.order.dtos.CartItemDTO;
import com.uit.fooddelivery_api.modules.order.dtos.CreateOrderDTO;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.entities.OrderItem;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public Order createOrder(CreateOrderDTO dto, User customer) {
        // 1. Check nhà hàng có tồn tại không
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng!"));

        // 2. Lấy ví của khách hàng
        Wallet wallet = walletRepository.findByUserId(customer.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của người dùng!"));

        // 3. Khởi tạo object Order trống
        Order order = Order.builder()
                .user(customer)
                .restaurant(restaurant)
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 4. Duyệt danh sách món chọn để tính tiền thực tế theo giá DB
        for (CartItemDTO itemDTO : dto.getItems()) {
            Food food = foodRepository.findById(itemDTO.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn có ID: " + itemDTO.getFoodId()));

            if (!food.getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException("Món " + food.getName() + " không thuộc nhà hàng này!");
            }

            // Tiền món = Giá DB * Số lượng
            BigDecimal itemTotal = food.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            total = total.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .food(food)
                    .quantity(itemDTO.getQuantity())
                    .price(food.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);

        // 5. Kiểm tra tài khoản có đủ tiền trả không
        if (wallet.getBalance().compareTo(total) < 0) {
            throw new RuntimeException("Số dư ví không đủ! Vui lòng nạp thêm tiền.");
        }

        // 6. Trừ tiền ví của khách
        wallet.setBalance(wallet.getBalance().subtract(total));
        walletRepository.save(wallet);

        // 7. Lưu đơn hàng
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

        return orderRepository.save(order);
    }
}