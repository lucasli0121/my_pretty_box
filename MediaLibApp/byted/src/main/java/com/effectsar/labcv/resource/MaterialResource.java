package com.effectsar.labcv.resource;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.effectsar.labcv.platform.struct.Material;

public class MaterialResource {

    //*****************************************************************
    // remote material properties
    //*****************************************************************

    Material material;

    public MaterialResource(Material material){
        this.material = material;
    }

    public Material getRemoteMaterial() {
        return material;
    }

    public String getIcon(){
        if (isRemote()) {
            return material.getIcon();
        }
        return null;
    }

    public String getMaterialId(){
        if (isRemote()) {
            return material.getId();
        }
        return null;
    }

    public String getMd5(){
        if (isRemote()) {
            return material.getMd5();
        }
        return null;
    }

    public int getProgress(){
        if (isRemote()) {
            return material.getProgress();
        }
        return 0;
    }

    public boolean isDownloading(){
        if (isRemote()) {
            return material.isDownloading();
        }
        return false;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    // ****************************************************************
    // local material properties
    // ****************************************************************

    String path;
    String title;
    String tips;
    @DrawableRes
    int iconId;

    public MaterialResource(String path, String title, String tips, @DrawableRes int iconId){
        this.path = path;
        this.title = title;
        this.tips = tips;
        this.iconId = iconId;
    }

    public boolean isLocal(){
        return material == null;
    }

    public boolean isRemote(){
        return material != null;
    }

    public int getIconId() {
        return iconId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // ****************************************************************
    // local & remote shared material properties
    // ****************************************************************

    public String getTitle(){
        if (isRemote()) {
            return material.getTitle();
        } else {
            return this.title;
        }
    }

    public String getTips(){
        if (isRemote()) {
            return material.getTips();
        } else {
            return this.tips;
        }
    }

    // compatible
    private int titleId;
    private int descId;

    public MaterialResource(){}

    public MaterialResource(int title, int icon, int desc){
        this.titleId = title;
        this.iconId = icon;
        this.descId = desc;
    }

    public void setTitleText(String titleText) {
        this.title = titleText;
    }

    public MaterialResource setIcon(int icon) {
        this.iconId = icon;
        return this;
    }

    public int getTitleId(){
        return titleId;
    }


    public int getDesc(){
        return descId;
    }


}
