package com.uit.fooddelivery_api.modules.voucher.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherExchangeRequest {
    private String title;
    private String type; // FOOD_DISCOUNT, SHIPPING_FEE
    private Integer coinCost;
}
