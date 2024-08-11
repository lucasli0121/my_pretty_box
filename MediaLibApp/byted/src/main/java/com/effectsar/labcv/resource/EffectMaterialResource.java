package com.effectsar.labcv.resource;

import android.media.effect.Effect;

import androidx.annotation.DrawableRes;

import com.effectsar.labcv.platform.struct.Material;

import java.util.ArrayList;

public class EffectMaterialResource extends MaterialResource {

    // compatible to default value lookup;
    private int id;
    // properties indicates how to use this resource
    private String[] keyArray;
    private float[] defaultValueArray = new float[0];
    private boolean enableNegative = false;
    private ArrayList<ColorItem> colorItems;

    // stored running status
    private float[] intensityArray = new float[0];
    private int selectColorIndex = 0;

    // properties to build hierarchy tree
    private EffectMaterialResource parent;
    private EffectMaterialResource[] children = new EffectMaterialResource[0];
    private boolean enableMultiSelect = true;

    public EffectMaterialResource(Material material) {
        super(material);
    }

    public EffectMaterialResource(String path, String title, String tips, int iconId) {
        super(path, title, tips, iconId);
    }

    public EffectMaterialResource(String path, String title, String tips, int iconId, String[] keyArray, float[] defaultValueArray, boolean enableNegative, ArrayList<ColorItem> colorItems) {
        super(path, title, tips, iconId);
        this.keyArray = keyArray;
        this.defaultValueArray = defaultValueArray;
        this.enableMultiSelect = enableNegative;
        this.colorItems = colorItems;
    }


}
