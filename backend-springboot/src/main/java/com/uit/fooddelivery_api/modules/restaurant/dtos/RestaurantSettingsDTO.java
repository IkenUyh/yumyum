package com.uit.fooddelivery_api.modules.restaurant.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantSettingsDTO {
    private Boolean isAcceptingOrders;
    private Integer maxPendingOrders;
}