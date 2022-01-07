package com.uzair.dropdownsectionrecyclerview.viewholders;

import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uzair.dropdownsectionrecyclerview.R;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class ChildViewHolder extends ExpandableAdapter.ViewHolder {
    public TextView itemName, itemSqCode, availableStock, totalPcs;
    public ImageView itemImage;
    public EditText edCtn, edPcs, edBox;
    public LinearLayout boxLayout;
    public TextWatcher editTextWatcher;

    public ChildViewHolder(View itemView) {
        super(itemView);
        boxLayout = itemView.findViewById(R.id.boxLayout);
        totalPcs = itemView.findViewById(R.id.totalPcs);
        availableStock = itemView.findViewById(R.id.availableStock);
        itemName = itemView.findViewById(R.id.itemName);
        itemSqCode = itemView.findViewById(R.id.itemSqCode);
        edCtn = itemView.findViewById(R.id.edCtn);
        edPcs = itemView.findViewById(R.id.edPcs);
        edBox = itemView.findViewById(R.id.edBox);
        itemImage = itemView.findViewById(R.id.itemImage);
    }

    public void setEdBox(int text)
    {
        if(text != 0) {
            edBox.setText("" + text);
        }else {
            edBox.setText("");
        }
    }
}
