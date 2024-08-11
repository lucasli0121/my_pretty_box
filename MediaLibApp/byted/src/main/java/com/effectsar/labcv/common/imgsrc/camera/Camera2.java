package com.effectsar.labcv.common.imgsrc.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;


import com.effectsar.labcv.common.database.LocalParamManager;
import com.effectsar.labcv.core.util.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2 implements CameraInterface {
    private static final String TAG = "Camera2";
    private CameraManager manager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private CaptureRequest.Builder mPreviewBuilder;
    private CaptureRequest.Builder mStillCaptureBuilder;
    private int cameraRotate;
    private int sWidth;
    private int sHeight;
    private Size[] outputSizes;
    private CameraListener listener;
    private int currentCameraPosition = -1;
    private ArrayList<Surface> mPreviewSurfaces;
    private final HashMap<String, PointF> mViewAngleMap = new HashMap<>();
    private ImageReader mStillImageReader;
    private CameraCharacteristics mCharacteristics = null;

    private CameraCaptureSession mPhotoSession;
//    private CaptureRequest.Builder mPhotoBuilder;
    private ImageReader mPhotoImageReader;
    private ArrayList<CaptureRequest> mPhotoRequest;

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            if (listener != null) {
                listener.onOpenSuccess();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            close();
            resetCameraVariables();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            if (listener != null) {
                listener.onOpenFail();
                listener = null;
            }
            mCameraDevice = camera;
            close();
            resetCameraVariables();
        }
    };

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            if (listener != null) {
                listener.onFrameArrived(result);
            }
            recycle(result);
        }
    };

    //  {zh} 通过反射主动调用释放  {en} Active call release through reflection
    private Field sTargetField;
    private static Method sTargetMethod;

    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi"})
    void recycle(TotalCaptureResult result) {
        try {
            if (sTargetField == null) {
                sTargetField = result.getClass().getSuperclass().getDeclaredField("mResults");
                sTargetField.setAccessible(true);
            }
            if (sTargetMethod == null) {
                sTargetMethod = Class.forName("android.hardware.camera2.impl.CameraMetadataNative").getDeclaredMethod("finalize");
                sTargetMethod.setAccessible(true);
            }
            sTargetMethod.invoke(sTargetField.get(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private CameraCharacteristics cameraInfo;
    private CaptureRequest mPreviewRequest;

    @Override
    public void init(Context context) {
        if (manager == null) {
            manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            sHeight = 0;
            sWidth = 0;

            // Get all view angles
            try {
                for (final String cameraId : manager != null ? manager.getCameraIdList() : new String[0]) {
                    mCharacteristics = manager.getCameraCharacteristics(cameraId);
                    float[] maxFocus = mCharacteristics.get(
                            CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    SizeF size = mCharacteristics.get(
                            CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                    if (maxFocus == null || maxFocus.length <= 0 || size == null) {
                        continue;
                    }
                    mViewAngleMap.put(cameraId, new PointF(
                            (float) Math.toDegrees(2*Math.atan(size.getWidth()/(maxFocus[0]*2))),
                            (float) Math.toDegrees(2*Math.atan(size.getHeight()/(maxFocus[0]*2)))));

                    Range<Integer> range2 = mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                }
            } catch (CameraAccessException e) {
                throw new RuntimeException("Failed to get camera view angles", e);
            }
        }
    }

    public class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return -Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private void resetCameraVariables() {
        mCameraDevice = null;
        mPreviewBuilder = null;
        mPreviewSession = null;
        cameraInfo = null;
        mPreviewRequest = null;
    }

    @Override
    public boolean open(int position, CameraListener aListener) {
        this.listener = aListener;
        try {
            String[] cameraList = manager.getCameraIdList();
            if (position < 0 || position > 2) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onOpenFail();
                        }
                    }
                });
                return false;
            }
            // TODO: 2018/2/22
            if (position >= cameraList.length)
                position = CAMERA_FRONT;
            currentCameraPosition = position;
            String currentCameraId = cameraList[position];
            cameraInfo = manager.getCameraCharacteristics(currentCameraId);
            StreamConfigurationMap streamConfigurationMap = cameraInfo.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            cameraRotate = cameraInfo.get(CameraCharacteristics.SENSOR_ORIENTATION);

            outputSizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            LogUtils.e("outputSizes ="+outputSizes.length);
            getBestMatchCameraPreviewSize(outputSizes, new Size(sWidth, sHeight));


            manager.openCamera(currentCameraId, mStateCallback, mainHandler);
            return true;
        } catch (Throwable e) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onOpenFail();
                        listener = null;
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void enableTorch(boolean enable) {
        if (mPreviewBuilder == null || mPreviewSession == null) {
            return;
        }
        try {
            mPreviewBuilder.set(CaptureRequest.FLASH_MODE,
                    enable ? CameraMetadata.FLASH_MODE_TORCH : CameraMetadata.FLASH_MODE_OFF);
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            if (mPreviewSurfaces != null) {
                for (Surface surface:
                     mPreviewSurfaces) {
//                    surface.release();
                }
                mPreviewSurfaces = null;
            }
            if (mPhotoSession != null) {
                mPhotoSession.close();
                mPhotoSession = null;
            }
            if (mPhotoRequest != null) {
                mPhotoRequest = null;
            }
            if (mPhotoImageReader != null) {
                mPhotoImageReader.close();
                mPhotoImageReader = null;
            }
        } catch (Throwable e) {
            // Just ignore any errors
        }
        listener = null;
    }

    public void startPreview(List<Surface> surfaces) {
        if (null == mCameraDevice || surfaces == null || surfaces.size() == 0) {
            return;
        }

        try {

            //  {zh} 有TEMPLATE_RECORD TEMPLATE_PREVIEW 等模式进行选择    {en} There are TEMPLATE_RECORD TEMPLATE_PREVIEW and other modes to choose from
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            if (mPreviewSurfaces == null) {
                mPreviewSurfaces = new ArrayList<>();
            }

            mPreviewSurfaces.addAll(surfaces);
            for (Surface surface:
                    mPreviewSurfaces) {
                mPreviewBuilder.addTarget(surface);
            }

            mCameraDevice.createCaptureSession(mPreviewSurfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                }
            }, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void startImageReaderPreview() {
    }

    @Override
    public void addPreview(SurfaceView surface) {
        startPreview(Collections.singletonList(surface.getHolder().getSurface()));
    }

    public void captureImage(ImageReader.OnImageAvailableListener listener) {
        if (listener == null) return;
        mStillImageReader.setOnImageAvailableListener(listener, mainHandler);
    }

    /** {zh} 
     * 设置预览大小(通过surfaceTexture)
     */
    /** {en} 
     * Set preview size (by surfaceTexture)
     */

    @Override
    public void startPreview(@NonNull SurfaceTexture surfaceTexture) {
        surfaceTexture.setDefaultBufferSize(sWidth, sHeight);
        LogUtils.d("Preview size ="+sWidth + " "+sHeight);
        startPreview(List.of(new Surface(surfaceTexture)));
    }

    /**
     * Update the camera preview. {@link #startPreview(SurfaceTexture surfaceTexture)} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice || mPreviewBuilder == null) {
            return;
        }
        try {
            Range<Integer> fpsRanges = new Range<>(LocalParamManager.getInstance().getFrameRate(),LocalParamManager.getInstance().getFrameRate());
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRanges);
            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            mPreviewBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE);
            mPreviewRequest = mPreviewBuilder.build();
            mPreviewSession.setRepeatingRequest(mPreviewRequest, captureCallback, mainHandler);
        } catch (CameraAccessException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void updatePreviewWithImageReader() {
    }

    @Override
    public void changeCamera(int cameraPosition, CameraListener listener) {
        close();
        open(cameraPosition, listener);
    }

    @Override
    public int[] initCameraParam() {
        return new int[]{sWidth, sHeight};
    }

    @Override
    public int[] getPreviewWH() {
        return new int[]{sWidth, sHeight};
    }

    @Override
    public void setZoom(float scaleFactor) {

    }

    @Override
    public boolean isTorchSupported() {
        boolean flashAvailable = false;
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics("0");
            flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        } catch (CameraAccessException e) {
            // Just ignore
        }

        return flashAvailable;
    }

    @Override
    public void cancelAutoFocus() {
        if (!isMeteringAreaAFSupported() || mPreviewBuilder == null || mCameraDevice == null) {
            return;
        }
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);

        mPreviewRequest = mPreviewBuilder.build();
        try {
            mPreviewSession.setRepeatingRequest(mPreviewRequest, null, mainHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "setRepeatingRequest failed, errMsg: " + e.getMessage());
        }
    }

    @Override
    public boolean currentValid() {
        return mCameraDevice != null;
    }

    @Override
    public boolean setFocusAreas(View view, float[] pos, int rotation) {
        if (!isMeteringAreaAFSupported() || mPreviewBuilder == null || mCameraDevice == null) {
            return false;
        }
        float x = pos[0];
        float y = pos[1];
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        int realPreviewWidth = sWidth;
        int realPreviewHeight = sHeight;
        float tmp;

        if (90 == cameraRotate || 270 == cameraRotate) {
            realPreviewWidth = sHeight;
            realPreviewHeight = sWidth;
        }
        //    {zh} 计算摄像头取出的图像相对于view放大了多少，以及有多少偏移        {en} Calculate how much the image taken by the camera is enlarged relative to the view and how much offset  
        float imgScale = 1.0f, verticalOffset = 0, horizontalOffset = 0;
        if (realPreviewHeight * viewWidth > realPreviewWidth * viewHeight) {
            imgScale = viewWidth * 1.0f / realPreviewWidth;
            verticalOffset = (realPreviewHeight - viewHeight / imgScale) / 2;
        } else {
            imgScale = viewHeight * 1.0f / realPreviewHeight;
            horizontalOffset = (realPreviewWidth - viewWidth / imgScale) / 2;
        }
        //    {zh} 将点击的坐标转换为图像上的坐标        {en} Converts the coordinates clicked to the coordinates on the image  
        x = x / imgScale + horizontalOffset;
        y = y / imgScale + verticalOffset;
        if (90 == rotation) {
            tmp = x;
            x = y;
            y = sHeight - tmp;
        } else if (270 == rotation) {
            tmp = x;
            x = sWidth - y;
            y = tmp;
        }

        //    {zh} 计算取到的图像相对于裁剪区域的缩放系数，以及位移        {en} Calculate the scaling factor of the taken image relative to the cropped area, and the displacement  
        Rect cropRegion = mPreviewRequest.get(CaptureRequest.SCALER_CROP_REGION);
        if (null == cropRegion) {
            Log.e(TAG, "can't get crop region");
            cropRegion = new Rect(0, 0, 1, 1);
        }

        int cropWidth = cropRegion.width(), cropHeight = cropRegion.height();
        if (sHeight * cropWidth > sWidth * cropHeight) {
            imgScale = cropHeight * 1.0f / sHeight;
            verticalOffset = 0;
            horizontalOffset = (cropWidth - imgScale * sWidth) / 2;
        } else {
            imgScale = cropWidth * 1.0f / sWidth;
            horizontalOffset = 0;
            verticalOffset = (cropHeight - imgScale * sHeight) / 2;
        }

        //    {zh} 将点击区域相对于图像的坐标，转化为相对于成像区域的坐标        {en} Convert the coordinates of the click area relative to the image to the coordinates relative to the imaging area  
        x = x * imgScale + horizontalOffset + cropRegion.left;
        y = y * imgScale + verticalOffset + cropRegion.top;

        double tapAreaRatio = 0.1;
        Rect rect = new Rect();
        rect.left = clamp((int) (x - tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
        rect.right = clamp((int) (x + tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
        rect.top = clamp((int) (y - tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());
        rect.bottom = clamp((int) (y + tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());


        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        mPreviewRequest = mPreviewBuilder.build();
        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), captureCallback, mainHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "setRepeatingRequest failed, " + e.getMessage());
            return false;
        }
        return true;
    }

    private int clamp(int x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    @Override
    public List<int[]> getSupportedPreviewSizes() {
        List<int[]> retSizes = new ArrayList<>();
        for (Size size : outputSizes) {
            retSizes.add(new int[]{size.getWidth(), size.getHeight()});
        }

        return retSizes;
    }

    public static boolean isSupportPreviewSize(Context context, int width, int height) {
        boolean result = true;
        boolean checked = false;
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics cameraCharacteristics;
        try {
            for (final String cameraId : cameraManager != null ? cameraManager.getCameraIdList() : new String[0]) {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (streamConfigurationMap != null) {
                    Size[] sizeList = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                    boolean cameraSupport = false;
                    for (Size size : sizeList) {
                        if (size!=null && size.getHeight() == height && size.getWidth() == width) {
                            checked = true;
                            cameraSupport = true;
                            break;
                        }
                    }
                    result = result && cameraSupport;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return result && checked;
    }

    private void getBestMatchCameraPreviewSize(Size[] supports,Size prefer) {
        if (supports != null) {
            int exactWidth = -1, exactHeight = -1;
            int bestWidth = -1, bestHeight = -1;
            for (Size size : supports) {
                LogUtils.d("支持分辨率："+size.getWidth()+"  "+size.getHeight());
                int width = size.getWidth();
                int height = size.getHeight();

                if (width == prefer.getWidth() && height == prefer.getHeight()) {
                    exactHeight = height;
                    exactWidth = width;
                    break;
                }
                if (width * height < sWidth* sHeight && width*height > bestWidth*bestHeight ){
                    bestWidth = width;
                    bestHeight = height;

                }
            }
            if (exactHeight != -1) {
                sWidth = exactWidth;
                sHeight = exactHeight;
            } else {
                sWidth = bestWidth;
                sHeight = bestHeight;
            }
        }
    }

    //  {zh} 找到最接近的大小  {en} Find the closest size
    //  {zh} 这里夜景找最大的,当ret 小的时候，返回prefer,然后看camera hal能返回的大小  {en} Find the largest night scene here, when the ret is small, return preferred, and then see the size that the camera hal can return
    private Size getClosestCameraPreviewSize(Size[] supports, Size prefer) {
        Size ret = supports[0];
        for (Size s: supports) {
            if (s.getHeight() >= prefer.getHeight() && s.getWidth() >= prefer.getWidth()) {
                ret = s;
            }
        }
        if (prefer.getWidth() > ret.getWidth() && prefer.getHeight() > ret.getHeight()) {
            return prefer;
        }
        return ret;
    }

    private boolean isMeteringAreaAFSupported() {
        boolean result = false;
        if (cameraInfo != null) {
            result = cameraInfo.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
        } else {
            CameraCharacteristics cameraCharacteristics = null;
            try {
                cameraCharacteristics = manager.getCameraCharacteristics("0");
                result = cameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public int getCameraPosition() {
        return currentCameraPosition;
    }

    @Override
    public boolean isVideoStabilizationSupported() {
        int[] list = null;
        if (cameraInfo != null) {
            list = cameraInfo.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
            return list != null && list.length > 0;
        }
        return false;
    }

    @Override
    public boolean setVideoStabilization(boolean toggle) {
        if (isVideoStabilizationSupported() && mPreviewBuilder != null) {
            mPreviewBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, toggle ?
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON : CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF);
            mPreviewRequest = mPreviewBuilder.build();
            try {
                mPreviewSession.setRepeatingRequest(mPreviewRequest, null, mainHandler);
                return true;
            } catch (CameraAccessException e) {
                Log.e(TAG, "setRepeatingRequest failed, errMsg: " + e.getMessage());
            }
        }
        return false;
    }


    @Override
    public int getOrientation() {
        if (cameraInfo == null) return 0;
        return cameraInfo.get(CameraCharacteristics.SENSOR_ORIENTATION);
    }

    @Override
    public boolean isFlipHorizontal() {
        if (cameraInfo == null) return true;
        int cameraId = cameraInfo.get(CameraCharacteristics.LENS_FACING);
        return cameraId == CameraCharacteristics.LENS_FACING_FRONT;
    }


    private float getHorizontalViewAngle() {
        if (mCameraDevice == null) {
            return 0f;
        }
        PointF angles = mViewAngleMap.get(mCameraDevice.getId());
        return angles != null ? angles.x : 0f;
    }

    private float getVerticalViewAngle() {
        if (mCameraDevice == null) {
            return 0f;
        }
        PointF angles = mViewAngleMap.get(mCameraDevice.getId());
        return angles != null ? angles.y : 0f;
    }

    @Override
    public float[] getFov() {
        float[] fov = new float[2];
        fov[0] = getHorizontalViewAngle();
        fov[1] = getVerticalViewAngle();
        return fov;
    }

    @Override
    public void setPreferSize(SurfaceTexture texture, int height, int width) {
        if (width == sWidth && height == sHeight){
            return;
        }

        sWidth = width;
        sHeight = height;

    }

    @Override
    public void stopPreview() {
        try {
            LogUtils.d("stopPreview");
            mPreviewSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //  {zh} 设备问题合集  {en} Equipment problem collection
    //  {zh} 1 pixel4 root map.getOutputSizes(ImageFormat.YUV_420_888) 返回值会非常小，这里我们使用原始的请求分辨率来完成，而不用得到的  {en} 1 pixel4 root map.get OutputSizes (ImageFormat.YUV_420_888) return value will be very small, here we use the original request resolution to complete, instead of getting
    // {zh} 2 华为 mat30 pro map.getOutputSizes(ImageFormat.YUV_420_888) 是空的 {en} 2 Huawei mat30 pro map.get OutputSizes (ImageFormat.YUV_420_888) is empty

    public boolean captureBurst(int width, int height, List<Integer> evs, ImageReader.OnImageAvailableListener listener) {
        if (mPhotoImageReader != null) {
            mPhotoImageReader.close();
            mPhotoImageReader = null;
        }

        // new surface find the most support size
        try  {
            StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(ImageFormat.YUV_420_888);
            ArrayList<Size> suitableSize = new ArrayList();

            if (sizes != null) {
                for (Size s: sizes  ){
                    if (s.getWidth() % 8 == 0 && s.getHeight() % 8 == 0) {
                        suitableSize.add(new Size(s.getWidth(), s.getHeight()));
                    }
                }
                Collections.sort(suitableSize, new CompareSizesByArea());
                Size s = getClosestCameraPreviewSize(suitableSize.toArray(new Size[suitableSize.size()]), new Size(width ,height));
                width = s.getWidth();
                height = s.getHeight();
                sWidth = width;
                sHeight = height;
            }

            mPhotoImageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2);
            sWidth = width;
            sHeight = height;
            mPhotoImageReader.setOnImageAvailableListener(listener, mainHandler);

        }catch (Exception e) {
            e.printStackTrace();
            StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(ImageFormat.YUV_420_888);

            ArrayList<Size> suitableSize = new ArrayList();
            for (int[] s: getSupportedPreviewSizes()  ){
                if (s[0] % 8 == 0 && s[1] % 8 == 0) {
                    suitableSize.add(new Size(s[0], s[1]));
                }
            }
            if (sizes != null) {
                getBestMatchCameraPreviewSize(suitableSize.toArray(new Size[suitableSize.size()]), new Size(width ,height));
            }
            mPhotoImageReader = ImageReader.newInstance(sWidth, sHeight, ImageFormat.YUV_420_888, 2);
            mPhotoImageReader.setOnImageAvailableListener(listener, mainHandler);
            return false;
        }

        Range<Integer> aeRange = mCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        Rational rational = mCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);

        //mutil capture request
        mPhotoRequest = new ArrayList<>(evs.size());
        for (Integer ev:evs) {
            try {
                CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                double ae = ev / rational.doubleValue();
                if (ae < aeRange.getLower()) {
                    ae = aeRange.getLower();
                } else if (ae > aeRange.getUpper()) {
                    ae = aeRange.getUpper();
                }
                builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf((int) ae));
                builder.set(CaptureRequest.CONTROL_AE_LOCK, true);
                builder.addTarget(mPhotoImageReader.getSurface());
                mPhotoRequest.add(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("can't not change the param " + e);
            }
         }
        if (mPhotoSession != null) {
            mPhotoSession.close();
            mPhotoSession = null;
        }

        // add surface and create capture session
        mPreviewSurfaces.add(mPhotoImageReader.getSurface());
//        mCameraDevice.close();

        CameraCaptureSession.CaptureCallback callback = new CameraCaptureSession.CaptureCallback() {

            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                Integer aeExposure = result.get(TotalCaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION);
                Integer iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                Long exposureTime = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                LogUtils.e("onCaptureCompleted, aeExposure: " + aeExposure + ", iso: " + iso + ", exposureTime: " + exposureTime);
            }
        };

        try {
            mCameraDevice.createCaptureSession(mPreviewSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPhotoSession = cameraCaptureSession;
                    try {
                        mPhotoSession.captureBurst(mPhotoRequest, callback, mainHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    LogUtils.e("onConfigureFailed");
                }
            }, mainHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public float[] getIsoInfo() {
        Range<Integer> range2 = mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        if (range2 == null) {
            return new float[] {0, 0};
        }
        int max1 = range2.getUpper();//10000
        int min1 = range2.getLower();//100
        return new float[] {max1, min1};
    }
}
