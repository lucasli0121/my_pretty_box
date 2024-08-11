package com.effectsar.labcv.common.imgsrc.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.opengl.GLSurfaceView;
import android.widget.ImageView;

import com.effectsar.labcv.common.imgsrc.ImageSourceProvider;
import com.effectsar.labcv.common.utils.BeMatrix4f;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

import java.util.ArrayList;
import java.util.List;

public class CameraSourceImpl implements ImageSourceProvider<Integer> {
    private final CameraProxy mCameraProxy;
    private final GLSurfaceView glSurfaceView;
    private SurfaceTexture.OnFrameAvailableListener mFrameListener;
    private ImageReader.OnImageAvailableListener mImageListener = null;
    private CameraFrameInfo mFrameInfoListener;

    private int mCameraID = android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean mCameraChanging;


    public CameraSourceImpl(Context context, GLSurfaceView glSurfaceView) {
        this.mCameraProxy =  new CameraProxy(context);
        this.glSurfaceView = glSurfaceView;
    }

    public void setFrameInfoListener (CameraFrameInfo listener) {
        mFrameInfoListener = listener;
    }

    @Override
    public void open(Integer cameraId, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {
        mFrameListener = onFrameAvailableListener;
        mCameraID = cameraId;
        mCameraProxy.openCamera(mCameraID, new CameraListener() {
            @Override
            public void onOpenSuccess() {
                onCameraOpen(mFrameListener);

            }

            @Override
            public void onOpenFail() {
                LogUtils.d("onOpenFail");
            }

            @Override
            public void onFrameArrived(TotalCaptureResult result) {
                if (mFrameInfoListener != null) {
                    mFrameInfoListener.onFrameInfoArrivedListener(result);
                }
            }
        });

    }

    private void onCameraOpen(final SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener){
        if (glSurfaceView == null) return;
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("onOpenSuccess");
                mCameraChanging = false;
                mCameraProxy.startPreview(onFrameAvailableListener);
            }
        });

    }

    public void changeCamera(int cameraId, Runnable onOpenSuccess) {
        LogUtils.d("switchCamera");
        if (null == glSurfaceView) return;
        if (Camera.getNumberOfCameras() == 1
                || mCameraChanging) {
            return;
        }
        mCameraID = cameraId;
        mCameraChanging = true;
        if (glSurfaceView == null) return;
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraProxy.changeCamera(mCameraID, new CameraListener() {
                    @Override
                    public void onOpenSuccess() {
                        if (glSurfaceView == null) return;
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.d("onOpenSuccess");
                                mCameraProxy.deleteTexture();
                                onCameraOpen(mFrameListener);
                                if (glSurfaceView != null) {
                                    glSurfaceView.requestRender();
                                }
                                if (onOpenSuccess != null) {
                                    onOpenSuccess.run();
                                }
                            }
                        });

                    }

                    @Override
                    public void onOpenFail() {
                        LogUtils.e("camera openFail!!");
                    }

                    @Override
                    public void onFrameArrived(TotalCaptureResult result) {
                        if (mFrameInfoListener != null) {
                            mFrameInfoListener.onFrameInfoArrivedListener(result);
                        }
                    }

                });

            }
        });
    }

    @Override
    public void update() {
        if (mCameraChanging)return;
        mCameraProxy.updateTexture();

    }

    @Override
    public void close() {
        mCameraProxy.releaseCamera();

    }

    public void setPreferSize(int width, int height) {
        mCameraProxy.setPreferSize(height, width);
    }

    @Override
    public float[] getFov() {
        return mCameraProxy.getFov();
    }

    @Override
    public float[] getUVMatrix() {
        return new BeMatrix4f().toFloatArray();
    }

    @Override
    public EffectsSDKEffectConstants.TextureFormat getTextureFormat() {
        return EffectsSDKEffectConstants.TextureFormat.Texture_Oes;
    }

    @Override
    public int getTexture() {
        return mCameraProxy.getPreviewTexture();
    }

    @Override
    public int getWidth() {
        return mCameraProxy.getPreviewWidth();
    }

    @Override
    public int getHeight() {
        return mCameraProxy.getPreviewHeight();
    }

    @Override
    public int getOrientation() {
        return mCameraProxy.getOrientation();
    }

    @Override
    public boolean isFront() {
        return mCameraProxy.isFrontCamera();
    }

    @Override
    public long getTimestamp() {
        return mCameraProxy.getTimeStamp();
    }

    @Override
    public boolean isReady() {
        return (mCameraProxy != null && mCameraProxy.isCameraValid() && !mCameraChanging);
    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return ImageView.ScaleType.CENTER_CROP;
    }

    public List<int[]> getSupportedPreviewSizes(){
        return mCameraProxy.getSupportPreviewSizes();
    }

    public int getCameraID() {
        return mCameraID;
    }

    public void setImageReaderAvailableListener(ImageReader.OnImageAvailableListener listener) {
        mImageListener = listener;
    }

    public boolean setBrust(int width, int height, ArrayList<Integer> aes, ImageReader.OnImageAvailableListener listener)
    {
        return mCameraProxy.brustImage(width, height, aes, listener);
    }

    @Override
    public float[] getIsoInfo() {
        return mCameraProxy.getIsoInfo();
    }

    public interface CameraFrameInfo{
        void onFrameInfoArrivedListener(TotalCaptureResult result);
    }
}
