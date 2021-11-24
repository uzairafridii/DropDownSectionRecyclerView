package com.uzair.dropdownsectionrecyclerview.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uzair.dropdownsectionrecyclerview.R;
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
    //    List<ProductCategory> categoryList;
//    List<ItemGroup> itemsGroupList;
    List<String> productBrandsList, productsName, categoryList, groupList;
    ItemListAdapter adapter;
    SearchView searchView;
    DrawerLayout drawerLayout;
    ExpandableListView sectionExpandedList;


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


        /// side menu show products name
        groupList = client.getItemGroupList();
        productsName = client.getProductNameList();
        productBrandsList = client.getProductBrandList();
        categoryList = client.getProductCategoryNameList();

        HashMap<String, List<String>> listDataChild = new HashMap<>();

        List<String> sectionList = new ArrayList<>();
        sectionList.add("Product");
        sectionList.add("Brand");
        sectionList.add("Group");
        sectionList.add("Category");

        listDataChild.put(sectionList.get(0).toString(), productsName);
        listDataChild.put(sectionList.get(1).toString(), productBrandsList);
        listDataChild.put(sectionList.get(2).toString(), groupList);
        listDataChild.put(sectionList.get(3).toString(), categoryList);


        sectionExpandedList = findViewById(R.id.sectionMenuList);
        ExpandedListAdapter sectionAdapter = new ExpandedListAdapter(MainActivity.this,
                sectionList, listDataChild, this);

        sectionExpandedList.setAdapter(sectionAdapter);

        sectionExpandedList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
//            Toast.makeText(
//                    getApplicationContext(),
//                    sectionList.get(groupPosition)
//                            + " : "
//                            + listDataChild.get(
//                            sectionList.get(groupPosition)).get(
//                            childPosition), Toast.LENGTH_SHORT)
//                    .show();

            drawerLayout.closeDrawer(GravityCompat.START);
            adapter.collapseAllSections();
            recyclerView.scrollToPosition(adapter.getSectionSubheaderPosition(childPosition));
            adapter.expandSection(childPosition);
            return false;
        });

    }

    private void initViews() {

        SharedPref.init(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        searchView = findViewById(R.id.searchView);
        dbRef = FirebaseDatabase.getInstance().getReference();
        client = new SqliteClient(this);
        getAllProducts();
        getAllItems();
        getAllItemsGroup();
        getProductsBrand();
        getProductCategory();

        /// recycler view
        itemList = client.getAllItems();
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

        if (!isExpanded) {
            SharedPref.storeType(groupName);
            sectionExpandedList.expandGroup(groupPosition);
            recyclerView.scrollToPosition(0);
            adapter.notifyDataChanged();
        } else {
            sectionExpandedList.collapseGroup(groupPosition);
            adapter.notifyDataChanged();
        }

    }

}