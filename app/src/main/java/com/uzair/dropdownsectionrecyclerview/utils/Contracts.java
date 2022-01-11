package com.uzair.dropdownsectionrecyclerview.utils;

public class Contracts {

    public static class DataSet {
        public static final String COL_TABLE_NAME = "dataset";
        public static final String COL_ID = "id";
        public static final String COL_BOX = "box";
        public static final String COL_CTN = "ctn";
        public static final String COL_PCS = "pcs";
        public static final String COL_TOTAL = "total";
    }

    public static class ProductsBrand {
        public static final String COL_TABLE_NAME = "products_brand";
        public static final String COL_BRAND_NAME = "brand_name";
        public static final String COL_PRODUCT_ID = "product_id";
        public static final String COL_BRAND_ID = "brand_id";

    }

    public static class Products {
        public static final String COL_TABLE_NAME = "products";
        public static final String COL_PRODUCT_NAME = "product_name";
        public static final String COL_COMPANY_ID = "company_id";
        public static final String COL_CATEGORY_ID = "category_id";
        public static final String COL_STATUS = "status";
        public static final String COL_PRODUCT_UID = "product_id";
    }

    public static class Items {
        public static final String COL_TABLE_NAME = "items";
        public static final String COL_PRODUCT_ID = "product_id";
        public static final String COL_GROUP_ID = "group_id";
        public static final String COL_BRAND_ID = "brand_id";
        public static final String COL_ITEM_UID = "item_id";
        public static final String COL_SKU_CODE = "sku_code";
        public static final String COL_ITEM_NAME = "item_name";
        public static final String COL_IMAGE_URL = "image_url";
        public static final String COL_BOX_SIZE = "box_size";
        public static final String COL_CTN_SIZE = "ctn_size";
        public static final String COL_FREQUENT = "frequent";
        public static final String COL_MUST_SELL = "must_sell";
        public static final String COL_ABOVE_TARGET = "above_target";
        public static final String COL_BELOW_TARGET = "below_target";
    }


    public static class ItemGroup {
        public static final String COL_TABLE_NAME = "item_group";
        public static final String COL_BRAND_ID = "brand_id";
        public static final String COL_GROUP_CODE = "group_code";
        public static final String COL_GROUP_NAME = "group_name";
        public static final String COL_GROUP_ID = "group_id";
    }

    public static class ProductCategory {
        public static final String COL_TABLE_NAME = "product_category";
        public static final String COL_CATEGORY_UID = "category_id";
        public static final String COL_CATEGORY_NAME = "category_name";
    }


}
