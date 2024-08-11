package com.effectsar.labcv.demo.model;

public class FeatureTabItem {
    private int titleId;
    private int iconId;
    private FeatureConfig content;

    public FeatureTabItem() {
    }

    public FeatureTabItem(int titleId, int iconId, FeatureConfig config) {
        this.titleId = titleId;
        this.iconId = iconId;
        this.content = config;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public FeatureConfig getConfig() {
        return content;
    }

    public void setContent(FeatureConfig content) {
        this.content = content;
    }
}
