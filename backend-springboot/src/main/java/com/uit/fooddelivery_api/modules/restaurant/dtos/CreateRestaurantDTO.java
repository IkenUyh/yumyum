package com.uit.fooddelivery_api.modules.restaurant.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreateRestaurantDTO {
    private String name;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
}