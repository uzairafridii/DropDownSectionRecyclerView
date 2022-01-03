package com.uzair.dropdownsectionrecyclerview.utils;

import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.dropdownsectionrecyclerview.R;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;

public class StickyHeader extends RecyclerView.ItemDecoration {

    private SectionCallback sectionCallback;
    private Integer mHeaderHeight;
    SectionedRecyclerViewAdapter adapter;

    public StickyHeader(SectionCallback sectionCallback) {
        this.sectionCallback = sectionCallback;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        adapter = (SectionedRecyclerViewAdapter) parent.getAdapter();

        View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }
        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }

        int headerPos = sectionCallback.getHeaderPositionForItem(topChildPosition);
        View currentHeader = getHeaderViewForItem(headerPos, parent);
        fixLayoutSize(parent, currentHeader);
        int contactPoint = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPoint, headerPos, adapter);


        if (childInContact != null && adapter.isSubheaderAtPosition(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact);
            return;
        }

        drawHeader(c, currentHeader);
    }

    private View getHeaderViewForItem(int headerPosition, RecyclerView parent) {
        int layoutResId = sectionCallback.getHeaderLayout(headerPosition);
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        sectionCallback.bindHeaderData(header, headerPosition);
        return header;
    }

    private void drawHeader(Canvas c, View headerView) {
        c.save();
        c.translate(0, 0);
        headerView.draw(c);
        c.restore();
    }

    private void moveHeader(Canvas c, View currentHeader, View nextHeader) {
        c.save();
        c.translate(0, nextHeader.getTop() - currentHeader.getHeight());
        currentHeader.draw(c);
        c.restore();

    }

    private View getChildInContact(RecyclerView parent, int contactPoint, int currentHeaderPos, SectionedRecyclerViewAdapter adapter) {
        View childInContact = null;

        Log.d("uzairtag", "getChildInContact: ");
        for (int i = 0; i < parent.getChildCount(); i++) {
            int heightTolerance = 0;
            View child = parent.getChildAt(i);

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                boolean isChildHeader = adapter.isSubheaderAtPosition(parent.getChildAdapterPosition(child));
                if (isChildHeader) {
                    heightTolerance = mHeaderHeight - child.getHeight();
                }
            }
            //add heightTolerance if child top be in display area
            int childBottomPosition;
            if (child.getTop() > 0) {
                childBottomPosition = child.getBottom() + heightTolerance;
            } else {
                childBottomPosition = child.getBottom();
            }

            if (childBottomPosition > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child;
                    break;
                }
            }
        }


        return childInContact;
    }


    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private void fixLayoutSize(ViewGroup parent, View view) {

        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        view.layout(0, 0, view.getMeasuredWidth(), mHeaderHeight = view.getMeasuredHeight());
    }

    // interface method for checking header
    public interface SectionCallback {
        boolean isHeader(int position);

        Integer getHeaderPositionForItem(Integer itemPosition);

        Integer getHeaderLayout(Integer headerPosition);

        void bindHeaderData(View header, Integer headerPosition);
    }
}
