package com.effectsar.labcv.resource;

import android.content.Context;

import com.effectsar.labcv.common.utils.LocaleUtils;
import com.google.gson.annotations.Expose;

import java.util.Map;

public class StickerGroup {
    @Expose private Map<String, String> titles;
    @Expose private StickerItem[] items;

    public String getTitle(Context context) {
        String title = titles.get(LocaleUtils.getLanguage(context));
        if (title == null && titles.size() > 0) {
            return titles.entrySet().iterator().next().getValue();
        }
        return title;
    }

    public StickerItem[] getItems() {
        return items;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public void setItems(StickerItem[] items) {
        this.items = items;
    }
}
