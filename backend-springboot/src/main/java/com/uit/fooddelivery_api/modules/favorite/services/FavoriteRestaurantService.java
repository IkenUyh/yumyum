package com.uit.fooddelivery_api.modules.favorite.services;

import com.uit.fooddelivery_api.modules.favorite.dtos.FavoriteRestaurantResponseDTO;
import com.uit.fooddelivery_api.modules.favorite.dtos.ToggleFavoriteResponseDTO;
import com.uit.fooddelivery_api.modules.favorite.entities.FavoriteRestaurant;
import com.uit.fooddelivery_api.modules.favorite.repositories.FavoriteRestaurantRepository;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserAddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteRestaurantService {

    private final FavoriteRestaurantRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserAddressRepository addressRepository;

    /**
     * Toggle tim: Nếu chưa thích thì thêm vào, nếu đã thích thì bỏ ra.
     * Android chỉ cần gọi 1 endpoint duy nhất cho cả hai hành động.
     */
    @Transactional
    public ToggleFavoriteResponseDTO toggleFavorite(Long restaurantId, User user) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng với id: " + restaurantId));

        Optional<FavoriteRestaurant> existing = favoriteRepository.findByUserIdAndRestaurantId(user.getId(), restaurantId);

        if (existing.isPresent()) {
            // Đã yêu thích rồi → bỏ yêu thích
            favoriteRepository.delete(existing.get());
            return ToggleFavoriteResponseDTO.builder()
                    .isFavorited(false)
                    .message("Đã bỏ yêu thích!")
                    .restaurantId(restaurantId)
                    .build();
        } else {
            // Chưa yêu thích → thêm vào
            FavoriteRestaurant favorite = FavoriteRestaurant.builder()
                    .user(user)
                    .restaurant(restaurant)
                    .build();
            favoriteRepository.save(favorite);
            return ToggleFavoriteResponseDTO.builder()
                    .isFavorited(true)
                    .message("Đã thêm vào yêu thích!")
                    .restaurantId(restaurantId)
                    .build();
        }
    }

    /**
     * Lấy toàn bộ danh sách nhà hàng yêu thích của user hiện tại.
     */
    public List<FavoriteRestaurantResponseDTO> getMyFavorites(User user) {
        var defaultAddresses = addressRepository.findByUserIdAndIsDefaultTrue(user.getId());
        Double userLat = 10.8750; // default lat
        Double userLng = 106.8000; // default lng
        if (!defaultAddresses.isEmpty() && defaultAddresses.get(0).getLatitude() != null && defaultAddresses.get(0).getLongitude() != null) {
            userLat = defaultAddresses.get(0).getLatitude().doubleValue();
            userLng = defaultAddresses.get(0).getLongitude().doubleValue();
        }

        final Double finalLat = userLat;
        final Double finalLng = userLng;

        return favoriteRepository.findByUserId(user.getId())
                .stream()
                .map(f -> FavoriteRestaurantResponseDTO.fromEntity(f, finalLat, finalLng))
                .toList();
    }

    /**
     * Kiểm tra xem user có đang yêu thích nhà hàng này không.
     * Dùng để Android khởi tạo trạng thái icon tim khi mở màn hình chi tiết quán.
     */
    public boolean isFavorited(Long restaurantId, User user) {
        return favoriteRepository.existsByUserIdAndRestaurantId(user.getId(), restaurantId);
    }
}
