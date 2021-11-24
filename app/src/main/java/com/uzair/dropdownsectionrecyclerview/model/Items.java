package com.uzair.dropdownsectionrecyclerview.model;

public class Items {
    private String itemName, imageUrl, skuCode, groupId, branId;
    private int uid , productId, boxSize, ctnSize;

    public Items() {
    }

    public Items(String itemName, String imageUrl, String skuCode, String groupId, String branId, int uid, int productId, int boxSize, int ctnSize) {
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.skuCode = skuCode;
        this.groupId = groupId;
        this.branId = branId;
        this.uid = uid;
        this.productId = productId;
        this.boxSize = boxSize;
        this.ctnSize = ctnSize;
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
