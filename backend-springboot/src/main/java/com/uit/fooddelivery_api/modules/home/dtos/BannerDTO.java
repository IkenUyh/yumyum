package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private String id;
    private String imageUrl;
    private String link;
}
