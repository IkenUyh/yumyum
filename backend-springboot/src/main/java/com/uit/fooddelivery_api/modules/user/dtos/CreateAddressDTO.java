package com.uit.fooddelivery_api.modules.user.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateAddressDTO {
    private String addressName;
    private String recipientName;
    private String phoneNumber;
    private String detailedAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault;
}