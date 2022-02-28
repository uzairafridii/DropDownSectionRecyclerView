package com.uzair.dropdownsectionrecyclerview.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.uzair.dropdownsectionrecyclerview.R;
import com.uzair.dropdownsectionrecyclerview.activity.DropDownList;
import com.uzair.dropdownsectionrecyclerview.database.SqliteClient;
import com.uzair.dropdownsectionrecyclerview.model.Items;
import com.uzair.dropdownsectionrecyclerview.model.ProductItem;
import com.uzair.dropdownsectionrecyclerview.viewholders.ChildViewHolder;
import com.uzair.dropdownsectionrecyclerview.viewholders.HeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class StickyProductAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder>
        implements Filterable {

    List<ProductItem> mFilteredListCopy;
    private List<ProductItem> productList;
    List<Integer> dataSetIdList;
    Context context;
    SqliteClient sqliteClient;

    public StickyProductAdapter(List<ProductItem> productList, Context context) {
        this.productList = productList;
        this.mFilteredListCopy = productList;
        this.context = context;
        sqliteClient = new SqliteClient(context);
        dataSetIdList = sqliteClient.getDataSetIdList();
    }

    @Override
    public boolean isGroup(int viewType) {
        return viewType > 0;
    }

    @Override
    public int getChildCount(int groupPosition) {
        return productList.get(groupPosition).getItemsList().size();
    }

    @Override
    public int getGroupCount() {
        return productList.size();
    }


    @Override
    protected void onBindChildViewHolder(ViewHolder viewHolder, int groupPosition, int chilPosition, List<?> list) {
        if (list.isEmpty()) {
            Items items = productList.get(groupPosition).getItemsList().get(chilPosition);
            /**
             ****************** SET VALUES IN TEXT FIELDS
             */
            ((ChildViewHolder) viewHolder).availableStock.setText("Stock Available : " + items.getBoxSize());
            ((ChildViewHolder) viewHolder).itemName.setText(items.getItemName());
            ((ChildViewHolder) viewHolder).itemSqCode.setText("SKU Code : " + items.getSkuCode());
            Glide.with(context)
                    .load(items.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .centerCrop()
                    .into(((ChildViewHolder) viewHolder).itemImage);


            /**
             * ********** CHECK ITEM ID IN DB
             */
            if (sqliteClient.getDataSetId(items.getUid()) == items.getUid()) {
                ((ChildViewHolder) viewHolder).setEdBox(sqliteClient.getBoxValueById(items.getUid()));
                ((ChildViewHolder) viewHolder).setEdCtn(sqliteClient.getCtnValueById(items.getUid()));
                ((ChildViewHolder) viewHolder).setEdPcs(sqliteClient.getPcsValueById(items.getUid()));
                ((ChildViewHolder) viewHolder).setPcsTotal(sqliteClient.getTotalValueById(items.getUid()));

            }
            else {
                ((ChildViewHolder) viewHolder).edBox.setText("");
                ((ChildViewHolder) viewHolder).edCtn.setText("");
                ((ChildViewHolder) viewHolder).edPcs.setText("");
                ((ChildViewHolder) viewHolder).totalPcsValue.setText("");

            }

            /**
             ************ REMOVE TEXT WATCHER
             */
            if (((ChildViewHolder) viewHolder).boxTextWatcher != null) {
                ((ChildViewHolder) viewHolder).edBox.removeTextChangedListener(((ChildViewHolder) viewHolder).boxTextWatcher);
            }
            if (((ChildViewHolder) viewHolder).ctnTextWatcher != null) {
                ((ChildViewHolder) viewHolder).edCtn.removeTextChangedListener(((ChildViewHolder) viewHolder).ctnTextWatcher);
            }
            if (((ChildViewHolder) viewHolder).pcsTextWatcher != null) {
                ((ChildViewHolder) viewHolder).edPcs.removeTextChangedListener(((ChildViewHolder) viewHolder).pcsTextWatcher);
            }


            /**
             ************ PCS EDIT TEXT FOCUS CHANGE LISTENER ******************
             */
            ((ChildViewHolder) viewHolder).edPcs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // get data set id list from db
                    dataSetIdList = sqliteClient.getDataSetIdList();
                    int total = 0;
                    if (!hasFocus && !((ChildViewHolder) viewHolder).edPcs.getText().toString().trim().isEmpty()) {
                        int number = Integer.parseInt(((ChildViewHolder) viewHolder).edPcs.getText().toString().trim());

                        // get total from pcs total textview
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }

                        // check for item value in table
                        if (!dataSetIdList.contains(items.getUid())) {
                            sqliteClient.insertDataSet(items.getUid(), 0, 0, number, total);

                        } else {
                            // update the value if id is exist
                            sqliteClient.updatePcsValue(items.getUid(), number, total);
                        }
                    } else if (!hasFocus && ((ChildViewHolder) viewHolder).edPcs.getText().toString().trim().isEmpty()) {
                        // get total from pcs total textview
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        // delete data if edittext is empty
                        sqliteClient.updatePcsValue(items.getUid(), 0, total);
                    }

                }
            });

            /**
             ************ CTN EDIT TEXT FOCUS CHANGE LISTENER ******************
             */
            ((ChildViewHolder) viewHolder).edCtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // get data set id list from db
                    dataSetIdList = sqliteClient.getDataSetIdList();
                    int total = 0;
                    if (!hasFocus && !((ChildViewHolder) viewHolder).edCtn.getText().toString().trim().isEmpty()) {
                        int number = Integer.parseInt(((ChildViewHolder) viewHolder).edCtn.getText().toString().trim());
                        // get total from pcs total textview
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        // check for value in table
                        if (!dataSetIdList.contains(items.getUid())) {
                            sqliteClient.insertDataSet(items.getUid(), 0, number, 0, total);

                        } else {
                            // update the value
                            sqliteClient.updateCtnValue(items.getUid(), number, total);
                        }
                    } else if (!hasFocus && ((ChildViewHolder) viewHolder).edCtn.getText().toString().trim().isEmpty()) {
                        // get total from pcs total textview
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        // delete data
                        sqliteClient.updateCtnValue(items.getUid(), 0, total);
                    }
                }
            });

            /**
             ************ BOX EDIT TEXT FOCUS CHANGE LISTENER ******************
             */
            ((ChildViewHolder) viewHolder).edBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // get data set id list from db
                    dataSetIdList = sqliteClient.getDataSetIdList();
                    int total = 0;
                    if (!hasFocus && !((ChildViewHolder) viewHolder).edBox.getText().toString().trim().isEmpty()) {
                        int number = Integer.parseInt(((ChildViewHolder) viewHolder).edBox.getText().toString().trim());
                        // get total from pcs total textview
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        // check for value in table
                        if (!dataSetIdList.contains(items.getUid())) {
                            sqliteClient.insertDataSet(items.getUid(), number, 0, 0, total);
                        } else {
                            // update the value
                            sqliteClient.updateBoxValue(items.getUid(), number, total);
                        }
                    } else if (!hasFocus && ((ChildViewHolder) viewHolder).edBox.getText().toString().trim().isEmpty()) {
                        // get total from pcs total textview
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        // delete data
                        sqliteClient.updateBoxValue(items.getUid(), 0, total);
                    }
                }
            });

            /**
             *    text change listener on edBox
             */
            /// edittext box text watcher
            ((ChildViewHolder) viewHolder).boxTextWatcher = new TextWatcher() {
                int number = 0;
                int total = 0;
                int boxTotal;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {
                        number = Integer.parseInt(s.toString().trim());
                        boxTotal = number * items.getBoxSize();
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        int pcsTotal = total + boxTotal;
                        ((ChildViewHolder) viewHolder).totalPcsValue.setText("" + pcsTotal);

                    } else if (s.toString().length() == 0) {
                        if (total > boxTotal) {
                            int pcsTotal = total - boxTotal;
                            ((ChildViewHolder) viewHolder).totalPcsValue.setText("" + pcsTotal);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            /// edittext pcs text watcher
            ((ChildViewHolder) viewHolder).pcsTextWatcher = new TextWatcher() {
                int total = 0;
                int number = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().trim().isEmpty()) {
                        number = Integer.parseInt(s.toString());
                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }
                        total = total + number;
                        ((ChildViewHolder) viewHolder).totalPcsValue.setText("" + total);

                    } else if (s.length() == 0) {
//                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
//                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
//                        }
                        if (total > number) {
                            total = total - number;
                            ((ChildViewHolder) viewHolder).totalPcsValue.setText("" + total);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            /// edittext ctn text watcher
            ((ChildViewHolder) viewHolder).ctnTextWatcher = new TextWatcher() {
                int number, ctnTotal, total;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().isEmpty()) {
                        number = Integer.parseInt(s.toString().trim());
                        ctnTotal = number * items.getCtnSize();

                        if (!((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim().isEmpty()) {
                            total = Integer.parseInt(((ChildViewHolder) viewHolder).totalPcsValue.getText().toString().trim());
                        }

                        int pcsTotal = total + ctnTotal;
                        ((ChildViewHolder) viewHolder).totalPcsValue.setText("" + pcsTotal);

                    } else if (s.length() == 0) {

                        if (total > ctnTotal) {
                            int pcsTotal = total - ctnTotal;
                            ((ChildViewHolder) viewHolder).totalPcsValue.setText("" + pcsTotal);
                        }
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            /**
             * add text watcher to new item
             */
            ((ChildViewHolder) viewHolder).edCtn.addTextChangedListener(((ChildViewHolder) viewHolder).ctnTextWatcher);
            ((ChildViewHolder) viewHolder).edPcs.addTextChangedListener(((ChildViewHolder) viewHolder).pcsTextWatcher);
            ((ChildViewHolder) viewHolder).edBox.addTextChangedListener(((ChildViewHolder) viewHolder).boxTextWatcher);


            //// testing to clear the focus of edittext on back button click
            ((ChildViewHolder) viewHolder).edPcs.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if (keyCode == event.KEYCODE_BACK) {
                        Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                        ((ChildViewHolder) viewHolder).clearFocus();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

        }
    }


//    private void idleTyping(final int currentLen) {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int newLen = mEditTextChat.getText().length();
//                if (currentLen == newLen) {
//                    //  stopTyping();
//                }
//
//            }
//        }, 800);
//    }
//

    @Override
    protected void onBindGroupViewHolder(ViewHolder viewHolder, int groupPosition, boolean expand, List<?> list) {
        if (list.isEmpty()) {
            ProductItem product = productList.get(groupPosition);
            ((HeaderViewHolder) viewHolder).headerTitle.setText(groupPosition + 1 + ". " + product.getProductName() + "  (" + getChildCount(groupPosition) + " Skus)");
        }
    }

    @Override
    protected ViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
        Log.d("TAG", "onCreateChildViewHolder: " + i);
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout, null);
        return new ChildViewHolder(mView);
    }

    @Override
    protected ViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, null);
        return new HeaderViewHolder(mView);
    }

    @Override
    protected void onGroupViewHolderExpandChange(ViewHolder viewHolder, int groupPosition, long animDuration, boolean expand) {
        View arrowImage = ((HeaderViewHolder) viewHolder).mArrow;
        float deg;
        // rotate icon
        if (isExpand(groupPosition)) {
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
                productList = mFilteredListCopy;
            } else {

                List<ProductItem> filteredList = new ArrayList<>();
                for (int i = 0; i < mFilteredListCopy.size(); i++) {
                    for (Items row : mFilteredListCopy.get(i).getItemsList()) {
                        if (row.getSkuCode().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getItemName().toLowerCase().contains(charString.toLowerCase())) {
                            List<Items> list = new ArrayList<>();
                            // set product data
                            ProductItem productItem = new ProductItem();
                            productItem.setProductName(mFilteredListCopy.get(i).getProductName());
                            productItem.setStatus(mFilteredListCopy.get(i).getStatus());
                            productItem.setUid(mFilteredListCopy.get(i).getUid());
                            productItem.setCategoryId(mFilteredListCopy.get(i).getCategoryId());
                            productItem.setCompanyId(mFilteredListCopy.get(i).getCompanyId());
                            // add row to list
                            list.add(row);
                            // set list row in product
                            productItem.setItemsList(list);
                            // add to filtered list
                            filteredList.add(productItem);
                            Log.d("tagUzairSearch", "performFiltering: " + row.getSkuCode());
                        }

                    }


                }

                productList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = productList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            productList = (ArrayList<ProductItem>) filterResults.values;
            notifyDataSetChanged();
            collapseAllGroup();
            DropDownList.setProductPosition(-1);
        }
    };

}


