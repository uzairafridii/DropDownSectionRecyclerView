package com.uzair.dropdownsectionrecyclerview.model;

import java.util.List;

public class Items {
    private String itemName, imageUrl, skuCode, groupId, branId , frequent, must_sell, above_target , below_target;
    private int uid , productId, boxSize, ctnSize;


    public Items() {
    }

    public Items(String itemName, String imageUrl, String skuCode, String groupId, String branId,
                 String frequent, String must_sell, String above_target,
                 String below_target, int uid, int productId, int boxSize, int ctnSize) {
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.skuCode = skuCode;
        this.groupId = groupId;
        this.branId = branId;
        this.frequent = frequent;
        this.must_sell = must_sell;
        this.above_target = above_target;
        this.below_target = below_target;
        this.uid = uid;
        this.productId = productId;
        this.boxSize = boxSize;
        this.ctnSize = ctnSize;
    }

    public String getFrequent() {
        return frequent;
    }

    public void setFrequent(String frequent) {
        this.frequent = frequent;
    }

    public String getMust_sell() {
        return must_sell;
    }

    public void setMust_sell(String must_sell) {
        this.must_sell = must_sell;
    }

    public String getAbove_target() {
        return above_target;
    }

    public void setAbove_target(String above_target) {
        this.above_target = above_target;
    }

    public String getBelow_target() {
        return below_target;
    }

    public void setBelow_target(String below_target) {
        this.below_target = below_target;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBranId() {
        return branId;
    }

    public void setBranId(String branId) {
        this.branId = branId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(int boxSize) {
        this.boxSize = boxSize;
    }

    public int getCtnSize() {
        return ctnSize;
    }

    public void setCtnSize(int ctnSize) {
        this.ctnSize = ctnSize;
    }
}
