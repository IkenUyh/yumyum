package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponseDTO {
    private String title;
    private String subtitle;
    private List<FoodMenuItemDTO> items;
}
