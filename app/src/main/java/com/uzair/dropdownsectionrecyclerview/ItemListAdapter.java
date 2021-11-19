package com.uzair.dropdownsectionrecyclerview;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends BaseAdapter implements Filterable {
    List<Items> itemsList, mFilteredListCopy;
    SqliteClient client;
    Context context;
    ImageLoader imageLoader;

    public ItemListAdapter(List<Items> itemsList, Context context) {
        super();
        this.itemsList = itemsList;
        this.context = context;
        this.mFilteredListCopy = itemsList;
        client = new SqliteClient(context);
        imageLoader = ImageLoader.getInstance();


    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        String previousHeader = String.valueOf(mFilteredListCopy.get(position).getProductId());
        String nextHeader = String.valueOf(mFilteredListCopy.get(position + 1).getProductId());

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


    }

    @Override
    public void onBindSubheaderViewHolder(HeaderView subheaderHolder, int nextItemPosition) {
        super.onBindSubheaderViewHolder(subheaderHolder, nextItemPosition);

        String nextItem = client.getProductNameById(mFilteredListCopy.get(nextItemPosition).getProductId());
        final int sectionSize = getSectionSize(getSectionIndex(subheaderHolder.getAdapterPosition()));
        final String subheaderText = String.format(
                context.getString(R.string.subheader),
                nextItem,
                context.getResources().getQuantityString(R.plurals.item, sectionSize, sectionSize)
        );
        subheaderHolder.headerTitle.setText(subheaderText);

    }


    @Override
    public int getItemSize() {
        Log.d("ItemSize", "getItemSize: " + mFilteredListCopy.size());
        return mFilteredListCopy.size();
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
            //  notifyDataSetChanged();

            Log.d("TAG", "publishResults: " + mFilteredListCopy.size());

            for (int i = 0; i < mFilteredListCopy.size(); i++)
                Log.d("searchItemList", "publishResults: " + mFilteredListCopy.get(i).getItemName());
        }
    };
}
