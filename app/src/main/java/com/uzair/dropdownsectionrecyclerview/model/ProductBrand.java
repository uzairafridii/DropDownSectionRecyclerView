package com.uzair.dropdownsectionrecyclerview.model;

import java.util.ArrayList;
import java.util.List;

public class ProductBrand
{
    private String name, productId, productBrandId;
    public List<Items> itemsList = new ArrayList<>();

    public ProductBrand() {}

    public ProductBrand(String name, String productId, String productBrandId , List<Items> itemsList) {
        this.name = name;
        this.productId = productId;
        this.productBrandId = productBrandId;
        this.itemsList  = itemsList;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductBrandId() {
        return productBrandId;
    }

    public void setProductBrandId(String productBrandId) {
        this.productBrandId = productBrandId;
    }
}
