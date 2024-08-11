package com.effectsar.labcv.effect.model;

import androidx.annotation.DrawableRes;

import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.effect.utils.MathUtils;
import com.effectsar.labcv.resource.MaterialResource;
import com.effectsar.labcv.platform.struct.Material;

import java.util.ArrayList;

public class EffectButtonItem extends MaterialResource {
    private int id;
    private ComposerNode node;
    private EffectButtonItem parent;
    private boolean enableNegative = false;

    private EffectButtonItem[] children = new EffectButtonItem[0];
    private EffectButtonItem selectChild;
    private ArrayList<ColorItem> colorItems;
    private int selectColorIndex = 0;
    private boolean enableMultiSelect = true;
    private boolean reuseChildrenIntensity = true;
    private boolean selected = false;

    public void setMaterial(Material material){
        super.setMaterial(material);
    }

    public boolean isSelected() {
        boolean childSelected = false;
        for (EffectButtonItem child : getChildren()) {
            if (child.isSelected()) {
                childSelected = true;
            }
        }
        return selected || childSelected;
    }

    // TODO:
    public EffectButtonItem setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
//    public EffectButtonItem setSelected(boolean selected) {
//        if (children == null) {
//            this.selected = selected;
//        }
//        return this;
//    }

    // TODO:
    public EffectButtonItem setSelectedRelation(boolean selected){
        if (getParent() != null) {
            if (selected) {
                getParent().setSelectChild(this);
            } else {
                if (getParent().getSelectChild() == this) {
                    getParent().setSelectChild(children.length > 0? children[0] : null);
                }
            }
        }
        return this;
    }

    // TODO:
    public EffectButtonItem setSelectedRelationToRoot(boolean selected){
        if (getParent() != null) {
            if (selected) {
                getParent().setSelectChild(this);
            } else {
                if (getParent().getSelectChild() == this) {
                    getParent().setSelectChild(children.length > 0? children[0] : null);
                }
            }
            getParent().setSelected(selected).setSelectedRelation(selected);
        }
        return this;
    }

    public EffectButtonItem(int id, Material material){
        super(material);
        this.id = id;
    }

    // Composer Node compatible interface
    public EffectButtonItem(int id, Material material, ComposerNode node){
        super(material);
        this.id = id;
        this.node = node;
    }

    public EffectButtonItem(int id, String path, String title, String tips, @DrawableRes int iconId){
        super(path, title, tips, iconId);
        this.id = id;
    }

    public EffectButtonItem(int id) {
        this.id = id;
    }

    public EffectButtonItem(int id, EffectButtonItem[] children) {
        super();
        this.id = id;
        this.children = children;
        updateChildren();
    }

    public EffectButtonItem(int id, EffectButtonItem[] children, ArrayList<ColorItem> colorItems) {
        this.id = id;
        this.children = children;
        this.colorItems = colorItems;
        updateChildren();
    }

    public EffectButtonItem(int id, EffectButtonItem[] children, boolean enableMultiSelect) {
        this.id = id;
        this.children = children;
        this.enableMultiSelect = enableMultiSelect;
        updateChildren();
    }

    public EffectButtonItem(int id, int icon, int title) {
        super(title, icon, 0);
        this.id = id;
    }

    public EffectButtonItem(int id, int icon, int title, EffectButtonItem[] children) {
        super(title, icon, 0);
        this.id = id;
        this.children = children;
        updateChildren();
    }

    public EffectButtonItem(int id, int icon, int title, int desc) {
        super(title, icon, desc);
        this.id = id;
    }

    public EffectButtonItem(int id, int icon, int title, ComposerNode node) {
        super(title, icon, 0);
        this.id = id;
        this.node = node;
    }

    public EffectButtonItem(int id, int icon, int title, ComposerNode node, String path) {
        super(title, icon, 0);
        this.id = id;
        this.node = node;
        this.setPath(path);
    }

    public EffectButtonItem(int id, int icon, int title, ComposerNode node, ArrayList<ColorItem> colorItems) {
        super(title, icon, 0);
        this.id = id;
        this.node = node;
        this.colorItems = colorItems;
    }

    public EffectButtonItem(int id, int icon, int title, int desc, ComposerNode node, ArrayList<ColorItem> colorItems) {
        super(title, icon, desc);
        this.id = id;
        this.node = node;
        this.colorItems = colorItems;
    }

    public EffectButtonItem(int id, int icon, int title, int desc, ComposerNode node) {
        super(title, icon, desc);
        this.id = id;
        this.node = node;
    }

    public EffectButtonItem(int id, int icon, int title, int desc, ComposerNode node, String path) {
        super(title, icon, desc);
        this.id = id;
        this.node = node;
        this.setPath(path);
    }

    public EffectButtonItem(int id, int icon, int title, ComposerNode node, boolean enableNegative) {
        super(title, icon, 0);
        this.id = id;
        this.node = node;
        this.enableNegative = enableNegative;
    }

    public EffectButtonItem(int id, int icon, int title, int desc, ComposerNode node, boolean enableNegative) {
        super(title, icon, desc);
        this.id = id;
        this.node = node;
        this.enableNegative = enableNegative;
    }

