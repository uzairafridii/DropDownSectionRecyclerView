package com.uzair.dropdownsectionrecyclerview.model;

public class Product {
    private String productName, status;
    private int uid, categoryId, companyId;

    public Product() {
    }

    public Product(String productName, String status, int uid, int categoryId, int companyId) {
        this.productName = productName;
        this.status = status;
        this.uid = uid;
        this.categoryId = categoryId;
        this.companyId = companyId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
