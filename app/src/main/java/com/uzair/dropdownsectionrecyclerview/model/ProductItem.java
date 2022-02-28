package com.uzair.dropdownsectionrecyclerview.model;

import java.util.ArrayList;
import java.util.List;

public class ProductItem {
    private String productName, status , uid, categoryId, companyId;
    public List<Items> itemsList = new ArrayList<>();

    public ProductItem() {}

    public ProductItem(String productName, String status, String uid, String categoryId, String companyId , List<Items> itemsList) {
        this.productName = productName;
        this.status = status;
        this.uid = uid;
        this.categoryId = categoryId;
        this.companyId = companyId;
        this.itemsList = itemsList;
    }


    public List<Items> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Items> itemsList) {
        this.itemsList = itemsList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
