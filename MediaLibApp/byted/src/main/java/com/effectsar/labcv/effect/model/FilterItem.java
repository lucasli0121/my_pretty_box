package com.effectsar.labcv.effect.model;

import com.effectsar.labcv.effect.utils.MathUtils;

public class FilterItem extends EffectButtonItem {
//    private String resource;
//    private float intensity;
    int selectChildPosition = 0;

    public FilterItem(int id, int title, int icon){
        super(id,icon,title);
    }

    public FilterItem(int id, FilterItem[] children){
        super(id, children);
    }

    public FilterItem(int id, FilterItem[] children, boolean enableMultiSelect){
        super(id, children, enableMultiSelect);
    }

    public FilterItem(int id, int title, int icon, String resource) {
        super(id, icon, title, new ComposerNode(resource, "", 0.0f));
//        setTitle(title);
//        setIcon(icon);
//        this.resource = resource;
    }

    public FilterItem(int id, int title, int icon, String resource, float intensity) {
        super(id,icon, title, new ComposerNode(resource, "", intensity));
//        setTitle(title);
//        setIcon(icon);
//        this.resource = resource;
//        this.intensity = intensity;
    }

    public FilterItem(int id, int title, int icon, FilterItem[] children, boolean enableMultiSelect) {
        super(id, icon, title, children, enableMultiSelect);
    }

    public String getResource() {
        if (getNode() != null) {
            return getNode().getPath();
        }
        return "";
//        return resource;
    }

    public void setResource(String resource) {
        if (getNode() != null) {
            getNode().setPath(resource);
        }
//        this.resource = resource;
    }

    public float getIntensity() {
        if (getNode() != null) {
            return getIntensityArray()[0];
        }
        return 0.0f;
//        return intensity;
    }

    public void setIntensity(float intensity) {
        setIntensityArray(new float[]{intensity});
//        this.intensity = intensity;
    }

    public void setSelectChild(FilterItem selectChild) {
        super.setSelectChild(selectChild);
        EffectButtonItem[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i].equals(selectChild)) {
                selectChildPosition = i;
                return;
            }
        }
        selectChildPosition = 0;
    }
//
    public void unsetSelectChild() {
        EffectButtonItem[] children = getChildren();
        if (children != null) {
            super.setSelectChild(children[0]);
            selectChildPosition = 0;
        }
    }

    public int getSelectChildPosition() {
        return selectChildPosition;
    }

//    public void setSelected(boolean selected) {
//        super.setSelected(selected);
////        if (getParent() != null) {
////            if (selected) {
////                ((FilterItem)getParent()).setSelectChild(this);
////            } else {
//////                ((FilterItem)getParent()).unsetSelectChild();
////            }
////        }
//    }

    public boolean hasIntensity() {
        boolean self = false;
        if (getIntensityArray().length > 0) {
            if (isEnableNegative()) {

                self = !MathUtils.floatEqual( getIntensityArray()[0], 0.5f);

            }else {
                self = getIntensityArray()[0]> 0;
            }
        }

        boolean child = false;
        EffectButtonItem item = getSelectChild();
        if (item != null) {
            if (item.hasIntensity()) {
                child = true;
            }
        }

//        for (EffectButtonItem item : getChildren()) {
//            if (item.hasIntensity()) {
//                child = true;
//            }
//
//        }

        return self || child;
    }

//    //  {zh} 是否显示小圆点  {en} Whether to show small dots
//    public boolean shouldPointOn() {
////        return hasIntensity();
//        return getParent().isEnableMultiSelect() && isSelected() && hasIntensity();
//    }
}
