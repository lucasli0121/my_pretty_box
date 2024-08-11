package com.effectsar.labcv.common.imgsrc.video;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import androidx.annotation.NonNull;
import android.view.Surface;
import android.widget.ImageView;


import com.effectsar.labcv.common.imgsrc.ImageSourceProvider;
import com.effectsar.labcv.common.imgsrc.TextureHolder;
import com.effectsar.labcv.core.opengl.GlUtil;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

public class VideoSourceImpl implements ImageSourceProvider<String>, SimplePlayer.IPlayStateListener {

    private SimplePlayer mSimplePlayer;
    private final SimplePlayer.IPlayStateListener mPlayStateListener;
    private final SimplePlayer.IAudioDataListener mAudioDataLitener;
    private final TextureHolder mTextureHolder;
    private final GLSurfaceView mSurfaceView;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    private boolean isFrameAvailable = false;

    private boolean mSimplerStepMode = false;
    private boolean mSimplerAudioOn = true;
    private boolean mSimplerAutoReplay = false;

    /** {zh} 
     * 构造函数，如果需要录制视频，音视频数据都需要传递给编码器
     * @param surfaceView
     * @param listener
     * @param audioDataLitener
     *
     */
    /** {en} 
     * Constructor, if you need to record video, audio and video data need to be passed to the encoder
     * @param surfaceView
     * @param listener
     * @param audioDataLitener
     *
     */

    public VideoSourceImpl(GLSurfaceView surfaceView, @NonNull SimplePlayer.IPlayStateListener listener, @NonNull SimplePlayer.IAudioDataListener audioDataLitener) {
        this.mSurfaceView = surfaceView;
        this.mPlayStateListener = listener;
        this.mAudioDataLitener = audioDataLitener;
        mTextureHolder = new TextureHolder();
    }

    @Override
    public void open(String mediaPath, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {
        mTextureHolder.onCreate(onFrameAvailableListener);
        mSimplePlayer = new SimplePlayer(new Surface(mTextureHolder.getSurfaceTexture()), mediaPath);
        mSimplePlayer.setStepMode(mSimplerStepMode);

        mSimplePlayer.setPlayStateListener(this);
        mSimplePlayer.setAudioDataListener(mAudioDataLitener);
        mSimplePlayer.setAudioOn(mSimplerAudioOn);
        mSimplePlayer.setAutoReplay(mSimplerAutoReplay);
        mSimplePlayer.play();
        isFrameAvailable = true;
    }

    @Override
    public void videoAspect(int width, int height, final int rotation) {
        mVideoRotation = rotation;
        /*   {zh} 视频存储的长宽不管是否旋转，返回的都是转正后的尺寸，而我们demo后续逻辑会跟相机保持一致，对有旋转的图像，会对换宽高，因此，此处把宽高返回方式对齐相机，根据旋转角度做一次旋转     {en} No matter whether the length and width of the video storage are rotated or not, the size returned is the size after becoming a full member, and the subsequent logic of our demo will be consistent with the camera. For images with rotation, the width and height will be changed. Therefore, the width and height return mode is aligned with the camera here, and a rotation is made according to the rotation angle */
        if (mVideoRotation % 180 == 90){
            mVideoWidth = height;
            mVideoHeight = width;
        }else {
            mVideoWidth = width;
            mVideoHeight = height;
        }

        if(null != mPlayStateListener){
            mPlayStateListener.videoAspect(width,height,rotation);
        }
    }

    @Override
    public void onVideoEnd() {
        if(null != mPlayStateListener){
            mPlayStateListener.onVideoEnd();
        }
        isFrameAvailable = false;
    }

    @Override
    public void frameRate(int rate) {
        mPlayStateListener.frameRate(rate);
    }

    @Override
    public void update() {
        mTextureHolder.updateTexImage();
    }

    @Override
    public void close() {
        if (null != mTextureHolder){
            mTextureHolder.onDestroy();
        }
        if (null != mSimplePlayer){
            mSimplePlayer.destroy();
        }
        isFrameAvailable = false;

    }

    @Override
    public EffectsSDKEffectConstants.TextureFormat getTextureFormat() {
        return EffectsSDKEffectConstants.TextureFormat.Texture_Oes;
    }

    @Override
    public int getTexture() {
        if (null == mTextureHolder)return GlUtil.NO_TEXTURE;
        return mTextureHolder.getSurfaceTextureID();
    }

    @Override
    public float[] getUVMatrix() {
        if (null == mTextureHolder) return null;
        return mTextureHolder.getmMPV();
    }

    @Override
    public int getWidth() {
        return mVideoWidth;
    }

    @Override
    public int getHeight() {
        return mVideoHeight;
    }

    @Override
    public int getOrientation() {
        return mVideoRotation;
    }

    @Override
    public boolean isFront() {
        return false;
    }

    @Override
    public long getTimestamp() {
        return mTextureHolder.getTimeStamp();
    }

    @Override
    public float[] getFov() {
        return new float[0];
    }

    @Override
    public float[] getIsoInfo() {
        return new float[0];
    }

    @Override
    public boolean isReady() {
        return isFrameAvailable;
    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return ImageView.ScaleType.CENTER_INSIDE;
    }

    public void pausePlay() {
        mSimplePlayer.pause();
    }

    public void continuePlay() {
        mSimplePlayer.continuePlay();
    }

    public void setSimplePlayerStepModel(boolean mode) {
        mSimplerStepMode  = mode;
    }

    public void setSimplerAudioOn(boolean on) {
        mSimplerAudioOn = on;
    }

    public void setSimplerAutoReplay (boolean on) {
        mSimplerAutoReplay = on;
        if (mSimplePlayer != null) {
            mSimplePlayer.setAutoReplay(on);
        }
    }
}
