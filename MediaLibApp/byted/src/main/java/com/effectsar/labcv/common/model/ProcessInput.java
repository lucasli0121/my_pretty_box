package com.effectsar.labcv.common.model;

import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

public class ProcessInput {
    private int texture;
    private int width;
    private int height;
    private byte[] buffer;
    private int format;
    public EffectsSDKEffectConstants.Rotation sensorRotation;


    public int getTexture() {
        return texture;
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[] getBuffer() { return buffer; }

    public void setBuffer(byte[] buffer) { this.buffer = buffer; }

    public int getFormat() { return format; }

    public void setFormat(int format) { this.format = format; }

    public EffectsSDKEffectConstants.Rotation getSensorRotation() {
        return sensorRotation;
    }

    public void setSensorRotation(EffectsSDKEffectConstants.Rotation sensorRotation) {
        this.sensorRotation = sensorRotation;
    }
}
