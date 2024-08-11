package com.effectsar.labcv.common.view.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.effectsar.labcv.R;

public abstract class BaseOnlineViewHolder extends BaseViewHolder  {

    private final DownloadView dpv;

    public BaseOnlineViewHolder(@NonNull View itemView) {
        super(itemView);

        dpv = itemView.findViewById(R.id.dpv);
        dpv.bringToFront();

    }

    public void setState(DownloadView.DownloadState status){
        dpv.setState(status);
    }

    public void setProgress(float progress){
        dpv.setProgress(progress);
    }

    public abstract void setIcon(String iconUrl);

}
