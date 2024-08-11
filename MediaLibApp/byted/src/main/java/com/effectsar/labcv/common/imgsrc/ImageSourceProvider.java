package com.effectsar.labcv.common.imgsrc;

import android.graphics.SurfaceTexture;
import android.widget.ImageView;

import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

public interface ImageSourceProvider<T> {

    void update();
    void close();
    void open(T media,SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener);
    EffectsSDKEffectConstants.TextureFormat getTextureFormat();
    int getTexture();
    int getWidth();
    int getHeight();
    int getOrientation();
    boolean isFront();
    float[] getFov();
    float[] getIsoInfo();
    ImageView.ScaleType getScaleType();
    float[] getUVMatrix();


    /** {zh} 
     * 判断当前源是否ready 因为相机是异步开启，所有需要在onDraw之前判断相机状态
     * @return
     */
    /** {en} 
     * Determine whether the current source is ready because the camera is turned on asynchronously, all the camera status needs to be determined before onDraw
     * @return
     */

    boolean isReady();
    long getTimestamp();
}
