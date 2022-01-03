package com.uzair.dropdownsectionrecyclerview.model;

import java.util.ArrayList;
import java.util.List;

public class ProductCategory
{
    private String name, id;
    public List<Items> itemsList = new ArrayList<>();

    public ProductCategory() {
    }

    public ProductCategory(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public ProductCategory(String name, String id , List<Items> itemsList) {
        this.name = name;
        this.id = id;
        this.itemsList = itemsList;
    }

    public List<Items> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Items> itemsList) {
        this.itemsList = itemsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
