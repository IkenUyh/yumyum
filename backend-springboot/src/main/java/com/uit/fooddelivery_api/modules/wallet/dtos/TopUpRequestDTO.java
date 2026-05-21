package com.uit.fooddelivery_api.modules.wallet.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class TopUpRequestDTO {
    private BigDecimal amount;
}