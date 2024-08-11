package com.effectsar.labcv.common.view.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.effectsar.labcv.R;
import com.effectsar.labcv.resource.MaterialResource;
import com.effectsar.labcv.platform.utils.ExtensionKt;

import java.util.List;

public class MaterialFragment extends ItemViewPageFragment<MaterialResource, SelectOnlineViewHolder> {

    protected int mType;
    protected int mSelect = 0;
    private MaterialFragmentCallback mCallback;

    interface MaterialFragmentCallback {
        void onItemClick(MaterialResource item, int position);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setItemSelectedPadding(getResources().getDimensionPixelSize(R.dimen.select_padding));
        super.onViewCreated(view, savedInstanceState);
    }

    public MaterialFragment setCallback(MaterialFragmentCallback callback) {
        mCallback = callback;
        return this;
    }

    public MaterialFragment setData(List<MaterialResource> materials) {
        setAdapter(new ItemViewRVAdapter<MaterialResource, SelectOnlineViewHolder>(materials, new ItemViewRVAdapter.OnItemClickListener<MaterialResource, SelectOnlineViewHolder>() {
            @Override
            public void onItemClick(SelectOnlineViewHolder holder,MaterialResource item, int position) {
                if (mCallback != null) {
                    mCallback.onItemClick(item, position);
                }
            }

            @Override
            public SelectOnlineViewHolder onCreateViewHolderInternal(ViewGroup parent, int viewType) {
                return new SelectOnlineViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_holder_online_item, parent, false));
            }

            @Override
            public void onBindViewHolderInternal(SelectOnlineViewHolder holder, int position, MaterialResource material) {
                holder.setFocused(mSelect == position, position, material);

                if (material != null) {
                    if (material.isRemote()) {
                        holder.setIcon(material.getIcon());
                        boolean exits = ExtensionKt.exists(material.getRemoteMaterial());
                        if (exits || material.getProgress() >= 100) {
                            holder.setState(DownloadView.DownloadState.CACHED);
                        } else {
                            if (material.getProgress() > 0) {
                                holder.setState(DownloadView.DownloadState.DOWNLOADING);
                                holder.setProgress(material.getProgress() / 100F);
                            } else {
                                holder.setState(DownloadView.DownloadState.REMOTE);
                            }
                        }
                    } else {
                        holder.setIcon(material.getIconId());
                        holder.setState(DownloadView.DownloadState.CACHED);
                    }
                    holder.setTitle(material.getTitle());
                }
            }

            @Override
            public void changeItemSelectRecord(MaterialResource item, int position) {

            }
        }));
        return this;
    }

    public MaterialFragment setType(int type) {
        mType = type;
        return this;
    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshItem(int index) {
        mAdapter.notifyItemChanged(index);
    }

    public void setSelected(int select) {
        if (mAdapter == null) return;
        if (mSelect != select) {
            int oldSelect = mSelect;
            mSelect = select;
            mAdapter.notifyItemChanged(oldSelect);
            mAdapter.notifyItemChanged(select);
        }
    }

}
