package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDTO {
    private List<RestaurantHomeDTO> brands;
}
