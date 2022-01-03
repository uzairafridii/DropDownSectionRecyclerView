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
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.ProductCategory;
import com.uzair.dropdownsectionrecyclerview.model.ProductItem;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;
import com.uzair.dropdownsectionrecyclerview.viewholders.HeaderViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class StickyCategoryAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder>
     implements Filterable
{
    private List<ProductCategory> productCategoryList;
    private List<ProductCategory> mFilteredListCopy;
    private Context context;
    LinkedHashMap<Integer, Integer> itemDataMap = new LinkedHashMap<>();
    ImageLoader imageLoader;
    private int pos;

    public StickyCategoryAdapter(List<ProductCategory> productCategoryList , Context context) {
        this.productCategoryList = productCategoryList;
        this.mFilteredListCopy = productCategoryList;
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    @Override
    public int getChildCount(int groupPosition) {
        return productCategoryList.get(groupPosition).itemsList.size();
    }

    @Override
    public int getGroupCount() {
        return productCategoryList.size();
    }

    @Override
    protected void onBindChildViewHolder(ViewHolder viewHolder, int groupPosition, int childPosition, List<?> list) {

        Items items = productCategoryList.get(groupPosition).getItemsList().get(childPosition);

        if (itemDataMap.containsKey(items.getUid())) {
            ((ChildViewHolder) viewHolder).edBox.setText("" + itemDataMap.get(items.getUid()));
        }

        /// text change listener on edBox
        ((ChildViewHolder) viewHolder).  edBox.addTextChangedListener(new TextWatcher() {
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
       // imageLoader.displayImage(items.getImageUrl(), ((ChildViewHolder) viewHolder).itemImage);
        Glide.with(context)
                .load(items.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(((ChildViewHolder) viewHolder).itemImage);

    }

    @Override
    protected void onBindGroupViewHolder(ViewHolder viewHolder, int groupPosition, boolean b, List<?> list) {
        ProductCategory product = productCategoryList.get(groupPosition);
        ((HeaderViewHolder) viewHolder).headerTitle.setText(product.getName() + "  (" + getChildCount(groupPosition) + " Skus)");
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
    protected void onGroupExpandChange(int groupPosition, boolean expand) {
        if (expand) {
            pos++;
            DropDownList.setCategoryPosition(pos);
            Log.d("TAG", "onGroupExpandChange: open " + DropDownList.getProductPosition());
        } else {
            if(pos > 0) {
                pos--;
                DropDownList.setCategoryPosition(pos);
                Log.d("TAG", "onGroupExpandChange: close " + DropDownList.getProductPosition());
            }
        }
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
    public Filter getFilter()
    {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            List<ProductCategory> filteredList = new ArrayList<>();
            if (charString.isEmpty() || charString == null) {
                productCategoryList = mFilteredListCopy;
            } else {

                for (int i = 0; i < mFilteredListCopy.size(); i++) {
                    for (Items item : mFilteredListCopy.get(i).getItemsList()) {
                        if (item.getItemName().toLowerCase().contains(charString.toLowerCase()) ||
                                item.getSkuCode().toLowerCase().contains(charString.toLowerCase())) {
                            List<Items> list = new ArrayList<>();
                            // set category data
                            ProductCategory productCategory = new ProductCategory();
                            productCategory.setName(mFilteredListCopy.get(i).getName());
                            productCategory.setId(mFilteredListCopy.get(i).getId());
                            // add item to list
                            list.add(item);
                            // set list item in category model
                            productCategory.setItemsList(list);
                            // add to filtered list
                            filteredList.add(productCategory);

                        }

                    }
                }

                productCategoryList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = productCategoryList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productCategoryList = (ArrayList<ProductCategory>) results.values;
            notifyDataSetChanged();
            expandAllGroup();
        }
    };
}
