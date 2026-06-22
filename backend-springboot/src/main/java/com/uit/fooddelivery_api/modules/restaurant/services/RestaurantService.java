package com.uit.fooddelivery_api.modules.restaurant.services;

import com.uit.fooddelivery_api.modules.restaurant.dtos.CreateRestaurantDTO;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final com.uit.fooddelivery_api.modules.user.services.CloudinaryService cloudinaryService;
    private final com.uit.fooddelivery_api.modules.search.services.RestaurantSearchService restaurantSearchService;

    @jakarta.transaction.Transactional
    public String updateRestaurantImage(Long restaurantId, org.springframework.web.multipart.MultipartFile file, User merchant) throws java.io.IOException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quán ăn!"));

        if (!restaurant.getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền đổi ảnh của nhà hàng khác!");
        }

        String imageUrl = cloudinaryService.uploadAvatar(file);
        restaurant.setImageUrl(imageUrl);
        Restaurant saved = restaurantRepository.save(restaurant);
        restaurantSearchService.syncRestaurant(saved);

        return imageUrl;
    }

    public Restaurant createRestaurant(CreateRestaurantDTO dto, User merchant) {
        // Tao object Restaurant tu DTO va gan chu quan
        Restaurant restaurant = Restaurant.builder()
                .merchant(merchant)
                .name(dto.getName())
                .address(dto.getAddress())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .isActive(true)
                .build();

        // Luu vao database
        Restaurant saved = restaurantRepository.save(restaurant);
        restaurantSearchService.syncRestaurant(saved);
        return saved;
    }

    public List<Restaurant> getAllRestaurants() {
        // Lay het nha hang ra de khach hang chon lua
        return restaurantRepository.findAll();
    }

    // Lấy chi tiết 1 nhà hàng theo ID (public - không cần đăng nhập)
    public Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng với id: " + restaurantId));
    }

    // Lưu thay đổi thông tin quán
    public Restaurant save(Restaurant restaurant) {
        Restaurant saved = restaurantRepository.save(restaurant);
        restaurantSearchService.syncRestaurant(saved);
        return saved;
    }
}