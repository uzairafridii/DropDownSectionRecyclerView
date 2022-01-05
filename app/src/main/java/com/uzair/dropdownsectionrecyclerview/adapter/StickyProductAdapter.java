package com.uzair.dropdownsectionrecyclerview.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.activity.DropDownList;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.ProductItem;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;
import com.uzair.dropdownsectionrecyclerview.viewholders.HeaderViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;


public class StickyProductAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder>
        implements Filterable {

    List<ProductItem> mFilteredListCopy;
    private List<ProductItem> productList;
    LinkedHashMap<Integer, Integer> itemDataMap = new LinkedHashMap<>();
    Context context;

    public StickyProductAdapter(List<ProductItem> productList, Context context) {
        this.productList = productList;
        this.mFilteredListCopy = productList;
        this.context = context;
    }

    @Override
    public int getChildCount(int groupPosition) {
        return productList.get(groupPosition).getItemsList().size();
    }

    @Override
    public int getGroupCount() {
        return productList.size();
    }

    @Override
    protected void onBindChildViewHolder(ViewHolder viewHolder, int groupPosition, int chilPosition, List<?> list) {

        Items items = productList.get(groupPosition).getItemsList().get(chilPosition);

        /// set item values
        ((ChildViewHolder) viewHolder).availableStock.setText("Stock Available : " + items.getBoxSize());
        ((ChildViewHolder) viewHolder).itemName.setText(items.getItemName());
        ((ChildViewHolder) viewHolder).itemSqCode.setText("SKU Code : " + items.getSkuCode());

        Glide.with(context)
                .load(items.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(((ChildViewHolder) viewHolder).itemImage);


        if (itemDataMap.containsKey(items.getUid())) {
            ((ChildViewHolder) viewHolder).edBox.setText("" + itemDataMap.get(items.getUid()));
        }

        /// text change listener on edBox
        ((ChildViewHolder) viewHolder).edBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    // edBox.setId(items.getUid());
                    int number = Integer.parseInt(s.toString().trim());
                    itemDataMap.put(items.getUid(), number);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    @Override
    protected void onBindGroupViewHolder(ViewHolder viewHolder, int groupPosition, boolean expand, List<?> list) {
        ProductItem product = productList.get(groupPosition);
        ((HeaderViewHolder) viewHolder).headerTitle.setText(groupPosition + 1 + ". " + product.getProductName() + "  (" + getChildCount(groupPosition) + " Skus)");
    }

    @Override
    protected ViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout, null);
        return new ChildViewHolder(mView);
    }

    @Override
    protected ViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, null);
        return new HeaderViewHolder(mView);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    protected void onGroupViewHolderExpandChange(ViewHolder viewHolder, int i, long animDuration, boolean expand) {
        View arrowImage = ((HeaderViewHolder) viewHolder).mArrow;
        float deg;
        if (expand) {
            // rotate icon and change background of selected header
          //  ((HeaderViewHolder) viewHolder).headerTitle.setBackgroundColor(Color.MAGENTA);
            deg = arrowImage.getRotation() + 180F;
        } else {
            deg = (arrowImage.getRotation() == 180F) ? 0F : 180F;
        }
        arrowImage.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if (charString.isEmpty() || charString == null) {
                productList = mFilteredListCopy;
            } else {

                List<ProductItem> filteredList = new ArrayList<>();
                for (int i = 0; i < mFilteredListCopy.size(); i++) {
                    for (Items row : mFilteredListCopy.get(i).getItemsList()) {
                        if (row.getSkuCode().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getItemName().toLowerCase().contains(charString.toLowerCase())) {
                            List<Items> list = new ArrayList<>();
                            // set product data
                            ProductItem productItem = new ProductItem();
                            productItem.setProductName(mFilteredListCopy.get(i).getProductName());
                            productItem.setStatus(mFilteredListCopy.get(i).getStatus());
                            productItem.setUid(mFilteredListCopy.get(i).getUid());
                            productItem.setCategoryId(mFilteredListCopy.get(i).getCategoryId());
                            productItem.setCompanyId(mFilteredListCopy.get(i).getCompanyId());
                            // add row to list
                            list.add(row);
                            // set list row in product
                            productItem.setItemsList(list);
                            // add to filtered list
                            filteredList.add(productItem);
                            Log.d("tagUzairSearch", "performFiltering: " + row.getSkuCode());
                        }

                    }


                }

                productList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = productList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            productList = (ArrayList<ProductItem>) filterResults.values;
            notifyDataSetChanged();
            collapseAllGroup();
            DropDownList.setProductPosition(0);
        }
    };


}
