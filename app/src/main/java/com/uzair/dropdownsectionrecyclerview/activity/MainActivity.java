package com.uzair.dropdownsectionrecyclerview.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

//import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.utils.SlidingDrawer;
import com.uzair.dropdownsectionrecyclerview.adapter.BaseAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.ExpandedListAdapter;
import com.uzair.dropdownsectionrecyclerview.adapter.ItemListAdapter;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.ItemGroup;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.Product;
import com.uzair.dropdownsectionrecyclerview.model.ProductBrand;
import com.uzair.dropdownsectionrecyclerview.model.ProductCategory;
import com.uzair.dropdownsectionrecyclerview.utils.Contracts;
import com.uzair.dropdownsectionrecyclerview.utils.SharedPref;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BaseAdapter.OnItemClickListener,
        ExpandedListAdapter.OnHeaderClickListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    DatabaseReference dbRef;
    SqliteClient client;
    List<Items> itemList;
    List<String> productBrandsList, productsName, categoryList, groupList;
    ItemListAdapter adapter;
    SearchView searchView;
    ExpandableListView sectionExpandedList;
    SlidingDrawer slidingDrawer;
    ImageView handleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        sectionSetup();

        /// search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    private void sectionSetup() {

        /// side sections name list
        groupList = client.getItemGroupList();
        productsName = client.getProductNameList();
        productBrandsList = client.getProductBrandList();
        categoryList = client.getProductCategoryNameList();

        // section header list and child list
        List<String> sectionList = new ArrayList<>();
        sectionList.add("Product");
        sectionList.add("Brand");
        sectionList.add("Group");
        sectionList.add("Category");

        HashMap<String, List<String>> listDataChild = new HashMap<>();
        listDataChild.put(sectionList.get(0), productsName);
        listDataChild.put(sectionList.get(1), productBrandsList);
        listDataChild.put(sectionList.get(2), groupList);
        listDataChild.put(sectionList.get(3), categoryList);

        /// expanded list view and adapter
        sectionExpandedList = findViewById(R.id.sectionMenuList);
        ExpandedListAdapter sectionAdapter = new ExpandedListAdapter(MainActivity.this,
                sectionList, listDataChild, this);
        sectionExpandedList.setAdapter(sectionAdapter);
        /// set onChild click on expanded list
        sectionExpandedList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (childPosition < adapter.getSectionsCount()) {
                slidingDrawer.animateClose();
                adapter.collapseAllSections();
                recyclerView.scrollToPosition(adapter.getSectionSubheaderPosition(childPosition));
                adapter.expandSection(childPosition);
            } else {
                Toast.makeText(MainActivity.this, "No Section Found", Toast.LENGTH_SHORT).show();
            }
            return false;
        });


        /// nav and drawer size setup
        handleImage = findViewById(R.id.handle_id);
        slidingDrawer = findViewById(R.id.drawer);
        double width = getResources().getDisplayMetrics().widthPixels / 1.5;
        FrameLayout layout = findViewById(R.id.content_c);
        SlidingDrawer.LayoutParams params = slidingDrawer.getLayoutParams();
        params.width = (int) width;
        layout.setLayoutParams(params);

        // click on sliding drawer
        slidingDrawer.setOnDrawerCloseListener(() -> {
            float deg = handleImage.getRotation() + 180F;
            handleImage.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
        });

        slidingDrawer.setOnDrawerOpenListener(() -> {
            float deg = (handleImage.getRotation() == 180F) ? 0F : 180F;
            handleImage.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());


        });


    }

    private void initViews() {
        /// pref
        SharedPref.init(this);
        // views ,db and get all data
        searchView = findViewById(R.id.searchView);
        dbRef = FirebaseDatabase.getInstance().getReference();
        client = new SqliteClient(this);
        getAllItems();
        getAllProducts();
        getAllItemsGroup();
        getProductsBrand();
        getProductCategory();

        /// recycler view
        itemList = client.getAllItems(Contracts.Items.COL_PRODUCT_ID, Contracts.Items.COL_BRAND_ID);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        /// recycler view adapter
        adapter = new ItemListAdapter(itemList, this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private void getProductCategory() {
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


    @Override
    public void onItemClicked(Items item) {
    }

    @Override
    public void onSubheaderClicked(int position) {
        //expand and collapse item list header
        if (adapter.isSectionExpanded(adapter.getSectionIndex(position))) {
            adapter.collapseSection(adapter.getSectionIndex(position));
        } else {
            adapter.expandSection(adapter.getSectionIndex(position));
        }
    }

    public void continueBtn(View view) {
        HashMap<Integer, Integer> itemMap = adapter.getItemDataMap();
        for (Map.Entry<Integer, Integer> itemData : itemMap.entrySet()) {
            Log.d("itemCtn", "continueBtn: " + itemData.getKey() + " : " + itemData.getValue());
        }

    }


    @Override
    public void onHeaderClick(int groupPosition, String groupName, boolean isExpanded) {

        switch (groupName) {
            case "Product":
            case "Category": {
                itemList = client.getAllItems(Contracts.Items.COL_PRODUCT_ID, Contracts.Items.COL_BRAND_ID);
                updateRecycler(isExpanded, groupName, groupPosition);
                break;
            }

            case "Brand": {
                itemList = client.getAllItems(Contracts.Items.COL_BRAND_ID, Contracts.Items.COL_PRODUCT_ID);
                updateRecycler(isExpanded, groupName, groupPosition);
                break;
            }
            case "Group": {
                itemList = client.getAllItems(Contracts.Items.COL_GROUP_ID, Contracts.Items.COL_PRODUCT_ID);
                updateRecycler(isExpanded, groupName, groupPosition);
                break;
            }
        }


    }

    private void updateRecycler(boolean isExpanded, String groupName, int groupPosition) {
        /// update the recycler by brand, group , product and category
        if (!isExpanded) {
            SharedPref.storeType(groupName);
            sectionExpandedList.expandGroup(groupPosition);
            recyclerView.scrollToPosition(0);
            adapter.notifyDataChanged();
        } else {
            sectionExpandedList.collapseGroup(groupPosition);
        }
    }


}