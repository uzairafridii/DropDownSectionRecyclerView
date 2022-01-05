package com.uzair.dropdownsectionrecyclerview.model;

import java.util.List;

public class Common
{
    String title;
    List<Items> itemsList;

    public Common() {
    }

    public Common(String title, List<Items> itemsList) {
        this.title = title;
        this.itemsList = itemsList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Items> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Items> itemsList) {
        this.itemsList = itemsList;
    }
}
