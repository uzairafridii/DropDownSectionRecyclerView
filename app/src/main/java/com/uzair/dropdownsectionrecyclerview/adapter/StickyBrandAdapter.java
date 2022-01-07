package com.uzair.dropdownsectionrecyclerview.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.activity.DropDownList;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.ProductBrand;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;
import com.uzair.dropdownsectionrecyclerview.viewholders.HeaderViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class StickyBrandAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder>
        implements Filterable {
    private Context context;
    private List<ProductBrand> brandList;
    private List<ProductBrand> mFilteredListCopy;
    private LinkedHashMap<Integer, Integer> itemDataMap = new LinkedHashMap<>();
    List<Integer> dataSetIdList;
    SqliteClient sqliteClient;

    public StickyBrandAdapter(List<ProductBrand> brandList, Context context) {
        this.brandList = brandList;
        this.mFilteredListCopy = brandList;
        this.context = context;
        sqliteClient = new SqliteClient(context);
        dataSetIdList = sqliteClient.getDataSetIdList();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return brandList.get(groupPosition).itemsList.size();
    }

    @Override
    public int getGroupCount() {
        return brandList.size();
    }

    @Override
    protected void onBindChildViewHolder(ViewHolder viewHolder, int groupPosition, int chilPosition, List<?> list) {

        if (list.isEmpty()) {
            Items items = brandList.get(groupPosition).getItemsList().get(chilPosition);
            // check item id
            if (sqliteClient.getDataSetId(items.getUid()) == items.getUid()) {
                ((ChildViewHolder) viewHolder).setEdBox(sqliteClient.getDataSetValueById(items.getUid()));
            } else {
                ((ChildViewHolder) viewHolder).edBox.setText("");
            }

            // remove text watcher
            if (((ChildViewHolder) viewHolder).editTextWatcher != null) {
                ((ChildViewHolder) viewHolder).edBox.removeTextChangedListener(((ChildViewHolder) viewHolder).editTextWatcher);
            }
            /// set item values
            ((ChildViewHolder) viewHolder).availableStock.setText("Stock Available : " + items.getBoxSize());
            ((ChildViewHolder) viewHolder).itemName.setText(items.getItemName());
            ((ChildViewHolder) viewHolder).itemSqCode.setText("SKU Code : " + items.getSkuCode());

            // add image
            Glide.with(context)
                    .load(items.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .centerCrop()
                    .into(((ChildViewHolder) viewHolder).itemImage);

            /// focus to get complete text
            ((ChildViewHolder) viewHolder).edBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && !((ChildViewHolder) viewHolder).edBox.getText().toString().trim().isEmpty()) {
                        int number = Integer.parseInt(((ChildViewHolder) viewHolder).edBox.getText().toString().trim());
                        // check for value in table
                        if (!dataSetIdList.contains(items.getUid())) {
                            sqliteClient.insertDataSet(items.getUid(), number);
                        } else {
                            // update the value
                            sqliteClient.updateDataSetValue(items.getUid(), number);
                        }
                    } else if (!hasFocus && ((ChildViewHolder) viewHolder).edBox.getText().toString().trim().isEmpty()) {
                        // delete data
                        sqliteClient.deleteDataSetValueById(items.getUid());
                    }
                }
            });


            /// text change listener on edBox
            ((ChildViewHolder) viewHolder).editTextWatcher = new TextWatcher() {
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
            };

            // add text watcher to new item
            ((ChildViewHolder) viewHolder).edBox.addTextChangedListener(((ChildViewHolder) viewHolder).editTextWatcher);


        }


    }

    @Override
    protected void onBindGroupViewHolder(ViewHolder viewHolder, int groupPosition, boolean expand, List<?> list) {
        ProductBrand product = brandList.get(groupPosition);
        ((HeaderViewHolder) viewHolder).headerTitle.setText(groupPosition + 1 + ". " + product.getName() +
                "  (" + getChildCount(groupPosition) + " Skus)");
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
    protected void onGroupViewHolderExpandChange(ViewHolder viewHolder, int i, long animDuration, boolean expand) {
        View arrowImage = ((HeaderViewHolder) viewHolder).mArrow;
        float deg;
        if (expand) {

            deg = arrowImage.getRotation() + 180F;
        } else {

            deg = (arrowImage.getRotation() == 180F) ? 0F : 180F;

        }
        arrowImage.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            List<ProductBrand> filteredList = new ArrayList<>();
            if (charString.isEmpty() || charString == null) {
                brandList = mFilteredListCopy;
            } else {

                for (int i = 0; i < mFilteredListCopy.size(); i++) {
                    for (Items item : mFilteredListCopy.get(i).getItemsList()) {
                        if (item.getItemName().toLowerCase().contains(charString.toLowerCase()) ||
                                item.getSkuCode().toLowerCase().contains(charString.toLowerCase())) {
                            List<Items> list = new ArrayList<>();
                            // set brand data
                            ProductBrand productBrand = new ProductBrand();
                            productBrand.setProductBrandId(mFilteredListCopy.get(i).getProductId());
                            productBrand.setName(mFilteredListCopy.get(i).getName());
                            productBrand.setProductBrandId(mFilteredListCopy.get(i).getProductBrandId());
                            //add item to item list
                            list.add(item);
                            // set that items list to brand
                            productBrand.setItemsList(list);
                            // add to filtered list
                            filteredList.add(productBrand);

                        }

                    }
                }

                brandList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = brandList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            brandList = (ArrayList<ProductBrand>) filterResults.values;
            notifyDataSetChanged();
            collapseAllGroup();
            DropDownList.setBrandPosition(0);
        }
    };

}
