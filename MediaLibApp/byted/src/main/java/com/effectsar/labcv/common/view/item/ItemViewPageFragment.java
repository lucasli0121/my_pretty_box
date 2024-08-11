package com.effectsar.labcv.common.view.item;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.effectsar.labcv.R;

public class ItemViewPageFragment<T, VH extends BaseViewHolder> extends Fragment {

    protected RecyclerView rv;
    protected ItemViewRVAdapter<T, VH> mAdapter;
    private int mSelectedPadding = 0;
    private RecyclerView.ItemDecoration mItemDecoration;
    private int mInitPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_item_view_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rv_item_view_page);
        if(mAdapter != null){
            initRVView();
        }
    }

    private void initRVView(){
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(mAdapter);
        if (mItemDecoration != null){
            rv.addItemDecoration(mItemDecoration);
        } else {
            rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    int totalCount = parent.getAdapter().getItemCount();
                    int rvMarginHorizontal = getResources().getDimensionPixelSize(R.dimen.rv_margin_horizontal)
                            - mSelectedPadding;
                    int space = getResources().getDimensionPixelSize(R.dimen.item_distance)
                            - 2 * mSelectedPadding;
                    if (position == 0) {// {zh} 第一个 {en} The first
                        outRect.left = rvMarginHorizontal;
                        outRect.right = space / 2;
                    } else if (position == totalCount - 1) {
                        outRect.left = space / 2;
                        outRect.right = rvMarginHorizontal;
                    } else {// {zh} 中间其它的 {en} Other in the middle
                        outRect.left = space / 2;
                        outRect.right = space / 2;
                    }
                }
            });
        }
        rv.scrollToPosition(mInitPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void refreshUI() {
        if (rv == null) return;
        if (mAdapter == null) return;
        mAdapter.notifyDataSetChanged();
    }

    public void setAdapter(ItemViewRVAdapter<T, VH> adapter){
        mAdapter = adapter;
    }

    public ItemViewRVAdapter<T, VH> getAdapter(){
        return mAdapter;
    }

    public RecyclerView getRecyclerView() {
        return rv;
    }

    public void setItemSelectedPadding(int padding){
        mSelectedPadding = padding;
    }

    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration){
        mItemDecoration = itemDecoration;
    }

    public void setInitPosition(int position){
        mInitPosition = position;
    }

    public void scrollToPosition(int position) {
        if (rv != null) {
            rv.scrollToPosition(position);
        }
    }

}
