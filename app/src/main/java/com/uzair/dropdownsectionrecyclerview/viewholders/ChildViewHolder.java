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
    public TextView itemName, itemSqCode, availableStock, totalPcsValue;
    public ImageView itemImage;
    public EditText edCtn, edPcs, edBox;
    public LinearLayout boxLayout;
    public TextWatcher boxTextWatcher, ctnTextWatcher, pcsTextWatcher;

    public ChildViewHolder(View itemView) {
        super(itemView);
        boxLayout = itemView.findViewById(R.id.boxLayout);
        totalPcsValue = itemView.findViewById(R.id.totalPcsValue);
        availableStock = itemView.findViewById(R.id.availableStock);
        itemName = itemView.findViewById(R.id.itemName);
        itemSqCode = itemView.findViewById(R.id.itemSqCode);
        edCtn = itemView.findViewById(R.id.edCtn);
        edPcs = itemView.findViewById(R.id.edPcs);
        edBox = itemView.findViewById(R.id.edBox);
        itemImage = itemView.findViewById(R.id.itemImage);
    }

    public void clearFocus()
    {
        edCtn.clearFocus();
        edBox.clearFocus();
        edPcs.clearFocus();
    }
    public void setEdBox(int boxItem) {
        if (boxItem != 0) {
            edBox.setText("" + boxItem);
        } else {
            edBox.setText("");
        }
    }

    public void setEdCtn(int ctnItem) {
        if (ctnItem != 0) {
            edCtn.setText("" + ctnItem);
        } else {
            edCtn.setText("");
        }
    }

    public void setEdPcs(int pcsItem) {
        if (pcsItem != 0) {
            edPcs.setText("" + pcsItem);
        } else {
            edPcs.setText("");
        }
    }


    public void setPcsTotal(int total) {
        if (total != 0) {
            totalPcsValue.setText("" + total);
        } else {
            totalPcsValue.setText("");
        }
    }


}
