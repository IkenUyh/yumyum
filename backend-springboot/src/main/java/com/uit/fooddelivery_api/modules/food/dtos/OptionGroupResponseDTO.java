package com.uit.fooddelivery_api.modules.food.dtos;

import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class OptionGroupResponseDTO {
    private Long id;
    private String name;
    private Boolean isRequired;
    private Integer maxChoices;
    private List<OptionItemResponseDTO> items;

    public static OptionGroupResponseDTO fromEntity(FoodOptionGroup group) {
        return OptionGroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .isRequired(group.getIsRequired())
                .maxChoices(group.getMaxChoices())
                .items(group.getOptionItems() != null ?
                        group.getOptionItems().stream().map(OptionItemResponseDTO::fromEntity).toList() :
                        List.of())
                .build();
    }
}