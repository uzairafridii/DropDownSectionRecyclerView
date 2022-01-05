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
import com.uzair.dropdownsectionrecyclerview.model.ItemGroup;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;
import com.uzair.dropdownsectionrecyclerview.viewholders.HeaderViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class StickyGroupAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder>
        implements Filterable {
    List<ItemGroup> itemGroupList;
    List<ItemGroup> mFilteredListCopy;
    Context context;
    LinkedHashMap<Integer, Integer> itemDataMap = new LinkedHashMap<>();

    public StickyGroupAdapter(List<ItemGroup> itemGroupList, Context context) {
        this.itemGroupList = itemGroupList;
        this.mFilteredListCopy = itemGroupList;
        this.context = context;
    }

    @Override
    public int getChildCount(int i) {
        return itemGroupList.get(i).itemsList.size();
    }

    @Override
    public int getGroupCount() {
        return itemGroupList.size();
    }

    @Override
    protected void onBindChildViewHolder(ViewHolder viewHolder, int groupPosition, int childPosition, List<?> list) {
        Items items = itemGroupList.get(groupPosition).itemsList.get(childPosition);

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

        /// set item values
        ((ChildViewHolder) viewHolder).availableStock.setText("Stock Available : " + items.getBoxSize());
        ((ChildViewHolder) viewHolder).itemName.setText(items.getItemName());
        ((ChildViewHolder) viewHolder).itemSqCode.setText("SKU Code : " + items.getSkuCode());
        Glide.with(context)
                .load(items.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(((ChildViewHolder) viewHolder).itemImage);

    }

    @Override
    protected void onBindGroupViewHolder(ViewHolder viewHolder, int groupPosition, boolean b, List<?> list) {
        ItemGroup itemGroup = itemGroupList.get(groupPosition);
        ((HeaderViewHolder) viewHolder).headerTitle.setText(groupPosition + 1 +". "+itemGroup.getName()
                + " (" + getChildCount(groupPosition) + " Skus" + ")");

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
    protected void onGroupViewHolderExpandChange(ViewHolder viewHolder, int i, long l, boolean expand) {
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
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            List<ItemGroup> filteredList = new ArrayList<>();
            if (charString.isEmpty() || charString == null) {
                itemGroupList = mFilteredListCopy;
            } else {

                for (int i = 0; i < mFilteredListCopy.size(); i++) {
                    for (Items item : mFilteredListCopy.get(i).getItemsList()) {
                        if (item.getItemName().toLowerCase().contains(charString.toLowerCase()) ||
                                item.getSkuCode().toLowerCase().contains(charString.toLowerCase())) {
                            List<Items> list = new ArrayList<>();
                            // set item group data
                            ItemGroup itemGroup = new ItemGroup();
                            itemGroup.setBrandId(mFilteredListCopy.get(i).getBrandId());
                            itemGroup.setGroupCode(mFilteredListCopy.get(i).getGroupCode());
                            itemGroup.setName(mFilteredListCopy.get(i).getName());
                            itemGroup.setItemGroupId(mFilteredListCopy.get(i).getItemGroupId());
                            // add item to list
                            list.add(item);
                            // set item list in group
                            itemGroup.setItemsList(list);
                            // add to filtered list
                            filteredList.add(itemGroup);

                            Log.d("uzairTag", "performFiltering: "+mFilteredListCopy.get(i).getName());

                        }

                    }
                }

                itemGroupList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = itemGroupList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            itemGroupList = (ArrayList<ItemGroup>) filterResults.values;
            notifyDataSetChanged();
            collapseAllGroup();
            DropDownList.setGroupPosition(0);
        }
    };


}
