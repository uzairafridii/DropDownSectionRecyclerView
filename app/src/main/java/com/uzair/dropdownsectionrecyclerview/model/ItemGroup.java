package com.uzair.dropdownsectionrecyclerview.model;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup
{
    private String brandId,groupCode,name, itemGroupId;
    public List<Items> itemsList = new ArrayList<>();

    public ItemGroup() {}

    public ItemGroup(String brandId, String groupCode, String name, String itemGroupId) {
        this.brandId = brandId;
        this.groupCode = groupCode;
        this.name = name;
        this.itemGroupId = itemGroupId;
    }

    public ItemGroup(String brandId, String groupCode, String name, String itemGroupId, List<Items> itemsList) {
        this.brandId = brandId;
        this.groupCode = groupCode;
        this.name = name;
        this.itemGroupId = itemGroupId;
        this.itemsList = itemsList;
    }

    public List<Items> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Items> itemsList) {
        this.itemsList = itemsList;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(String itemGroupId) {
        this.itemGroupId = itemGroupId;
    }
}
