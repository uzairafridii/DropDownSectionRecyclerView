package com.uzair.dropdownsectionrecyclerview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


import com.uzair.dropdownsectionrecyclerview.model.ItemGroup;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.Product;
import com.uzair.dropdownsectionrecyclerview.model.ProductBrand;
import com.uzair.dropdownsectionrecyclerview.model.ProductCategory;
import com.uzair.dropdownsectionrecyclerview.utils.Contracts;

import java.util.ArrayList;
import java.util.List;

public class SqliteClient extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "shop.db";
    public static final int DATABASE_VERSION = 35;
    SQLiteDatabase sqliteDb;


    public SqliteClient(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.sqliteDb = db;
        // create table product
        sqliteDb.execSQL("create table " + Contracts.Products.COL_TABLE_NAME +
                " ( " + Contracts.Products.COL_PRODUCT_UID + " integer primary key UNIQUE, "
                + Contracts.Products.COL_PRODUCT_NAME + " text, " +
                Contracts.Products.COL_COMPANY_ID + " integer, " +
                Contracts.Products.COL_CATEGORY_ID + " integer, " +
                Contracts.Products.COL_STATUS + " text ); ");


        ///create table items
        sqliteDb.execSQL(" CREATE TABLE " + Contracts.Items.COL_TABLE_NAME +
                " ( " + Contracts.Items.COL_ITEM_UID + " integer primary key UNIQUE, "
                + Contracts.Items.COL_ITEM_NAME + " text, " +
                Contracts.Items.COL_SKU_CODE + " text, " +
                Contracts.Items.COL_BOX_SIZE + " integer, " +
                Contracts.Items.COL_CTN_SIZE + " integer, " +
                Contracts.Items.COL_IMAGE_URL + " text, " +
                Contracts.Items.COL_PRODUCT_ID + " integer, " +
                Contracts.Items.COL_BRAND_ID + " text, " +
                Contracts.Items.COL_GROUP_ID + " text, " +
                " FOREIGN KEY (" + Contracts.Items.COL_PRODUCT_ID + " ) REFERENCES " +
                Contracts.Products.COL_TABLE_NAME + " ( " + Contracts.Products.COL_PRODUCT_UID + " ) ); ");


        /// create table Product Brand
        sqliteDb.execSQL("create table " + Contracts.ProductsBrand.COL_TABLE_NAME +
                " ( " + Contracts.ProductsBrand.COL_BRAND_ID + " integer primary key UNIQUE, "
                + Contracts.ProductsBrand.COL_BRAND_NAME + " text, " +
                Contracts.ProductsBrand.COL_PRODUCT_ID + " integer ); ");

        /// create table for ItemGroup
        sqliteDb.execSQL("create table " + Contracts.ItemGroup.COL_TABLE_NAME +
                " ( " + Contracts.ItemGroup.COL_GROUP_ID + " integer primary key UNIQUE, "
                + Contracts.ItemGroup.COL_GROUP_NAME + " text, "
                + Contracts.ItemGroup.COL_BRAND_ID + " text, " +
                Contracts.ItemGroup.COL_GROUP_CODE + " integer ); ");


        /// create table for Product Category
        sqliteDb.execSQL("create table " + Contracts.ProductCategory.COL_TABLE_NAME +
                " ( " + Contracts.ProductCategory.COL_CATEGORY_UID + " integer primary key UNIQUE, "
                + Contracts.ProductCategory.COL_CATEGORY_NAME + " text ); ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /// upgrade tables here
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.ProductsBrand.COL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.Products.COL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.Items.COL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.ItemGroup.COL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.ProductCategory.COL_TABLE_NAME);

        onCreate(db);
    }

    //////*********************** PRODUCT TABLE************/////////////////
    public void insertProduct(Product product) {
        sqliteDb = this.getWritableDatabase();
        ContentValues productCv = new ContentValues();
        productCv.put(Contracts.Products.COL_PRODUCT_NAME, product.getProductName());
        productCv.put(Contracts.Products.COL_PRODUCT_UID, product.getUid());
        productCv.put(Contracts.Products.COL_CATEGORY_ID, product.getCategoryId());
        productCv.put(Contracts.Products.COL_COMPANY_ID, product.getCompanyId());
        productCv.put(Contracts.Products.COL_STATUS, product.getStatus());


        sqliteDb.insert(Contracts.Products.COL_TABLE_NAME, null, productCv);

    }

    public String getProductNameById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String productName = "";
        Cursor productCursor = db.rawQuery("select * from " + Contracts.Products.COL_TABLE_NAME +
                " where " + Contracts.Products.COL_PRODUCT_UID + " =? ORDER BY "
                + Contracts.Products.COL_PRODUCT_UID + " ASC ", new String[]{String.valueOf(productId)});

        if (productCursor.moveToFirst()) {

            do {
                productName = productCursor.getString(productCursor.getColumnIndexOrThrow(Contracts.Products.COL_PRODUCT_NAME));
            }
            while (productCursor.moveToNext());

        }
        productCursor.close();
        return productName;


    }

    public List<String> getProductNameList() {
        List<String> productNameList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + Contracts.Products.COL_TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                productNameList.add(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Products.COL_PRODUCT_NAME)));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return productNameList;
    }


    //////*********************** ITEMS TABLE************/////////////////
    public void insertItems(Items item) {
        sqliteDb = this.getWritableDatabase();
        ContentValues itemContentValue = new ContentValues();
        itemContentValue.put(Contracts.Items.COL_ITEM_UID, item.getUid());
        itemContentValue.put(Contracts.Items.COL_ITEM_NAME, item.getItemName());
        itemContentValue.put(Contracts.Items.COL_BOX_SIZE, item.getBoxSize());
        itemContentValue.put(Contracts.Items.COL_CTN_SIZE, item.getCtnSize());
        itemContentValue.put(Contracts.Items.COL_IMAGE_URL, item.getImageUrl());
        itemContentValue.put(Contracts.Items.COL_PRODUCT_ID, item.getProductId());
        itemContentValue.put(Contracts.Items.COL_GROUP_ID, item.getGroupId());
        itemContentValue.put(Contracts.Items.COL_BRAND_ID, item.getBranId());
        itemContentValue.put(Contracts.Items.COL_SKU_CODE, item.getSkuCode());
        sqliteDb.insert(Contracts.Items.COL_TABLE_NAME, null, itemContentValue);
    }

    /// get items list
    public List<Items> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Items> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * from " + Contracts.Items.COL_TABLE_NAME +
                " ORDER BY " + Contracts.Items.COL_PRODUCT_ID + " ASC ", null);

        if (cursor.moveToFirst()) {
            do {
                Items items = new Items();
                items.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Items.COL_ITEM_NAME)));
                items.setGroupId(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Items.COL_GROUP_ID)));
                items.setBranId(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Items.COL_BRAND_ID)));
                items.setUid(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Items.COL_ITEM_UID)));
                items.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Items.COL_PRODUCT_ID)));
                items.setBoxSize(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Items.COL_BOX_SIZE)));
                items.setCtnSize(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Items.COL_CTN_SIZE)));
                items.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Items.COL_IMAGE_URL)));
                items.setSkuCode(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Items.COL_SKU_CODE)));

                list.add(items);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }


    //////*********************** PRODUCT CATEGORY GROUP TABLE************/////////////////
    /// insert items in product category table
    public void insertProductCategory(ProductCategory category) {
        sqliteDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.ProductCategory.COL_CATEGORY_NAME, category.getName());
        contentValues.put(Contracts.ProductCategory.COL_CATEGORY_UID, category.getId());

        sqliteDb.insert(Contracts.ProductCategory.COL_TABLE_NAME, null, contentValues);

    }

    /// get items by product category id
    public String getProductCategoryId(int productId) {
        String productCategoryId = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor itemCursor = db.rawQuery("Select * from " + Contracts.ProductCategory.COL_TABLE_NAME + " pc INNER JOIN " +
                Contracts.Products.COL_TABLE_NAME + " pd on  pc.category_id = pd.category_id " +
                " where " + Contracts.Products.COL_PRODUCT_UID + "=?", new String[]{String.valueOf(productId)});

        if (itemCursor.moveToFirst()) {
            do {
                productCategoryId = String.valueOf(itemCursor.getInt(itemCursor.getColumnIndexOrThrow(Contracts.ProductCategory.COL_CATEGORY_UID)));
            } while (itemCursor.moveToNext());
        }

        itemCursor.close();
        return productCategoryId;

    }

    /// get items by product category name
    public String getProductCategoryName(int productId) {
        String productCategoryName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor itemCursor = db.rawQuery("Select * from " + Contracts.ProductCategory.COL_TABLE_NAME + " pc INNER JOIN " +
                Contracts.Products.COL_TABLE_NAME + " pd on  pc.category_id = pd.category_id " +
                " where " + Contracts.Products.COL_PRODUCT_UID + " =? ORDER BY "
                + Contracts.ProductCategory.COL_CATEGORY_UID + " ASC ", new String[]{String.valueOf(productId)});

        if (itemCursor.moveToFirst()) {
            do {
                productCategoryName = itemCursor.getString(itemCursor.getColumnIndexOrThrow(Contracts.ProductCategory.COL_CATEGORY_NAME));
            } while (itemCursor.moveToNext());
        }

        itemCursor.close();
        return productCategoryName;

    }

    public List<String> getProductCategoryNameList() {
        List<String> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + Contracts.ProductCategory.COL_TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                categoryList.add(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.ProductCategory.COL_CATEGORY_NAME)));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return categoryList;
    }


    //////*********************** Product Brand GROUP TABLE************/////////////////
    ///insert data into PRODUCT BRAND table
    public void insertProductBrand(ProductBrand productBrand) {
        sqliteDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.ProductsBrand.COL_BRAND_NAME, productBrand.getName());
        contentValues.put(Contracts.ProductsBrand.COL_BRAND_ID, productBrand.getProductBrandId());
        contentValues.put(Contracts.ProductsBrand.COL_PRODUCT_ID, productBrand.getProductId());

        sqliteDb.insert(Contracts.ProductsBrand.COL_TABLE_NAME, null, contentValues);

    }

    /// get  productBrand by id
    public String getProductBrand(String brandId) {
        String brandName = "";
        sqliteDb = this.getReadableDatabase();
        Cursor cursor = sqliteDb.rawQuery(" select * from " + Contracts.ProductsBrand.COL_TABLE_NAME +
                " where " + Contracts.ProductsBrand.COL_BRAND_ID +" =? ORDER BY "+
                Contracts.ProductsBrand.COL_PRODUCT_ID, new String[]{String.valueOf(brandId)});

        if (cursor.moveToFirst()) {
            do {
                brandName = cursor.getString(cursor.getColumnIndexOrThrow(Contracts.ProductsBrand.COL_BRAND_NAME));

            } while (cursor.moveToNext());
        }

        Log.d("brandName", "getProductBrand: " + brandName);
        cursor.close();
        return brandName;

    }

    public List<String> getProductBrandList() {
        List<String> productBrandList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + Contracts.ProductsBrand.COL_TABLE_NAME
                +  " ORDER BY "
                + Contracts.ProductsBrand.COL_BRAND_ID + " ASC ", null);

        if (cursor.moveToFirst()) {
            do {
                productBrandList.add(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.ProductsBrand.COL_BRAND_NAME)));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return productBrandList;
    }


    //////*********************** ITEM GROUP TABLE************/////////////////
    /// insert data into ITEM GROUP table
    public void insertItemGroup(ItemGroup itemGroup) {
        sqliteDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.ItemGroup.COL_GROUP_ID, itemGroup.getItemGroupId());
        contentValues.put(Contracts.ItemGroup.COL_GROUP_NAME, itemGroup.getName());
        contentValues.put(Contracts.ItemGroup.COL_BRAND_ID, itemGroup.getBrandId());
        contentValues.put(Contracts.ItemGroup.COL_GROUP_CODE, itemGroup.getGroupCode());

        sqliteDb.insert(Contracts.ItemGroup.COL_TABLE_NAME, null, contentValues);
    }

    /// get group item by id
    public String getItemGroupById(String itemGroupID) {
        String name = "Group";
        sqliteDb = this.getReadableDatabase();
        Cursor cursor = sqliteDb.rawQuery(" select * from " + Contracts.ItemGroup.COL_TABLE_NAME +
                " where " + Contracts.ItemGroup.COL_GROUP_ID + " =?  ORDER BY "
                + Contracts.ItemGroup.COL_BRAND_ID + " ASC ", new String[]{String.valueOf(itemGroupID)});

        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(Contracts.ItemGroup.COL_GROUP_NAME));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return name;


    }

    public List<String> getItemGroupList() {
        List<String> itemGroupList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + Contracts.ItemGroup.COL_TABLE_NAME
                +" ORDER BY "+ Contracts.ItemGroup.COL_GROUP_ID + " ASC ", null);

//        Cursor cursor = db.rawQuery("Select * from " + Contracts.ItemGroup.COL_TABLE_NAME + " ig INNER JOIN " +
//                Contracts.Items.COL_TABLE_NAME + " pd on  ig.brand_id = pd.brand_id  ORDER BY ig.group_id ", null);
//

        if (cursor.moveToFirst()) {
            do {
                itemGroupList.add(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.ItemGroup.COL_GROUP_NAME)));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return itemGroupList;
    }


}