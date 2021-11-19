package com.uzair.dropdownsectionrecyclerview;

public class Items {
    private String itemName, imageUrl, skuCode;
    private int uid , productId, boxSize, ctnSize;

    public Items() {
    }

    public Items(String itemName, String imageUrl, String skuCode, int uid, int productId, int boxSize, int ctnSize) {
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.skuCode = skuCode;
        this.uid = uid;
        this.productId = productId;
        this.boxSize = boxSize;
        this.ctnSize = ctnSize;
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
