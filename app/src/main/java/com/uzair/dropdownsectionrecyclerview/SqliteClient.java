package com.uzair.dropdownsectionrecyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

public class SqliteClient extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "shop.db";
    public static final int DATABASE_VERSION = 2;
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
                " FOREIGN KEY (" + Contracts.Items.COL_PRODUCT_ID + " ) REFERENCES " +
                Contracts.Products.COL_TABLE_NAME + " ( " + Contracts.Products.COL_PRODUCT_UID + " ) ); ");


        // insert data in product table on start
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /// upgrade tables here
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.Products.COL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.Items.COL_TABLE_NAME);
        onCreate(db);
    }

    ///insert
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

    /// get product list here
    public List<Product> getProductList() {
        List<Product> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Contracts.Products.COL_TABLE_NAME, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Product productData = new Product();
                productData.setUid(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Products.COL_PRODUCT_UID)));
                productData.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Products.COL_PRODUCT_NAME)));
                productData.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Products.COL_CATEGORY_ID)));
                productData.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Products.COL_STATUS)));
                productData.setCompanyId(cursor.getInt(cursor.getColumnIndexOrThrow(Contracts.Products.COL_COMPANY_ID)));
                // Adding product to list
                list.add(productData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;

    }

    public String getProductNameById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String productName = "";
        Cursor productCursor = db.rawQuery("select * from " + Contracts.Products.COL_TABLE_NAME +
                " where " + Contracts.Products.COL_PRODUCT_UID + "=?", new String[]{String.valueOf(productId)});

        if (productCursor.moveToFirst()) {

            do {
                productName = productCursor.getString(productCursor.getColumnIndexOrThrow(Contracts.Products.COL_PRODUCT_NAME));
            }
            while (productCursor.moveToNext());

        }
        productCursor.close();
        return productName;


    }



    /// insert data into items
    public void insertItems(Items item) {
        sqliteDb = this.getWritableDatabase();
        ContentValues itemContentValue = new ContentValues();
        itemContentValue.put(Contracts.Items.COL_ITEM_UID, item.getUid());
        itemContentValue.put(Contracts.Items.COL_ITEM_NAME, item.getItemName());
        itemContentValue.put(Contracts.Items.COL_BOX_SIZE, item.getBoxSize());
        itemContentValue.put(Contracts.Items.COL_CTN_SIZE, item.getCtnSize());
        itemContentValue.put(Contracts.Items.COL_IMAGE_URL, item.getImageUrl());
        itemContentValue.put(Contracts.Items.COL_PRODUCT_ID, item.getProductId());
        itemContentValue.put(Contracts.Items.COL_SKU_CODE, item.getSkuCode());
        sqliteDb.insert(Contracts.Items.COL_TABLE_NAME, null, itemContentValue);
    }

    /// get items list
    public List<Items> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Items> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * from " + Contracts.Items.COL_TABLE_NAME + " order by "+Contracts.Items.COL_PRODUCT_ID, null);

        if (cursor.moveToFirst()) {
            do {
                Items items = new Items();
                items.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(Contracts.Items.COL_ITEM_NAME)));
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


}