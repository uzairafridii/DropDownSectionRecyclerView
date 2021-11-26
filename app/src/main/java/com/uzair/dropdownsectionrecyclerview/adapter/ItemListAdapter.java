package com.uzair.dropdownsectionrecyclerview.adapter;

import android.content.ClipData;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemListAdapter extends BaseAdapter implements Filterable {
    List<Items> itemsList, mFilteredListCopy;
    SqliteClient client;
    Context context;
    ImageLoader imageLoader;
    HashMap<Integer, Integer> itemDataMap = new HashMap<>();

    public ItemListAdapter(List<Items> itemsList, Context context) {
        super();
        this.itemsList = itemsList;
        this.context = context;
        this.mFilteredListCopy = itemsList;
        SharedPref.init(context);
        client = new SqliteClient(context);
        imageLoader = ImageLoader.getInstance();

    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        String previousHeader = "", nextHeader = "";

        switch (SharedPref.getType()) {
            case "Brand": {
                previousHeader = String.valueOf(mFilteredListCopy.get(position).getBranId());
                nextHeader = String.valueOf(mFilteredListCopy.get(position + 1).getBranId());
                break;
            }
            case "Category": {
                previousHeader = client.getProductCategoryId(mFilteredListCopy.get(position).getProductId());
                nextHeader = client.getProductCategoryId(mFilteredListCopy.get(position + 1).getProductId());
                break;
            }
            case "Group": {
                previousHeader = String.valueOf(mFilteredListCopy.get(position).getGroupId());
                nextHeader = String.valueOf(mFilteredListCopy.get(position + 1).getGroupId());
                break;
            }
            case "Product": {
                previousHeader = String.valueOf(mFilteredListCopy.get(position).getProductId());
                nextHeader = String.valueOf(mFilteredListCopy.get(position + 1).getProductId());
                break;
            }
        }


        return !previousHeader.equals(nextHeader);
    }

    @Override
    public void onBindItemViewHolder(ItemView childViewHolder, int itemPosition) {

        Items items = mFilteredListCopy.get(itemPosition);

        childViewHolder.availableStock.setText("Stock Available : " + items.getBoxSize());
        childViewHolder.itemName.setText(items.getItemName());
        childViewHolder.itemSqCode.setText("SKU Code : " + items.getSkuCode());
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(items.getImageUrl(), childViewHolder.itemImage);

        /// text change listener on edBox
        childViewHolder.edBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty() || s.toString() != null) {
                    int number = Integer.parseInt(s.toString());
                    itemDataMap.put(items.getUid(), number);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /// text change listener on edCtn
        childViewHolder.edCtn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /// text change listener on edPcs
        childViewHolder.edPcs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onBindSubheaderViewHolder(HeaderView subheaderHolder, int nextItemPosition) {
        super.onBindSubheaderViewHolder(subheaderHolder, nextItemPosition);

        String nextItem = "";

        switch (SharedPref.getType()) {
            case "Brand": {
                nextItem = client.getProductBrand(String.valueOf(mFilteredListCopy.get(nextItemPosition).getBranId()));
                break;
            }
            case "Category": {
                nextItem = client.getProductCategoryName(mFilteredListCopy.get(nextItemPosition).getProductId());
                break;
            }
            case "Group": {
                nextItem = client.getItemGroupById(mFilteredListCopy.get(nextItemPosition).getGroupId());
                break;
            }
            case "Product": {
                nextItem = client.getProductNameById(mFilteredListCopy.get(nextItemPosition).getProductId());
                break;
            }

        }

        final int sectionSize = getSectionSize(getSectionIndex(subheaderHolder.getAdapterPosition()));
        final String subheaderText = String.format(
                context.getString(R.string.subheader),
                nextItem,
                context.getResources().getQuantityString(R.plurals.item, sectionSize, sectionSize)
        );
        subheaderHolder.headerTitle.setText(subheaderText);

    }

//    @Override
//    public int getViewType(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public int getItemSize() {
        return mFilteredListCopy.size();
    }

    public HashMap<Integer, Integer> getItemDataMap() {
        return itemDataMap;
    }


    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if (charString.isEmpty()) {
                mFilteredListCopy = itemsList;
            } else {
                List<Items> filteredList = new ArrayList<>();
                for (Items row : itemsList) {
                    if (row.getItemName().toLowerCase().contains(charString.toLowerCase()) ||
                            row.getSkuCode().toLowerCase().contains(charString.toLowerCase())) {
                        filteredList.add(row);
                    }
                }

                mFilteredListCopy = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = mFilteredListCopy;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mFilteredListCopy = (ArrayList<Items>) filterResults.values;
            notifyDataChanged();
        }
    };
}
