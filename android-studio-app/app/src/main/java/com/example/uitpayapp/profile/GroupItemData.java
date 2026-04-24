package com.example.uitpayapp.profile;

import java.util.List;

public class GroupItemData {
    private String title;
    private List<MenuItemData> ListItems;
    public GroupItemData(String title, List<MenuItemData> ListItems) {
        this.title = title; this.ListItems = ListItems;
    }
    public String getTitle() { return title; }
    public List<MenuItemData> getListItems() { return ListItems; }
}
