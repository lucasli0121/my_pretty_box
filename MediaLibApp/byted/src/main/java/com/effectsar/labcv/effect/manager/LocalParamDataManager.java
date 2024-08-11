package com.effectsar.labcv.effect.manager;

import android.text.TextUtils;

import com.effectsar.labcv.common.database.LocalParamHelper;
import com.effectsar.labcv.common.database.LocalParamManager;
import com.effectsar.labcv.common.model.EffectType;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.effect.model.EffectButtonItem;
import com.effectsar.labcv.effect.model.FilterItem;

import java.util.ArrayList;

public class LocalParamDataManager {

    private static boolean mUseLocalParamStorage = true;

    public static boolean useLocalParamStorage(){
        return mUseLocalParamStorage;
    }

    public static void setUseLocalParamStorage(boolean use){
        mUseLocalParamStorage = use;
    }

    public static void saveComposerNode(EffectButtonItem item){
        if (useLocalParamStorage()) {
            if (item != null && item.getNode() != null && !TextUtils.isEmpty(item.getNode().getPath())) {
                for (int i = 0; i < item.getNode().getKeyArray().length; i++) {
                    LocalParamManager.getInstance().saveComposerNode(
                            item.getNode().getPath(),
                            item.getNode().getKeyArray()[i],
                            item.getIntensityArray()[i],
                            item.isEnableNegative()? 1 : 0,
                            item.getParent()==null? -1 : item.getParent().getSelectColorIndex(),
                            item.shouldHighLight(),
                            item.getParent() != null && (item.getParent().isEnableMultiSelect() ? item.shouldPointOn() : item.shouldHighLight() & item.getParent().shouldPointOn()));
//                    LogUtils.e("s: "+item.getNode().getPath()+"|"+item.getNode().getKeyArray()[0]+"|"+item.getIntensityArray()[0]);
                }
            }
        }
    }

    public static void removeComposerNode(EffectButtonItem item){
        if (useLocalParamStorage()) {
            if (item != null && item.getNode() != null && !TextUtils.isEmpty(item.getNode().getPath())) {
                LocalParamManager.getInstance().removeComposerNode(item.getNode().getPath());
                LogUtils.e("s: "+item.getNode().getPath()+"|"+item.getNode().getKeyArray()[0]+"|"+item.getIntensityArray()[0]);
            }
        }
    }

    public static void updateComposerNode(EffectButtonItem item){
        if (useLocalParamStorage()) {
            if (item != null && item.getNode() != null && !TextUtils.isEmpty(item.getNode().getPath())) {
                for (int i = 0; i < item.getNode().getKeyArray().length; i++) {
                    LocalParamManager.getInstance().updateComposerNode(
                        item.getNode().getPath(),
                        item.getNode().getKeyArray()[i],
                        item.getIntensityArray()[i],
                        item.isEnableNegative()? 1 : 0,
                        item.getParent()==null? -1:item.getParent().getSelectColorIndex(),
                        item.shouldHighLight(),
                        item.getParent() != null && (item.getParent().isEnableMultiSelect() ? item.shouldPointOn() : item.shouldHighLight() & item.getParent().shouldPointOn()));
//                    LogUtils.e("u: "+item.getNode().getPath()+"|"+item.getNode().getKeyArray()[0]+"|"+item.getIntensityArray()[0]);
                }
            }
        }
    }

    public static void saveFilter(FilterItem item){
        if (useLocalParamStorage()) {
            if (item!= null && !TextUtils.isEmpty(item.getResource())) {
                LocalParamManager.getInstance().updateFilter(
                        item.getResource(),
                        item.getIntensity(),
                        item.shouldHighLight(),
                        item.getParent() != null && (item.getParent().isEnableMultiSelect() ? item.shouldPointOn() : item.shouldHighLight() & item.getParent().shouldPointOn()));
            }
//        ToastUtils.show("s: "+item.getResource()+"|"+item.getIntensity());
        }
    }

    public static void reset(){
        if (useLocalParamStorage()) {
            LocalParamManager.getInstance().reset();
        }
    }

    public static void load(EffectButtonItem item, EffectType effectType){
        if (useLocalParamStorage()) {
            if (item.hasChildren()) {
                // reset parents' mSelectedChild
                if (item.getChildren() != null && item.getChildren().length > 0) {
                    item.setSelected(false);
                    if (item.isEnableMultiSelect()) {
                        item.setSelectChild(null);
                    } else {
                        item.setSelectChild(item.getChildren()[0]);
                    }
                }
                // if a child is recorded to be selected, it will change its parents' mSelectedChild
                for (EffectButtonItem child : item.getChildren()) {
                    load(child, effectType);
                }
            } else {
                ArrayList<LocalParamHelper.LocalParam> localParamList = new ArrayList<>();
                if (item != null && item.getNode()!= null && !TextUtils.isEmpty(item.getNode().getPath())) {
                    if (item instanceof FilterItem) {
                        localParamList = LocalParamManager.getInstance().queryLocalParam(item.getNode().getPath());
                    } else {
                        if (item.getIntensityArray().length > 1) {
                            localParamList = LocalParamManager.getInstance().queryLocalParam(item.getNode().getPath());
                        } else {
                            localParamList = LocalParamManager.getInstance().queryLocalParam(item.getNode().getPath(), item.getNode().getKeyArray()[0]);
                        }
                    }
                }
                if ( localParamList!=null && localParamList.size() > 0) {
                    // local record founded
                    LocalParamHelper.LocalParam localParam = localParamList.get(0);
                    LogUtils.e("q: "+localParam.path+"|"+localParam.key+"|"+localParam.intensity);

                    // update intensity
                    for (int i = 0; i < localParamList.size(); i++) {
                        item.getAvailableItem().getIntensityArray()[i] = localParamList.get(i).intensity;
                    }
//                    // update enableNegative
//                    item.setEnableNegative(localParam.arg0 > 0 ? true:false);


                    if (item.getParent() != null) {
                        if (localParam.selected) {
                            for (EffectButtonItem childItem: item.getParent().getChildren()) {
                                if (childItem.isSelected()) {
                                    childItem.setSelectedRelationToRoot(true);
                                }
                            }
                            item.getParent().setSelectedRelationToRoot(false);
                            item.getParent().setSelectColorIndex((int)localParam.arg1);
                        } else {
                            item.setSelectedRelation(false);
                        }
                    }
                    item.setSelectColorIndex((int)localParam.arg1);
                } else {
                    item.setSelectChild(null);
                    if (item.getParent() != null) {
                        item.setSelectedRelation(false);
                    }
                    item.setIntensityArray(EffectDataManager.getDefaultIntensity(item.getId(),effectType,item.isEnableNegative()));
                }
            }
        }
    }

    public static void resetItem(EffectButtonItem item, EffectType effectType) {
        if (item.hasChildren()) {
            for (EffectButtonItem child : item.getChildren()) {
                resetItem(child, effectType);
            }
        }
        item.setSelectChild(null);
        item.setSelected(false);
//        item.setSelectColorIndex(0);
        item.setIntensityArray(EffectDataManager.getDefaultIntensity(item.getId(),effectType,item.isEnableNegative()));
    }

    public static void refresh(EffectButtonItem item){
        if (useLocalParamStorage()) {
            if (item.hasChildren()) {
                for (EffectButtonItem child : item.getChildren()) {
                    refresh(child);
                }
            } else {
                saveComposerNode(item);
            }
        }
    }

    public static ArrayList<LocalParamHelper.LocalParam> allItems(){
        return LocalParamManager.getInstance().queryAll();
    }

}
