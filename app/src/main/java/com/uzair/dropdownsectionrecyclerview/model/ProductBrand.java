package com.uzair.dropdownsectionrecyclerview.model;

public class ProductBrand
{
    private String name, productId, productBrandId;

    public ProductBrand() {}

    public ProductBrand(String name, String productId, String productBrandId) {
        this.name = name;
        this.productId = productId;
        this.productBrandId = productBrandId;
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
