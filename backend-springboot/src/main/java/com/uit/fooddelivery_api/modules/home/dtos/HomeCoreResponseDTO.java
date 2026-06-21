package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeCoreResponseDTO {
    private List<BannerDTO> banners;
    private List<FoodCategoryDTO> categories;
    private List<FoodMenuItemDTO> flashSales;
}
