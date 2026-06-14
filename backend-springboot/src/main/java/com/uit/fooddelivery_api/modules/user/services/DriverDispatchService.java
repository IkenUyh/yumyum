package com.uit.fooddelivery_api.modules.user.services;

import com.uit.fooddelivery_api.modules.notification.services.NotificationService;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.user.entities.DriverProfile;
import com.uit.fooddelivery_api.modules.user.repositories.DriverProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverDispatchService {

    private final DriverLocationService locationService;
    private final DriverProfileRepository driverProfileRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    // Hàm gọi khi Quán ăn bấm "Đã chuẩn bị xong món"
    @Transactional
    public void assignNearestDriverToOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        double resLat = order.getRestaurant().getLatitude().doubleValue();
        double resLng = order.getRestaurant().getLongitude().doubleValue();

        // 1. Quét Redis lấy danh sách tài xế trong bán kính 3Km quanh quán
        List<Long> nearbyDriverIds = locationService.findNearbyDrivers(resLat, resLng, 3.0);

        if (nearbyDriverIds.isEmpty()) {
            throw new RuntimeException("Hiện tại không có tài xế nào ở gần quán ăn!");
        }

        // 2. Chạy vòng lặp tìm tài xế GẦN NHẤT + ĐANG RẢNH (Tránh việc gọi trúng tài xế đang chở đơn khác)
        DriverProfile selectedDriver = null;
        for (Long driverId : nearbyDriverIds) {
            DriverProfile profile = driverProfileRepository.findById(driverId).orElse(null);
            if (profile != null && "ONLINE".equals(profile.getStatus())) {
                selectedDriver = profile;
                break; // Chọn được người đầu tiên (gần nhất) là thoát luôn
            }
        }

        if (selectedDriver == null) {
            throw new RuntimeException("Các tài xế ở gần đều đang kẹt đơn khác, vui lòng thử lại sau ít phút!");
        }

        // 3. Chốt tài xế: Đổi trạng thái Tài xế thành BUSY và gán vào Đơn hàng
        selectedDriver.setStatus("BUSY");
        selectedDriver.setCurrentOrderId(order.getId());
        driverProfileRepository.save(selectedDriver);

        order.setDriver(selectedDriver.getUser());
        order.setStatus("DELIVERING");
        orderRepository.save(order);

        // 4. Bắn thông báo Real-time (SSE - Issue #11) cho Tài xế để họ chạy tới quán lấy đồ
        notificationService.pushNotification(
                selectedDriver.getUserId(),
                "Bạn có đơn hàng mới!",
                "Hãy đến quán " + order.getRestaurant().getName() + " để nhận đơn #" + order.getId(),
                "SYSTEM"
        );
    }
}