package com.effectsar.labcv.demo.model;

public class SettingModel {
    // 0 - show type 1 - input type 2 - switch type
    private final SettingItemShowType type;

    // shown as header text
    private final SettingItemTitleEnum enumType;

    // shown as hint text
    private final String hintText;

    // used as default text
    private String content;

    public SettingModel(SettingItemShowType type, SettingItemTitleEnum enumType, String hintText, String content) {
        this.type = type;
        this.enumType = enumType;
        this.hintText = hintText;
        this.content = content;
    }

    public SettingItemShowType getType() {
        return type;
    }

    public SettingItemTitleEnum getEnumType() {
        return enumType;
    }

    public String getHintText() {
        return hintText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public enum SettingItemShowType {
        ST_SHOW(0),
        ST_INPUT(1),
        ST_SWITCH(2),

        ST_TRIGGER(3);

        private final int value;

        SettingItemShowType(int value) {
            this.value = value;
        }

        public int value() {return value;}
    }
    public enum SettingItemTitleEnum {
        SI_UNKNOWN("UNKNOWN"),
        SI_FPS("FPS"),
        SI_BOE("BOE"),
        SI_DEVICEID("DEVICE ID"),
        SI_CHANNELID("CHANNEL ID"),
        SI_DELLICCACHE("LIC缓存"),
        SI_CHANGEDID("切换Did"),
        SI_AUTHORIZE("授权类型"),
        SI_MIDPLATTYPE("中台源"),
        SI_EDITMODE("编辑模式"),
        ;

        private final String value;

        SettingItemTitleEnum(String value) {this.value = value;}

        public String value() {
            return value;
        }
    }
}
