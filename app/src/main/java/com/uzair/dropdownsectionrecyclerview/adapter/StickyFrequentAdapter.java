package com.uzair.dropdownsectionrecyclerview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.model.Common;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;
import com.uzair.dropdownsectionrecyclerview.viewholders.HeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class StickyFrequentAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder>
        implements Filterable {
    List<Common> commonList;
    List<Common> mFilteredListCopy;
    Context context;

    public StickyFrequentAdapter(List<Common> commonList, Context context) {
        this.commonList = commonList;
        this.context = context;
        this.mFilteredListCopy = commonList;
    }


    @Override
    public int getChildCount(int groupPosition) {
        return commonList.get(groupPosition).getItemsList().size();
    }

    @Override
    public int getGroupCount() {
        return commonList.size();
    }

    @Override
    protected void onBindChildViewHolder(ViewHolder viewHolder, int groupPosition, int childPosition, List<?> list) {
        Items items = commonList.get(groupPosition).getItemsList().get(childPosition);
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
        Common common = commonList.get(groupPosition);
        ((HeaderViewHolder) viewHolder).headerTitle.setText(common.getTitle() + "(" + getChildCount(groupPosition) + " Skus)");
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
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if (charString.isEmpty() || charString == null) {
                commonList = mFilteredListCopy;
            } else {

                List<Common> filteredList = new ArrayList<>();
                for (int i = 0; i < mFilteredListCopy.size(); i++) {
                    for (Items row : mFilteredListCopy.get(i).getItemsList()) {
                        if (row.getSkuCode().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getItemName().toLowerCase().contains(charString.toLowerCase())) {
                            List<Items> list = new ArrayList<>();
                            // set product data
                            Common common = new Common();
                            common.setTitle(mFilteredListCopy.get(i).getTitle());
                            // add row to list
                            list.add(row);
                            // set list row in product
                            common.setItemsList(list);
                            // add to filtered list
                            filteredList.add(common);
                            Log.d("tagUzairSearch", "performFiltering: " + row.getSkuCode());
                        }

                    }


                }

                commonList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = commonList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            commonList = (ArrayList<Common>) results.values;
            notifyDataSetChanged();
            expandAllGroup();
        }
    };
}
