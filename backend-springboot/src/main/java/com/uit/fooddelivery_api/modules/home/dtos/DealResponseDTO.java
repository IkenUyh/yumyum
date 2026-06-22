package com.uit.fooddelivery_api.modules.home.dtos;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealResponseDTO {
    private List<RecommendedDealDTO> deals;
    private int totalPages;
    private int currentPage;
}
