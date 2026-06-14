package com.uit.fooddelivery_api.modules.wallet.dtos;

import com.uit.fooddelivery_api.modules.wallet.entities.WalletTransaction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String type;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;

    public static TransactionResponseDTO fromEntity(WalletTransaction tx) {
        return TransactionResponseDTO.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .type(tx.getType())
                .referenceId(tx.getReferenceId())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}