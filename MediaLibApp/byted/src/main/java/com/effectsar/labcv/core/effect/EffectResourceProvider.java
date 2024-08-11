package com.effectsar.labcv.core.effect;

public interface EffectResourceProvider {
    String getModelPath();
    String getAssetModelPath();
    String getComposePath();
    String getFilterPath();
    String getFilterPath(String filter);
    String getStickerPath(String sticker);
}
