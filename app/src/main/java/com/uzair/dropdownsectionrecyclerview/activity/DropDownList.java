package com.uzair.dropdownsectionrecyclerview.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyProductAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyBrandAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyCategoryAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyGroupAdapter;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.ItemGroup;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.Product;
import com.uzair.dropdownsectionrecyclerview.model.ProductBrand;
import com.uzair.dropdownsectionrecyclerview.model.ProductCategory;
import com.uzair.dropdownsectionrecyclerview.model.ProductItem;
import com.uzair.dropdownsectionrecyclerview.utils.Contracts;
import com.uzair.dropdownsectionrecyclerview.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableRecyclerView;

public class DropDownList extends AppCompatActivity {

    //views
    ExpandableRecyclerView recyclerView;
    SearchView searchView;
    //db
    DatabaseReference dbRef;
    SqliteClient client;
    //list
    List<Items> itemList;
    List<ProductItem> productCompleteList;
    List<Product> product;
    List<ProductBrand> productBrandList, pBrandCompleteList;
    List<ItemGroup> itemGroupsList, pItemGroupCompleteList;
    List<ProductCategory> categoryList, pCategoryCompleteList;
    //adapters
    StickyProductAdapter productAdapter;
    StickyBrandAdapter brandAdapter;
    StickyGroupAdapter groupAdapter;
    StickyCategoryAdapter categoryAdapter;
    public static int productPosition = 0, brandPosition, groupPosition, categoryPosition;
    //bottom sheet
    LinearLayout bottomSheetLayout;
    BottomSheetBehavior bottomSheetBehavior;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_down_list);

        initViews();
        setBottomSheetLayout();
        loadAdapter();

        // click on more icon to open bottom sheet
        findViewById(R.id.moreIcon)
                .setOnClickListener(v -> {
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });


        /// search view
        setUpSearch();

    }

    /// ******* LOAD ADAPTER ACCORDING TO LAST SELECTION ****** ///
    private void loadAdapter() {
        switch (SharedPref.getType()) {
            case "Product": {
                setHeaderTitle("Product");
                setUpProductAdapter();
                break;
            }
            case "Category": {
                setHeaderTitle("Category");
                setUpCategoryAdapter();
                break;
            }
            case "Brand": {
                setHeaderTitle("Brand");
                setUpBrandAdapter();
                break;
            }
            case "Group": {
                setHeaderTitle("Group");
                setUpGroupAdapter();
                break;
            }
        }
    }

    ///// ******** POSITION SETTER AND GETTER ********** //////
    public static void setBrandPosition(int bPosition) {
        brandPosition = bPosition;
    }

    public static int getProductPosition() {
        return productPosition;
    }

    public static int getCategoryPosition() {
        return categoryPosition;
    }

    public static int getBrandPosition() {
        return brandPosition;
    }

    public static int getGroupPosition() {
        return groupPosition;
    }

    public static void setProductPosition(int pPosition) {
        productPosition = pPosition;
    }

    public static void setCategoryPosition(int cPosition) {
        categoryPosition = cPosition;
    }

    public static void setGroupPosition(int gPosition) {
        groupPosition = gPosition;
    }

    //// ************ SEARCH VIEW **********////
    private void setUpSearch() {
        searchView = findViewById(R.id.searchView);
        //search icon click
        searchView.setOnSearchClickListener(v -> title.setVisibility(View.GONE));
        // close search view click
        searchView.setOnCloseListener(() -> {
            title.setVisibility(View.VISIBLE);
            return false;
        });

        // query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchQuery) {
                switch (SharedPref.getType()) {
                    case "Product": {
                        productAdapter.getFilter().filter(searchQuery);
                        break;
                    }

                    case "Brand": {
                        brandAdapter.getFilter().filter(searchQuery);
                        break;
                    }

                    case "Group": {
                        groupAdapter.getFilter().filter(searchQuery);
                        break;
                    }

                    case "Category": {
                        categoryAdapter.getFilter().filter(searchQuery);
                        break;
                    }


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {

                switch (SharedPref.getType()) {
                    case "Product": {
                        productAdapter.getFilter().filter(searchText);
                        break;
                    }

                    case "Brand": {
                        brandAdapter.getFilter().filter(searchText);
                        break;
                    }

                    case "Group": {
                        groupAdapter.getFilter().filter(searchText);
                        break;
                    }

                    case "Category": {
                        categoryAdapter.getFilter().filter(searchText);
                        break;
                    }


                }
                return false;
            }
        });

    }

    private void initViews() {
        /// pref
        SharedPref.init(this);
        // views ,db and get all data
        title = findViewById(R.id.title);
        dbRef = FirebaseDatabase.getInstance().getReference();
        client = new SqliteClient(this);
        getAllItems();
        getAllProducts();
        getProductsBrand();
        getAllItemsGroup();
        getAllCategory();

        // init list
        productCompleteList = new ArrayList<>();
        pBrandCompleteList = new ArrayList<>();
        pItemGroupCompleteList = new ArrayList<>();
        pCategoryCompleteList = new ArrayList<>();
        // get product, brand , group and item list from sqlite db
        itemList = client.getAllItems(Contracts.Items.COL_PRODUCT_ID, Contracts.Items.COL_BRAND_ID);
        product = client.getProductList();
        productBrandList = client.getBrandList();
        itemGroupsList = client.getGroupsList();
        categoryList = client.getCategoryList();

        // add items to product list according to product id
        for (int i = 0; i < product.size(); i++) {
            // first get item list by product it then pass it to the product item class
            List<Items> list = client.getItemsListById(Contracts.Items.COL_PRODUCT_ID, product.get(i).getUid());
            ProductItem productItem = new ProductItem(
                    product.get(i).getProductName(),
                    product.get(i).getStatus(),
                    String.valueOf(product.get(i).getUid()),
                    String.valueOf(product.get(i).getCategoryId()),
                    String.valueOf(product.get(i).getCompanyId()),
                    list
            );

            productCompleteList.add(productItem);
        }

        // add items to brand list according brand id
        for (int i = 0; i < productBrandList.size(); i++) {
            List<Items> list = client.getItemsListById(Contracts.Items.COL_BRAND_ID, Integer.parseInt(productBrandList.get(i).getProductBrandId()));

            ProductBrand productBrand = new ProductBrand(
                    productBrandList.get(i).getName(),
                    productBrandList.get(i).getProductId(),
                    productBrandList.get(i).getProductBrandId(),
                    list
            );

            pBrandCompleteList.add(productBrand);


        }

        // Add items to group list according group id
        for (int i = 0; i < itemGroupsList.size(); i++) {

            List<Items> list = client.getItemsListById(Contracts.Items.COL_GROUP_ID, Integer.parseInt(itemGroupsList.get(i).getItemGroupId()));

            ItemGroup itemGroup = new ItemGroup(
                    itemGroupsList.get(i).getBrandId(),
                    itemGroupsList.get(i).getGroupCode(),
                    itemGroupsList.get(i).getName(),
                    itemGroupsList.get(i).getItemGroupId(),
                    list);

            pItemGroupCompleteList.add(itemGroup);

        }

        // add items to category list
        for (int i = 0; i < categoryList.size(); i++) {
            List<String> productId = client.getProductIdByCategoryId(categoryList.get(i).getId());
            List<Items> itemList = new ArrayList<>();
            ProductCategory productCategory = null;
            for (String id : productId) {
                productCategory = new ProductCategory();
                List<Items> list = client.getItemsListById(Contracts.Items.COL_PRODUCT_ID, Integer.parseInt(id));
                itemList.addAll(list);
                productCategory.setItemsList(itemList);
            }

            assert productCategory != null;
            productCategory.setId(categoryList.get(i).getId());
            productCategory.setName(categoryList.get(i).getName() + itemList.size());
            pCategoryCompleteList.add(productCategory);

        }

        // recycler view
        recyclerView = findViewById(R.id.recyclerViewStickyHeader);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    //// *********** BOTTOM SHEET SETUP ************** /////
    private void setBottomSheetLayout()
    {
        bottomSheetLayout = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Button product, category, brand, group;
        product = findViewById(R.id.btnProduct);
        category = findViewById(R.id.btnCategory);
        brand = findViewById(R.id.btnBrand);
        group = findViewById(R.id.btnGroup);

        product.setOnClickListener(v -> {
            setHeaderTitle("Product");
            setUpProductAdapter();
        });

        category.setOnClickListener(v -> {
            setHeaderTitle("Category");
            setUpCategoryAdapter();
        });

        brand.setOnClickListener(v -> {
            setHeaderTitle("Brand");
            setUpBrandAdapter();
        });

        group.setOnClickListener(v -> {
            setHeaderTitle("Group");
            setUpGroupAdapter();
        });


    }

    // title
    private void setHeaderTitle(String header) {
        title.setText(header);
    }

    /// ***************** SETUP ADAPTER ************* ///////
    private void setUpCategoryAdapter() {
        categoryAdapter = new StickyCategoryAdapter(pCategoryCompleteList, this);
        categoryAdapter.collapseAllGroup();
        recyclerView.setAdapter(categoryAdapter);
        SharedPref.storeType("Category");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private void setUpGroupAdapter() {
        groupAdapter = new StickyGroupAdapter(pItemGroupCompleteList, this);
        groupAdapter.collapseAllGroup();
        recyclerView.setAdapter(groupAdapter);
        SharedPref.storeType("Group");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setUpProductAdapter() {

        productAdapter = new StickyProductAdapter(productCompleteList, DropDownList.this);
        productAdapter.collapseAllGroup();
        recyclerView.setItemViewCacheSize(itemList.size());
        recyclerView.setAdapter(productAdapter);
        SharedPref.storeType("Product");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setUpBrandAdapter() {
        brandAdapter = new StickyBrandAdapter(pBrandCompleteList, this);
        brandAdapter.collapseAllGroup();
        recyclerView.setAdapter(brandAdapter);
        SharedPref.storeType("Brand");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /// *************  GET PRODUCTS , ITEMS, CATEGORY , GROUP , BRAND DATA FROM FIREBASE *********** /////////
    private void getAllProducts() {
        dbRef.child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        /// insert product in sqlite database from firebase
                        for (DataSnapshot query : snapshot.getChildren()) {
                            Product product = new Product();
                            product.setProductName(String.valueOf(query.child("name").getValue()));
                            product.setStatus(String.valueOf(query.child("status").getValue()));
                            product.setUid(Integer.parseInt(String.valueOf(query.child("uid").getValue())));
                            product.setCompanyId(Integer.parseInt(String.valueOf(query.child("companyid").getValue())));
                            product.setCategoryId(Integer.parseInt(String.valueOf(query.child("category_id").getValue())));

                            client.insertProduct(product);


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllItems() {
        dbRef.child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /// insert item in sqlite database from firebase

                        for (DataSnapshot query : snapshot.getChildren()) {
                            Items items = new Items();
                            items.setBranId(String.valueOf(query.child("bran_id").getValue()));
                            items.setGroupId(String.valueOf(query.child("group_id").getValue()));
                            items.setItemName(String.valueOf(query.child("name").getValue()));
                            items.setProductId(Integer.parseInt(String.valueOf(query.child("productid").getValue())));
                            items.setSkuCode(String.valueOf(query.child("sku_code").getValue()));
                            items.setImageUrl(String.valueOf(query.child("image").getValue()));
                            items.setCtnSize(Integer.parseInt(String.valueOf(query.child("ctn_size").getValue())));
                            items.setBoxSize(Integer.parseInt(String.valueOf(query.child("box_size").getValue())));
                            items.setUid(Integer.parseInt(String.valueOf(query.child("uid").getValue())));

                            client.insertItems(items);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllCategory() {
        dbRef.child("ProductCategory")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        /// insert product category in sqlite database from firebase
                        for (DataSnapshot query : snapshot.getChildren()) {
                            ProductCategory category = new ProductCategory();
                            category.setName(String.valueOf(query.child("name").getValue()));
                            category.setId(String.valueOf(query.child("uid").getValue()));
                            client.insertProductCategory(category);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getProductsBrand() {
        dbRef.child("ProductBrand")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /// insert product brand in sqlite database from firebase

                        for (DataSnapshot query : snapshot.getChildren()) {
                            ProductBrand productBrand = new ProductBrand();
                            productBrand.setName(String.valueOf(query.child("name").getValue()));
                            productBrand.setProductBrandId(String.valueOf(query.child("uid").getValue()));
                            productBrand.setProductId(String.valueOf(query.child("product_id").getValue()));

                            client.insertProductBrand(productBrand);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllItemsGroup() {
        dbRef.child("ItemGroup")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /// insert item group in sqlite database from firebase

                        for (DataSnapshot query : snapshot.getChildren()) {
                            ItemGroup itemGroup = new ItemGroup();
                            itemGroup.setName(String.valueOf(query.child("name").getValue()));
                            itemGroup.setItemGroupId(String.valueOf(query.child("uid").getValue()));
                            itemGroup.setBrandId(String.valueOf(query.child("brand_id").getValue()));
                            itemGroup.setGroupCode(String.valueOf(query.child("group_code").getValue()));

                            client.insertItemGroup(itemGroup);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //// ***************** CLICK ON UP AND DOWN BUTTON *************** /////
    public void btnDown(View view) {

        switch (SharedPref.getType()) {
            case "Product": {
                if (productPosition < productAdapter.getGroupCount()) {
                    productAdapter.expandGroup(getProductPosition(), true);
                    setProductPosition(productPosition++);
                    Log.d("down", "onGroupExpandChange: " + getProductPosition());

                } else {
                    Toast.makeText(DropDownList.this, "All sections are expanded", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "Brand": {
                if (brandPosition < brandAdapter.getGroupCount()) {
                    brandAdapter.expandGroup(getBrandPosition(), true);
                    setBrandPosition(brandPosition++);
                } else {
                    Toast.makeText(DropDownList.this, "All sections are expanded", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case "Group": {
                if (groupPosition < groupAdapter.getGroupCount()) {
                    groupAdapter.expandGroup(getGroupPosition(), true);
                    setGroupPosition(groupPosition++);
                } else {
                    Toast.makeText(DropDownList.this, "All sections are expanded", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case "Category": {
                if (categoryPosition < categoryAdapter.getGroupCount()) {
                    categoryAdapter.expandGroup(getCategoryPosition(), true);
                    setCategoryPosition(categoryPosition++);
                } else {
                    Toast.makeText(DropDownList.this, "All sections are expanded", Toast.LENGTH_SHORT).show();
                }
                break;
            }


        }

    }

    public void btnUp(View view) {

        switch (SharedPref.getType()) {
            case "Product": {
                if (productPosition > 0) {
                    productPosition--;
                    productAdapter.collapseGroup(productPosition, true);
                    Log.d("up", "onGroupExpandChange: " + getProductPosition());
                } else {
                    Toast.makeText(DropDownList.this, "All sections are collapsed", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case "Brand": {
                if (brandPosition > 0) {
                    brandPosition--;
                    brandAdapter.collapseGroup(brandPosition, true);
                } else {
                    Toast.makeText(DropDownList.this, "All sections are collapsed", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case "Group": {
                if (groupPosition > 0) {
                    groupPosition--;
                    groupAdapter.collapseGroup(groupPosition, true);
                } else {
                    Toast.makeText(DropDownList.this, "All sections are collapsed", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case "Category": {
                if (categoryPosition > 0) {
                    categoryPosition--;
                    categoryAdapter.collapseGroup(categoryPosition, true);
                } else {
                    Toast.makeText(DropDownList.this, "All sections are collapsed", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }


    }
}