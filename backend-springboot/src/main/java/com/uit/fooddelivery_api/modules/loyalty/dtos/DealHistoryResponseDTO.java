package com.uit.fooddelivery_api.modules.loyalty.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealHistoryResponseDTO {
    private String dealId;
    private String merchantName;
    private String purchaseDate;
    private String dealTitle;
    private String price;
    private String expiryText;
    private String quantityText;
    private String statusText;
    private String appliedOrderId;
}
