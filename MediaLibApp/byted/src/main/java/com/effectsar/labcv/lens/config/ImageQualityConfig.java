package com.effectsar.labcv.lens.config;

public class ImageQualityConfig {
    public static final String IMAGE_QUALITY_KEY = "image_quality_key";

    public static final String KEY_VIDEO_SR = "video_sr";
    public static final String KEY_NIGHT_SCENE = "night_scene";
    public static final String KEY_ADAPTIVE_SHARPEN = "apdative_sharpen";
    public static final String KEY_PHOTO_NIGHT_SCENE = "photo_night_scene";
    public static final String KEY_VFI = "vfi";
    public static final String KEY_ONEKEY_ENHANCE = "onekey_enhance";
    public static final String KEY_VIDA = "vidas";
    public static final String KEY_TAINT_DETECT = "taint_scene_detect";
    public static final String KEY_VIDEO_LITE_HDR = "video_lite_hdr";
    public static final String KEY_CINE_MOVE = "cine_move";
    public static final String KEY_VIDEO_STAB = "video_stab";
    public static final String KEY_VIDEO_DEFLICKER = "video_deflicker";

    public ImageQualityConfig(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String type = "IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_SNAKE_V8";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
