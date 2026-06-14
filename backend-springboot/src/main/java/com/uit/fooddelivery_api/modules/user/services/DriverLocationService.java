package com.uit.fooddelivery_api.modules.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverLocationService {

    // Inject String-based RedisTemplate
    private final RedisTemplate<String, String> redisTemplate;

    private static final String GEO_KEY = "drivers:locations"; // Tên bảng lưu trên RAM

    // 1. App Tài xế gọi hàm này mỗi 10 giây để báo vị trí hiện tại
    public void updateDriverLocation(Long driverId, double latitude, double longitude) {
        // Lưu/Cập nhật tọa độ (Longitude trước, Latitude sau theo chuẩn GeoJSON)
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(longitude, latitude), driverId.toString());
    }

    // 2. Xóa tọa độ khỏi RAM khi tài xế tắt App (OFFLINE)
    public void removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(GEO_KEY, driverId.toString());
    }

    // 3. Quét tìm danh sách ID tài xế gần quán ăn nhất (Trong bán kính N km)
    public List<Long> findNearbyDrivers(double lat, double lng, double radiusKm) {
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
        Circle area = new Circle(new Point(lng, lat), radius);

        // Quét Redis lấy danh sách tài xế, sắp xếp từ gần đến xa
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .sortAscending(); // Xếp gần nhất lên đầu

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(GEO_KEY, area, args);

        List<Long> nearestDriverIds = new ArrayList<>();
        if (results != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                nearestDriverIds.add(Long.valueOf(result.getContent().getName()));
            }
        }
        return nearestDriverIds;
    }
}