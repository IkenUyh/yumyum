package com.uit.fooddelivery_api.modules.food.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreateOptionGroupDTO {
    private String name; // Ví dụ: "Kích cỡ", "Topping"
    private Boolean isRequired; // Bắt buộc chọn không?
    private Integer maxChoices; // Số lượng tối đa được chọn
    private List<CreateOptionItemDTO> items;
}