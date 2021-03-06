package com.uzair.dropdownsectionrecyclerview.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
import com.uzair.dropdownsectionrecyclerview.adapter.StickyFrequentAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyProductAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyBrandAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyCategoryAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.StickyGroupAdapter;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.Common;
import com.uzair.dropdownsectionrecyclerview.model.ItemGroup;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.Product;
import com.uzair.dropdownsectionrecyclerview.model.ProductBrand;
import com.uzair.dropdownsectionrecyclerview.model.ProductCategory;
import com.uzair.dropdownsectionrecyclerview.model.ProductItem;
import com.uzair.dropdownsectionrecyclerview.utils.Contracts;
import com.uzair.dropdownsectionrecyclerview.utils.SharedPref;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pokercc.android.expandablerecyclerview.ExpandableRecyclerView;

public class DropDownList extends AppCompatActivity {

    //views
    static ExpandableRecyclerView recyclerView;
    static LinearLayoutManager layoutManager;
    SearchView searchView;
    //db
    DatabaseReference dbRef;
    SqliteClient client;
    //list
    List<Items> itemList;
    List<Common> frequentList, mustSellList, aboveTargetList, belowTargetList;
    List<ProductItem> productCompleteList;
    List<Product> product;
    List<ProductBrand> productBrandList, pBrandCompleteList;
    List<ItemGroup> itemGroupsList, pItemGroupCompleteList;
    List<ProductCategory> categoryList, pCategoryCompleteList;
    //adapters
    StickyFrequentAdapter frequentAdapter;
    static StickyProductAdapter productAdapter;
    StickyBrandAdapter brandAdapter;
    StickyGroupAdapter groupAdapter;
    StickyCategoryAdapter categoryAdapter;
    public static int productPosition = -1, brandPosition = -1, groupPosition = -1, categoryPosition = -1;
    //bottom sheet
    LinearLayout bottomSheetLayout;
    BottomSheetBehavior bottomSheetBehavior;
    TextView title;
    static Button btnDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_down_list);

        initViews();
        setBottomSheetLayout();
        loadAdapter();

        // click on more icon to open/close bottom sheet
        findViewById(R.id.moreIcon)
                .setOnClickListener(v -> {
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });


        // search view
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
            case "Frequent": {
                setHeaderTitle("Frequent");
                setUpFrequentAdapter(frequentList, "Frequent");
                break;
            }
        }
    }

    ///// ******** POSITION SETTER AND GETTER ********** //////
    public static void setBrandPosition(int bPosition) {
        brandPosition = bPosition;
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


                    case "Frequent": {
                        frequentAdapter.getFilter().filter(searchQuery);
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

                    case "Frequent":
                    case "Below Target":
                    case "Must Sell":
                    case "Above Target": {
                        frequentAdapter.getFilter().filter(searchText);
                        break;
                    }


                }
                return false;
            }
        });

    }

    private void initViews() {
        btnDown = findViewById(R.id.btnDown);
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
        frequentList = new ArrayList<>();
        mustSellList = new ArrayList<>();
        belowTargetList = new ArrayList<>();
        aboveTargetList = new ArrayList<>();

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


        // add items to above target list
        List<Items> aboveTargetItemList = client.getItemsListById(Contracts.Items.COL_ABOVE_TARGET, 102);
        aboveTargetList.add(new Common("Above Target", aboveTargetItemList));
        // add items to below target list
        List<Items> belowTargetItemsList = client.getItemsListById(Contracts.Items.COL_BELOW_TARGET, 103);
        belowTargetList.add(new Common("Below Target", belowTargetItemsList));
        // add items to must sell list
        List<Items> mustSellItemsList = client.getItemsListById(Contracts.Items.COL_MUST_SELL, 101);
        mustSellList.add(new Common("Must Sell", mustSellItemsList));
        // add items to frequent list using frequent id
        List<Items> freqItemList = client.getItemsListById(Contracts.Items.COL_FREQUENT, 100);
        frequentList.add(new Common("Frequent", freqItemList));

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
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    //// *********** BOTTOM SHEET SETUP ************** /////
    private void setBottomSheetLayout() {
        bottomSheetLayout = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Button product, category, brand, group, frequent, mustSell, aboveTarget, belowTarget;
        product = findViewById(R.id.btnProduct);
        category = findViewById(R.id.btnCategory);
        brand = findViewById(R.id.btnBrand);
        group = findViewById(R.id.btnGroup);
        frequent = findViewById(R.id.btnFrequent);
        mustSell = findViewById(R.id.btnMustSell);
        aboveTarget = findViewById(R.id.btnAboveTarget);
        belowTarget = findViewById(R.id.btnBelowTarget);

        product.setOnClickListener(v -> {
            setHeaderTitle("Product");
            resetPosition();
            setUpProductAdapter();
        });

        category.setOnClickListener(v -> {
            setHeaderTitle("Category");
            resetPosition();
            setUpCategoryAdapter();
        });

        brand.setOnClickListener(v -> {
            setHeaderTitle("Brand");
            resetPosition();
            setUpBrandAdapter();
        });

        group.setOnClickListener(v -> {
            setHeaderTitle("Group");
            resetPosition();
            setUpGroupAdapter();
        });

        frequent.setOnClickListener(v -> {
            setHeaderTitle("Frequent");
            setUpFrequentAdapter(frequentList, "Frequent");
        });

        mustSell.setOnClickListener(v -> {
            setHeaderTitle("Must Sell");
            setUpFrequentAdapter(mustSellList, "Must Sell");
        });

        aboveTarget.setOnClickListener(v -> {
            setHeaderTitle("Above Target");
            setUpFrequentAdapter(aboveTargetList, "Above Target");
        });

        belowTarget.setOnClickListener(v -> {
            setHeaderTitle("Below Target");
            setUpFrequentAdapter(belowTargetList, "Below Target");
        });


    }

    // set all position to zero
    private void resetPosition() {
        setProductPosition(-1);
        setBrandPosition(-1);
        setGroupPosition(-1);
        setCategoryPosition(-1);
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

    private void setUpFrequentAdapter(List<Common> commonList, String storeType) {
        frequentAdapter = new StickyFrequentAdapter(commonList, this);
        frequentAdapter.collapseAllGroup();
        recyclerView.setAdapter(frequentAdapter);
        SharedPref.storeType(storeType);
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
                            String frequent = String.valueOf(query.child("frequent").getValue());
                            if (frequent != null && !frequent.isEmpty()) {
                                items.setFrequent(frequent);
                            }
                            if (String.valueOf(query.child("must_sell").getValue()) != null) {
                                items.setMust_sell(String.valueOf(query.child("must_sell").getValue()));
                            }
                            if (String.valueOf(query.child("above_target").getValue()) != null) {
                                items.setAbove_target((String.valueOf(query.child("above_target").getValue())));
                            }
                            if (String.valueOf(query.child("below_target").getValue()) != null) {
                                items.setBelow_target(String.valueOf(query.child("below_target").getValue()));
                            }

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
                    productAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                productPosition++;
                                if (productPosition < productAdapter.getGroupCount()) {
                                    layoutManager.scrollToPositionWithOffset(productPosition, 0);
                                    productAdapter.expandGroup(productPosition, true);
                                }


                            }, 10);

                } else {
                    productPosition = -1;
                    productAdapter.collapseAllGroup();
                }
                break;
            }
            case "Brand": {
                if (brandPosition < brandAdapter.getGroupCount()) {
                    brandAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                brandPosition++;
                                if (brandPosition < brandAdapter.getGroupCount()) {
                                    layoutManager.scrollToPositionWithOffset(brandPosition, 0);
                                    brandAdapter.expandGroup(brandPosition, true);
                                }
                            }, 10);
                } else {
                    brandPosition = -1;
                    brandAdapter.collapseAllGroup();
                }
                break;
            }
            case "Group": {
                if (groupPosition < groupAdapter.getGroupCount()) {
                    groupAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                groupPosition++;
                                if (groupPosition < groupAdapter.getGroupCount()) {
                                    layoutManager.scrollToPositionWithOffset(groupPosition, 0);
                                    groupAdapter.expandGroup(groupPosition, true);
                                }
                            }, 10);
                } else {
                    groupPosition = -1;
                    groupAdapter.collapseAllGroup();
                }
                break;
            }
            case "Category": {
                if (categoryPosition < categoryAdapter.getGroupCount()) {
                    categoryAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                categoryPosition++;
                                if (categoryPosition < categoryAdapter.getGroupCount()) {
                                    layoutManager.scrollToPositionWithOffset(categoryPosition, 0);
                                    categoryAdapter.expandGroup(categoryPosition, true);
                                }
                            }, 10);
                } else {
                    categoryPosition = -1;
                    categoryAdapter.collapseAllGroup();
                }
                break;
            }


        }

    }

    public void btnUp(View view) {

        switch (SharedPref.getType()) {
            case "Product": {
                if (productPosition > 0 && productPosition <= productAdapter.getGroupCount()) {
                    productAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                productPosition--;
                                Log.d("product", "btnUp: " + productPosition);
                                layoutManager.scrollToPositionWithOffset(productPosition, 0);
                                productAdapter.expandGroup(productPosition, true);
                            }, 10);

                } else {
                    if (productPosition == 0)
                        productAdapter.collapseGroup(productPosition, true);
                    productPosition = -1;
                }
                break;
            }

            case "Brand": {
                if (brandPosition > 0 && brandPosition <= brandAdapter.getGroupCount()) {
                    brandAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                brandPosition--;
                                layoutManager.scrollToPositionWithOffset(brandPosition, 0);
                                brandAdapter.expandGroup(brandPosition, true);
                            }, 10);


                } else {
                    if (brandPosition == 0)
                        brandAdapter.collapseGroup(brandPosition, true);
                    brandPosition = -1;
                }

                break;
            }

            case "Group": {

                if (groupPosition > 0 && groupPosition <= groupAdapter.getGroupCount()) {
                    groupAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                groupPosition--;
                                layoutManager.scrollToPositionWithOffset(groupPosition, 0);
                                groupAdapter.expandGroup(groupPosition, true);
                            }, 10);

                } else {
                    if (groupPosition == 0)
                        groupAdapter.collapseGroup(groupPosition, true);
                    groupPosition = -1;
                }

                break;
            }

            case "Category": {

                if (categoryPosition > 0 && categoryPosition <= categoryAdapter.getGroupCount()) {
                    categoryAdapter.collapseAllGroup();
                    new Handler()
                            .postDelayed(() -> {
                                categoryPosition--;
                                layoutManager.scrollToPositionWithOffset(categoryPosition, 0);
                                categoryAdapter.expandGroup(categoryPosition, true);
                            }, 10);

                } else {
                    if (categoryPosition == 0)
                        categoryAdapter.collapseGroup(categoryPosition, true);
                    categoryPosition = -1;
                }


                break;
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        client.deleteAllDataSet();
    }

    public void continueBtn(View view) {
        HashMap<Integer, Integer> itemMap = client.getHashMapOfDataSet();
        for (Map.Entry<Integer, Integer> itemData : itemMap.entrySet()) {
            Log.d("itemCtn", "continueBtn: " + itemData.getKey() + " : " + itemData.getValue());
        }
    }


    /// language methods
    public void langBtn(View view) {
        String lang = SharedPref.getLanguage();
        if (lang.equals("en")) {
            SharedPref.setLanguage("ur");
        } else {
            SharedPref.setLanguage("en");
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.finish();
        Intent refresh = new Intent(this, DropDownList.class);
        startActivity(refresh);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String lang = SharedPref.getLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }
}