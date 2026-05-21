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
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        // Lay het nha hang ra de khach hang chon lua
        return restaurantRepository.findAll();
    }
}