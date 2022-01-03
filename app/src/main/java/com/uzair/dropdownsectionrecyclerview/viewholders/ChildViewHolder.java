package com.uzair.dropdownsectionrecyclerview.viewholders;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.uzair.dropdownsectionrecyclerview.R;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class ChildViewHolder extends ExpandableAdapter.ViewHolder {
    public static TextView itemName, itemSqCode, availableStock, totalPcs;
    public static ImageView itemImage;
    public static EditText edCtn, edPcs, edBox;

    public ChildViewHolder(View itemView) {
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
