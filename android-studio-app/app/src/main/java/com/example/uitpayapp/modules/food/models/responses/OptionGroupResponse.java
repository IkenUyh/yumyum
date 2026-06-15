package com.example.uitpayapp.modules.food.models.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OptionGroupResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("isRequired")
    private Boolean isRequired;

    @SerializedName("maxChoices")
    private Integer maxChoices;

    @SerializedName("items")
    private List<OptionItemResponse> items;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Boolean getIsRequired() { return isRequired; }
    public Integer getMaxChoices() { return maxChoices; }
    public List<OptionItemResponse> getItems() { return items; }
}