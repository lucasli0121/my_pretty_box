package com.effectsar.labcv.demo.model;

import com.effectsar.labcv.algorithm.config.AlgorithmConfig;
import com.effectsar.labcv.common.config.ImageSourceConfig;
import com.effectsar.labcv.common.config.UIConfig;
import com.effectsar.labcv.effect.config.EffectConfig;
import com.effectsar.labcv.effect.config.StickerConfig;
import com.effectsar.labcv.lens.config.ImageQualityConfig;

public class FeatureConfig {
    private AlgorithmConfig algorithmConfig;
    private EffectConfig effectConfig;
    private ImageSourceConfig imageSourceConfig;
    private ImageQualityConfig imageQualityConfig;
    private StickerConfig stickerConfig;
    private UIConfig uiConfig;
    private String activityClassName;
    private String featureCategory;

    public FeatureConfig() {

    }

    public AlgorithmConfig getAlgorithmConfig() {
        return algorithmConfig;
    }

    public FeatureConfig setAlgorithmConfig(AlgorithmConfig algorithmConfig) {
        this.algorithmConfig = algorithmConfig;
        return this;
    }

    public EffectConfig getEffectConfig() {
        return effectConfig;
    }

    public FeatureConfig setEffectConfig(EffectConfig effectConfig) {
        this.effectConfig = effectConfig;
        return this;
    }

    public ImageSourceConfig getImageSourceConfig() {
        return imageSourceConfig;
    }

    public FeatureConfig setImageSourceConfig(ImageSourceConfig imageSourceConfig) {
        this.imageSourceConfig = imageSourceConfig;
        return this;
    }

    public ImageQualityConfig getImageQualityConfig() {
        return imageQualityConfig;
    }

    public FeatureConfig setImageQualityConfig(ImageQualityConfig config) {
        this.imageQualityConfig = config;
        return this;
    }

    public String getActivityClassName() {
        return activityClassName;
    }

    public FeatureConfig setActivityClassName(String activityClassName) {
        this.activityClassName = activityClassName;
        return this;
    }

    public StickerConfig getStickerConfig() {
        return stickerConfig;
    }

    public FeatureConfig setStickerConfig(StickerConfig stickerConfig) {
        this.stickerConfig = stickerConfig;
        return this;
    }

    public UIConfig getUiConfig() {
        return uiConfig;
    }

    public FeatureConfig setUiConfig(UIConfig uiConfig) {
        this.uiConfig = uiConfig;
        return this;
    }

    public FeatureConfig setFeatureCategory(String category) {
        featureCategory = category;
        return this;
    }

    public String getFeatureCategory() {
        return featureCategory;
    }

    @Override
    public FeatureConfig clone() {
        FeatureConfig config = new FeatureConfig();
        config.algorithmConfig = algorithmConfig;
        config.imageSourceConfig = imageSourceConfig;
        config.effectConfig = effectConfig;
        config.imageQualityConfig = imageQualityConfig;
        config.activityClassName = activityClassName;
        config.stickerConfig = stickerConfig;
        config.uiConfig = uiConfig;
        return config;
    }
}
