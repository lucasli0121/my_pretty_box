package com.effectsar.labcv.common.model;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;

public class ButtonItem implements Serializable {
    private int title;
    private String titleText = null;
    private int icon;
    private int desc;

    public ButtonItem() {
    }
    public ButtonItem(int icon, int desc) {
        this.icon = icon;
        this.desc = desc;
    }

    public ButtonItem(int title, int icon, int desc) {
        this.title = title;
        this.icon = icon;
        this.desc = desc;
    }

    public int getTitle() {
        return title;
    }

    public String getTitle(Context context){
        if (TextUtils.isEmpty(titleText)) {
            return context.getString(title);
        } else {
            return titleText;
        }
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public ButtonItem setTitle(int title) {
        this.title = title;
        return this;
    }

    public int getIcon() {
        return icon;
    }

    public ButtonItem setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public int getDesc() {
        return desc;
    }

    public ButtonItem setDesc(int desc) {
        this.desc = desc;
        return this;
    }
}
