package com.effectsar.labcv.common.mediapicker.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by dmcBig on 2017/7/11.
 */

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private final int space;
    private final int spanCount;

    public SpacingDecoration(int spanCount, int space) {
        this.spanCount = spanCount;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        int position = parent.getChildLayoutPosition(view);
        if (position % spanCount == 0) {
            outRect.left = 0;
        }
    }
}
