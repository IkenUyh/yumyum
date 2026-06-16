package com.example.uitpayapp.modules.food.models.requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateOptionGroupRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("isRequired")
    private Boolean isRequired;

    @SerializedName("maxChoices")
    private Integer maxChoices;

    @SerializedName("items")
    private List<CreateOptionItemRequest> items;

    public CreateOptionGroupRequest(String name, Boolean isRequired, Integer maxChoices, List<CreateOptionItemRequest> items) {
        this.name = name;
        this.isRequired = isRequired;
        this.maxChoices = maxChoices;
        this.items = items;
    }

    // Getters và Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    public Integer getMaxChoices() { return maxChoices; }
    public void setMaxChoices(Integer maxChoices) { this.maxChoices = maxChoices; }
    public List<CreateOptionItemRequest> getItems() { return items; }
    public void setItems(List<CreateOptionItemRequest> items) { this.items = items; }
}