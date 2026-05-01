package com.example.uitpayapp.giftexchange;

public class CategoryModel {
    private String name;
    private String type;
    private boolean isSelected;

    public CategoryModel(String name, String type, boolean isSelected) {
        this.name = name;
        this.type = type;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
