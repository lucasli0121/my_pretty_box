package com.effectsar.labcv.core;

import android.content.Context;
import android.os.Environment;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.effect.EffectResourceProvider;

import java.io.File;
import java.util.Objects;

public class ResourceHelper implements EffectResourceProvider {
    public static final String RESOURCE = "resource";
    public static final String AUTO_ROOT = "/data/local/tmp/EffectsARSDK/auto";
    protected Context mContext;

    public ResourceHelper(Context mContext) {
        this.mContext = mContext;
    }

    /* Basic getPath methods. */

    public String getResourcePath() {
        return Objects.requireNonNull(mContext.getExternalFilesDir("assets")).getAbsolutePath() + File.separator + RESOURCE;
    }

    /* Material distribution getPath methods. */
    public String getMaterialPath(){
        return new File(getResourcePath(), "material").getAbsolutePath();
    }

    public String getConfigPath(){
        return new File(getResourcePath(), "config").getAbsolutePath();
    }

    /* getPath methods for .bundle directories. */
    public String get3DObjPath() {

        File objFolder = new File(getModelPath(), "3DModelResource.bundle");
        String objPath = objFolder.getAbsolutePath();

        File autoRoot3DObj = new File(new File(AUTO_ROOT, "model"), "3DModelResource.bundle");
        if (objFolder.exists() && autoRoot3DObj.exists() && autoRoot3DObj.isDirectory()) {
            return autoRoot3DObj.getAbsolutePath();
        }

        if (Config.ENABLE_ASSETS_SYNC) {
            return new File(getResourcePath(), "3DModelResource.bundle").getAbsolutePath();
        }
        return objPath;
    }

    @Override
    public String getAssetModelPath() {
        return RESOURCE + File.separator + "ModelResource.bundle";
    }

    @Override
    public String getModelPath(){

        File modelFolder = new File(getResourcePath(), "model");
        String modelPath = modelFolder.getAbsolutePath();

        File autoRootModel = new File(AUTO_ROOT, "model");
        if (modelFolder.exists() && autoRootModel.exists() && autoRootModel.isDirectory()) {
            return autoRootModel.getAbsolutePath();
        }

        if (Config.ENABLE_ASSETS_SYNC) {
            return new File(new File(getResourcePath(), "ModelResource.bundle"), "").getAbsolutePath();
        }
        return modelPath;
    }

    @Override
    public String getComposePath() {

        String composePath = getMaterialPath("ComposeMakeup") + File.separator;
        File composeFolder = new File(composePath);

        File autoRootCompose = new File(new File(AUTO_ROOT, "material"), "ComposeMakeup");
        if (composeFolder.exists() && autoRootCompose.exists() && autoRootCompose.isDirectory()) {
            return autoRootCompose.getAbsolutePath() + File.separator;
        }

        if (Config.ENABLE_ASSETS_SYNC) {
            return new File(new File(getResourcePath(), "ComposeMakeup.bundle"), "ComposeMakeup").getAbsolutePath() + File.separator;
        }
        return composePath;
    }

    @Override
    public String getFilterPath() {

        String filterPath = getMaterialPath("Filter");
        File filterFolder = new File(filterPath);

        File autoRootFilter = new File(new File(AUTO_ROOT, "material"), "Filter");
        if (filterFolder.exists() && autoRootFilter.exists() && autoRootFilter.isDirectory()) {
            return autoRootFilter.getAbsolutePath();
        }

        if (Config.ENABLE_ASSETS_SYNC) {
            return new File(new File(getResourcePath(), "FilterResource.bundle"), "Filter").getAbsolutePath();
        }
        return filterPath;
    }

    public String getStickerPath() {

        String stickerPath = getMaterialPath();
        File stickerFolder = new File(stickerPath);

        File autoRootSticker = new File(AUTO_ROOT, "material");
        if (stickerFolder.exists() && autoRootSticker.exists() && autoRootSticker.isDirectory()) {
            return autoRootSticker.getAbsolutePath();
        }

        if (Config.ENABLE_ASSETS_SYNC) {
            return new File( new File(getResourcePath(), "StickerResource.bundle"), "stickers").getAbsolutePath();
        }
        return stickerPath;
    }

    /* Extended getPath methods for convenient use */

    public String getMaterialPath(String material){
        return new File(getMaterialPath(), material).getAbsolutePath();
    }

    public String getModelPath(String model){
        return new File(getModelPath(), model).getAbsolutePath();
    }

    public String getConfigPath(String config){
        return new File(getConfigPath(), config).getAbsolutePath();
    }

    public String get3DObjPath(String modelName) {
        return new File(get3DObjPath(), modelName).getAbsolutePath();
    }

    @Override
    public String getFilterPath(String filter) {
        return new File(getFilterPath(), filter).getAbsolutePath();
    }

    @Override
    public String getStickerPath(String sticker) {
        return new File(getStickerPath(), sticker).getAbsolutePath();
    }

}
