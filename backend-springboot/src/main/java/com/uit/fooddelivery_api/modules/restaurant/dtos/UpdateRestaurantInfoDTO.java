package com.uit.fooddelivery_api.modules.restaurant.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRestaurantInfoDTO {
    private String name;
    private String address;
    private String openTime;   // Dạng "HH:mm" ví dụ: "07:00"
    private String closeTime;  // Dạng "HH:mm" ví dụ: "22:00"
    private String imageUrl;
}
