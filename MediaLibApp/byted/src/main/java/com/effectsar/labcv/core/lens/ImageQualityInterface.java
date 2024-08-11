package com.effectsar.labcv.core.lens;

import android.hardware.camera2.TotalCaptureResult;

import com.effectsar.labcv.core.util.ImageUtil;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

public interface ImageQualityInterface {
    /** {zh} 
     * 初始SDK，确保在gl线程中执行
     * dir 确定要求可读可写权限
     */
    /** {en} 
     * Initial SDK, ensure that
     * dir is executed in the gl thread to determine the read and write permissions required
     */

    int init(String dir, ImageUtil imageUtil);

    int destroy();

    void selectImageQuality(EffectsSDKEffectConstants.ImageQualityType type, boolean on);

    int processTexture(int srcTextureId,
                       int srcTextureWidth, int srcTextureHeight, ImageQualityResult result);

    void setPause(boolean pause);

    void recoverStatus();

    void setFrameInfo(TotalCaptureResult result);

    void setCameraIsoInfo(int maxIso, int minIso);


    class ImageQualityResult {
        int texture;
        int height;
        int width;

        // vida
        float face;
        float aes;
        float clarity;

        // taint detect
        float score;

        public ImageQualityResult(){
            texture = -1;
            height = 0;
            width = 0;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setTexture(int texture) {
            this.texture = texture;
        }

        public int getTexture() {
            return texture;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public float getFace() {
            return face;
        }

        public void setFace(float face) {
            this.face = face;
        }

        public float getAes() {
            return aes;
        }

        public void setAes(float aes) {
            this.aes = aes;
        }

        public float getClarity() {
            return clarity;
        }

        public void setClarity(float clarity) {
            this.clarity = clarity;
        }

        public float getScore() { return score; }

        public void setScore(float score) { this.score = score; }
    }
}