    public EffectButtonItem(int id, int icon, int title, EffectButtonItem[] children, boolean enableMultiSelect) {
        super(title, icon, 0);
        this.id = id;
        this.children = children;
        this.enableMultiSelect = enableMultiSelect;
        updateChildren();
    }

    public EffectButtonItem(int id, int icon, String title, ComposerNode node) {
        super();
        super.setIcon(icon);
        super.setTitleText(title);
        this.id = id;
        this.node = node;
    }

    public float[] getValidIntensity() {
        if (selectChild == null) {
            if (node == null) return new float[0];
            return node.getIntensityArray();
        }
        return selectChild.getValidIntensity();
    }

    public EffectButtonItem getAvailableItem() {
        if (!hasChildren()) {
            return node == null ? null : this;
        }
        return selectChild == null ? null : selectChild.getAvailableItem();
    }

    private void updateChildren() {
        for (EffectButtonItem child : children) {
            child.parent = this;
        }
//        if (!enableMultiSelect) {
//            selectChild = children[0];
//        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ComposerNode getNode() {
        return node;
    }

    public void setNode(ComposerNode node) {
        this.node = node;
    }

    public float[] getIntensityArray() {
        if (node == null) return new float[0];

        return node.getIntensityArray();
    }

    public void setIntensityArray(float[] intensityArray) {
        if (node == null) return;
        this.node.setIntensityArray(intensityArray);
    }

    public EffectButtonItem getParent() {
        return parent;
    }

    public void setParent(EffectButtonItem parent) {
        this.parent = parent;
    }

    public boolean isEnableNegative() {
        return enableNegative;
    }

    public EffectButtonItem setEnableNegative(boolean enableNegative) {
        this.enableNegative = enableNegative;
        return this;
    }

    public EffectButtonItem[] getChildren() {
        return children;
    }

    public void setChildren(EffectButtonItem[] children) {
        this.children = children;
    }

    public EffectButtonItem getSelectChild() {
        return selectChild;
    }

    public EffectButtonItem getSelectRoot(){
        if (!hasChildren()) {
            if (parent != null && shouldHighLight()) {
                return this;
            } else {
                return null;
            }
        } else {
            if (selectChild != null) {
                return selectChild.getSelectRoot();
            }else {
                return null;
            }

        }
    }

    public void setSelectChild(EffectButtonItem selectChild) {
        this.selectChild = selectChild;
    }

    public boolean isEnableMultiSelect() {
        return enableMultiSelect;
    }

    public void setEnableMultiSelect(boolean enableMultiSelect) {
        this.enableMultiSelect = enableMultiSelect;
    }

    public boolean isReuseChildrenIntensity() {
        return reuseChildrenIntensity;
    }

    public void setReuseChildrenIntensity(boolean reuseChildrenIntensity) {
        this.reuseChildrenIntensity = reuseChildrenIntensity;
    }

    public ArrayList<ColorItem> getColorItems() {
        if (hasChildren()){
            for (EffectButtonItem child: getChildren()){
                if (child.getColorItems() != null && child.getColorItems().size() > 0){
                    return child.getColorItems();
                }
            }
        }
        return colorItems;
    }



    public EffectButtonItem setColorItems(ArrayList<ColorItem> colorItems) {
        this.colorItems = colorItems;
        return this;
    }

    public boolean hasChildren() {
        return children != null && children.length > 0;
    }


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
        for (EffectButtonItem item : getChildren()) {
            if (item.hasIntensity() && item.selected) {
                child = true;
            }

        }
        return self || child;
    }



    //  {zh} 是否高亮  {en} Whether to highlight
    public boolean shouldHighLight() {
        return getParent().getSelectChild() == this;

    }

    //  {zh} 是否显示小圆点  {en} Whether to show small dots
    public boolean shouldPointOn() {
        if (hasChildren()) {
            boolean status = false;
            for (EffectButtonItem item : getChildren()) {
                status = status || item.shouldPointOn();
            }
            return getParent()==null?
                    (status || isSelected() && hasIntensity())
                    :  (status || getParent().isEnableMultiSelect() && isSelected() && hasIntensity());
        } else {
            return getParent().isEnableMultiSelect() && isSelected() && hasIntensity();
        }
    }

    public int getSelectColorIndex() {
        return selectColorIndex;
    }

    public void setSelectColorIndex(int selectColorIndex) {
        this.selectColorIndex = selectColorIndex;
    }

    /**
     * deep copy
     * @return
     */
    public EffectButtonItem clone(){
        LogUtils.e("clone invoked");
        EffectButtonItem item = new EffectButtonItem(id);
        item.setIntensityArray(getIntensityArray().clone());
        item.setChildren(children.clone());
        item.setSelectColorIndex(selectColorIndex);
        item.setColorItems(colorItems);
        item.setEnableNegative(enableNegative);
        item.setParent(parent);
        item.setNode(node);
        item.setReuseChildrenIntensity(reuseChildrenIntensity);
        return item;

    }
}
