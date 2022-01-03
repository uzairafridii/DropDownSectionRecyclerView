package com.uzair.dropdownsectionrecyclerview.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uzair.dropdownsectionrecyclerview.R;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class HeaderViewHolder extends ExpandableAdapter.ViewHolder {
    public static TextView headerTitle;
    public static ImageView mArrow;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        headerTitle = itemView.findViewById(R.id.headerText);
        mArrow = itemView.findViewById(R.id.arrow);
    }
}
