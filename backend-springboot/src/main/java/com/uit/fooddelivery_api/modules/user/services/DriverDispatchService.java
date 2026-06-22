package com.uit.fooddelivery_api.modules.user.services;

import com.uit.fooddelivery_api.modules.notification.services.NotificationService;
import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.system.entities.SystemParameter;
import com.uit.fooddelivery_api.modules.system.repositories.SystemParameterRepository;
import com.uit.fooddelivery_api.modules.user.entities.DriverProfile;
import com.uit.fooddelivery_api.modules.user.repositories.DriverProfileRepository;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverDispatchService {

    private final DriverLocationService locationService;
    private final DriverProfileRepository driverProfileRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final WalletRepository walletRepository;
    private final SystemParameterRepository systemParameterRepository;

    @Transactional
    public void assignNearestDriverToOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (order.getRestaurant().getLatitude() == null || order.getRestaurant().getLongitude() == null) {
            throw new RuntimeException("Lỗi: Quán ăn này chưa được cập nhật tọa độ!");
        }

        double resLat = order.getRestaurant().getLatitude().doubleValue();
        double resLng = order.getRestaurant().getLongitude().doubleValue();

        List<Long> nearbyDriverIds = locationService.findNearbyDrivers(resLat, resLng, 3.0);

        if (nearbyDriverIds.isEmpty()) {
            throw new RuntimeException("Hiện tại không có tài xế nào ở gần quán ăn!");
        }

        BigDecimal minDeposit = new BigDecimal(systemParameterRepository.findByParamKey("DRIVER_MIN_DEPOSIT").map(SystemParameter::getParamValue).orElse("50000"));
        int maxBatchOrders = Integer.parseInt(systemParameterRepository.findByParamKey("DRIVER_MAX_BATCH_ORDERS").map(SystemParameter::getParamValue).orElse("2"));

        DriverProfile selectedDriver = null;

        for (Long driverId : nearbyDriverIds) {
            DriverProfile profile = driverProfileRepository.findById(driverId).orElse(null);
            if (profile == null) continue;

            Wallet driverWallet = walletRepository.findByUserId(driverId).orElse(null);
            if (driverWallet == null || driverWallet.getBalance().compareTo(minDeposit) < 0) {
                continue; // Bỏ qua nếu ví không đủ cọc
            }

            boolean isOnline = "ONLINE".equals(profile.getStatus());
            boolean canBatch = "BUSY".equals(profile.getStatus()) && (profile.getCurrentOrderCount() == null || profile.getCurrentOrderCount() < maxBatchOrders);

            if (isOnline || canBatch) {
                selectedDriver = profile;
                break;
            }
        }

        if (selectedDriver == null) {
            throw new RuntimeException("Các tài xế ở gần đều đang kẹt đơn khác hoặc ví không đủ điều kiện!");
        }

        selectedDriver.setStatus("BUSY");
        selectedDriver.setCurrentOrderId(order.getId());
        selectedDriver.setCurrentOrderCount((selectedDriver.getCurrentOrderCount() == null ? 0 : selectedDriver.getCurrentOrderCount()) + 1);
        driverProfileRepository.save(selectedDriver);

        order.setDriver(selectedDriver.getUser());
        order.setStatus("DELIVERING");
        orderRepository.save(order);

        notificationService.pushNotification(selectedDriver.getUserId(), "Đơn hàng mới!", "Hãy đến quán " + order.getRestaurant().getName() + " để nhận hàng.", "SYSTEM");
    }
}