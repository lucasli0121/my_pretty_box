package com.effectsar.labcv.common.view.item;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.effectsar.labcv.common.model.ButtonItem;
import com.effectsar.labcv.common.utils.CommonUtils;
import com.effectsar.labcv.core.util.LogUtils;

import java.util.List;

public class ItemViewRVAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> mItemList;
    protected OnItemClickListener<T, VH> mListener;

    public ItemViewRVAdapter(OnItemClickListener<T, VH> listener) {
        mListener = listener;
    }

    public ItemViewRVAdapter(List<T> itemList, OnItemClickListener<T, VH> listener) {
        mItemList = itemList;
        mListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mListener.onCreateViewHolderInternal(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final T item = mItemList.get(position);
        if (item == null) return;
        mListener.onBindViewHolderInternal(holder, position, item);
        holder.itemView.setOnClickListener(v -> {
            if (CommonUtils.isFastClick()) {
                LogUtils.e("too fast click");
                return;
            }
            mListener.changeItemSelectRecord(item, position);
            mListener.onItemClick(holder,item, position);
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public void setItemList(List<T> itemList) {
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<T> getItemList(){
        return  mItemList;
    }

    public interface OnItemClickListener<T, VH> {
        void onItemClick(VH holder,T item, int position);
        VH onCreateViewHolderInternal(ViewGroup parent, int viewType);
        void onBindViewHolderInternal(VH holder, int position, T item);
        void changeItemSelectRecord(T item, int position);
    }

}
