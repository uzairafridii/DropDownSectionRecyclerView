package com.uzair.dropdownsectionrecyclerview.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseAdapter extends SectionedRecyclerViewAdapter<BaseAdapter.HeaderView, BaseAdapter.ItemView> {

    OnItemClickListener onItemClickListener;

    BaseAdapter() {
        super();
    }

    @Override
    public ItemView onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, null));
    }

    @Override
    public HeaderView onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new HeaderView(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, null));

    }


    @Override
    public void onBindSubheaderViewHolder(HeaderView subheaderHolder, int nextItemPosition) {
        boolean isSectionExpanded = isSectionExpanded(getSectionIndex(subheaderHolder.getAdapterPosition()));

        if (isSectionExpanded) {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.getContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
        } else {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.getContext(), R.drawable.ic_baseline_keyboard_arrow_down_24));
        }
        subheaderHolder.itemView.setOnClickListener(v -> onItemClickListener.onSubheaderClicked(subheaderHolder.getAdapterPosition()));


    }


    /// header view holder
    public static class HeaderView extends RecyclerView.ViewHolder {

        public TextView headerTitle;
        public ImageView mArrow;

        public HeaderView(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerText);
            mArrow = itemView.findViewById(R.id.arrow);
        }
    }

    /// item view holder
    public static class ItemView extends RecyclerView.ViewHolder {

        public TextView itemName, itemSqCode, availableStock, totalPcs;
        public ImageView itemImage;
        public EditText edCtn, edPcs, edBox;

        public ItemView(@NonNull View itemView) {
            super(itemView);

            totalPcs = itemView.findViewById(R.id.totalPcs);
            availableStock = itemView.findViewById(R.id.availableStock);
            itemName = itemView.findViewById(R.id.itemName);
            itemSqCode = itemView.findViewById(R.id.itemSqCode);
            edCtn = itemView.findViewById(R.id.edCtn);
            edPcs = itemView.findViewById(R.id.edPcs);
            edBox = itemView.findViewById(R.id.edBox);
            itemImage = itemView.findViewById(R.id.itemImage);


        }


    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /// interface for item click
    public interface OnItemClickListener {
        void onItemClicked(Items item);

        void onSubheaderClicked(int position);
    }

}
