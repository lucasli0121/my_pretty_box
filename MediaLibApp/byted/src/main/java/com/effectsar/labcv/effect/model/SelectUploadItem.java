package com.effectsar.labcv.effect.model;

import androidx.annotation.DrawableRes;

import com.effectsar.labcv.common.model.ButtonItem;

public class SelectUploadItem extends ButtonItem {
    private String path;

    public SelectUploadItem(@DrawableRes int icon) {
        setIcon(icon);
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
