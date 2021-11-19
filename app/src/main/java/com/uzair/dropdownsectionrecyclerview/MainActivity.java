package com.uzair.dropdownsectionrecyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    DatabaseReference dbRef;
    SqliteClient client;
    List<Items> itemList;
    List<String> productsName;
    ItemListAdapter adapter;
    SearchView searchView;
    ListView productNameList;
    ArrayAdapter listViewAdapter;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        productNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <= adapter.getSectionsCount()) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    adapter.collapseAllSections();
                    recyclerView.scrollToPosition(adapter.getSectionSubheaderPosition(position));
                    adapter.expandSection(position);
                }
                else {
                    Toast.makeText(MainActivity.this, "Sorry! No Items in "+parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        /// search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void initViews() {

        drawerLayout = findViewById(R.id.drawerLayout);
        searchView = findViewById(R.id.searchView);
        dbRef = FirebaseDatabase.getInstance().getReference();
        client = new SqliteClient(this);
        getAllProducts();
        getAllItems();

        itemList = client.getAllItems();


        /// side menu show products name
        productsName = new ArrayList<>();
        productNameList = findViewById(R.id.productList);

        listViewAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_expandable_list_item_1, productsName);
        productNameList.setAdapter(listViewAdapter);

        /// recycler view
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        /// recycler view adapter
        adapter = new ItemListAdapter(itemList, this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
//        adapter.collapseAllSections();
//        adapter.expandSection(0);


    }


    private void getAllProducts() {
        dbRef.child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot query : snapshot.getChildren()) {
                            productsName.add(String.valueOf(query.child("name").getValue()));
                            Product product = new Product();
                            product.setProductName(String.valueOf(query.child("name").getValue()));
                            product.setStatus(String.valueOf(query.child("status").getValue()));
                            product.setUid(Integer.parseInt(String.valueOf(query.child("uid").getValue())));
                            product.setCompanyId(Integer.parseInt(String.valueOf(query.child("companyid").getValue())));
                            product.setCategoryId(Integer.parseInt(String.valueOf(query.child("category_id").getValue())));

                            client.insertProduct(product);
                        }
                        listViewAdapter.notifyDataSetChanged();



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
}