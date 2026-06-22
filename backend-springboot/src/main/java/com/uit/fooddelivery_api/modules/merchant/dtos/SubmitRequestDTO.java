package com.uit.fooddelivery_api.modules.merchant.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRequestDTO {
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private String confirmationCode;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
}