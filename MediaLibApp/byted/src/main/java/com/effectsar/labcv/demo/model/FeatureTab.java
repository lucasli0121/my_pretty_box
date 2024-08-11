package com.effectsar.labcv.demo.model;

import java.util.ArrayList;
import java.util.List;

public class FeatureTab extends FeatureTabItem {
    private List<FeatureTabItem> children;



    public FeatureTab(int titleId, List<FeatureTabItem> children) {
        super(titleId, 0, null);
        this.children = children;
    }





    public List<FeatureTabItem> getChildren() {
        return children;
    }

    public void addChild(FeatureTabItem child) {
        if (children == null){
            children = new ArrayList<>();

        }
        children.add(child);

    }


    public List<FeatureTabItem> toList() {
        List<FeatureTabItem> items = new ArrayList<>();
        items.add(this);
        for (FeatureTabItem item : children) {
            if (item instanceof FeatureTab) {
                items.addAll(((FeatureTab) item).toList());
            } else {
                items.add(item);
            }
        }
        return items;
    }
}
