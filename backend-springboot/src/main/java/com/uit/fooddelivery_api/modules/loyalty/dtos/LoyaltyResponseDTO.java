package com.uit.fooddelivery_api.modules.loyalty.dtos;

import com.uit.fooddelivery_api.modules.loyalty.entities.LoyaltyPoint;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class LoyaltyResponseDTO {
    private Integer currentPoints;
    private Integer checkinStreak;
    private LocalDate lastCheckinDate;
    private Boolean canCheckInToday; // Trả về cho Frontend biết hôm nay nút Điểm danh có sáng lên không

    public static LoyaltyResponseDTO fromEntity(LoyaltyPoint lp, boolean canCheckIn) {
        return LoyaltyResponseDTO.builder()
                .currentPoints(lp.getCurrentPoints())
                .checkinStreak(lp.getCheckinStreak())
                .lastCheckinDate(lp.getLastCheckinDate())
                .canCheckInToday(canCheckIn)
                .build();
    }
}