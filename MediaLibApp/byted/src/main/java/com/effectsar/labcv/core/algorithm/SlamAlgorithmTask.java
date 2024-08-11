package com.effectsar.labcv.core.algorithm;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.SensorManager;
import com.effectsar.labcv.core.util.TimestampUtil;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefSlamInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.SlamDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class SlamAlgorithmTask  extends AlgorithmTask<SlamAlgorithmTask.SlamResourceProvider, SlamAlgorithmTask.SlamRenderInfo> implements SensorManager.OnSensorChangedListener {
    private SlamDetect mDetector = null;
    private SensorManager mSensorManager = null;
    private BefSlamInfo.SlamCameraInfo mSlamCameraInfo;
    private final EffectsSDKEffectConstants.SlamVersion mVersion = EffectsSDKEffectConstants.SlamVersion.BEF_AI_SLAM_HorizontalPlaneTracking;
    private final BefSlamInfo.SlamClickFlag mSlamClickFlag;
    private boolean hasUpdateDelta = false;
    private long imuDeltaTime = 0;
    private long mImageTimestamp = 0;
    private BefSlamInfo.SlamCameraIntrinsic mSlamCameraIntrinsic = null;
    private SlamRenderInfo slamRenderInfo;

    public static final AlgorithmTaskKey SLAM  = AlgorithmTaskKey.createKey("slam", true);
    public static final AlgorithmTaskKey SLAM_REGION_TRACKING = AlgorithmTaskKey.createKey("slam_region_tracking", true);
    public static final AlgorithmTaskKey SLAM_WORLD_CORD = AlgorithmTaskKey.createKey("slam_world_cord", true);
    public static final AlgorithmTaskKey SLAM_FEATURE_POINTS = AlgorithmTaskKey.createKey("slam_feature_points", true);


    public static final AlgorithmTaskKey SLAM_CLICK_FLAG = AlgorithmTaskKey.createKey("slam_click");

    public SlamAlgorithmTask(Context context, SlamResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mSlamClickFlag = new BefSlamInfo.SlamClickFlag();
        mSlamClickFlag.isClicked = 0;

    }

    @Override
    public int initTask() {
        // start sensor manager
        mSensorManager = new SensorManager(this);
        mSensorManager.startSlamSensor(mContext);
        mDetector = new SlamDetect();
        BefSlamInfo.SlamImuInfo imuInfo = new BefSlamInfo.SlamImuInfo();
        {
            imuInfo.setHasAccelerometer(mSensorManager.hasAccelerator()?1:0);
            imuInfo.setHasGravity(mSensorManager.hasGravity()?1:0);
            imuInfo.setHasGyroscope(mSensorManager.hasGyroscope()?1:0);
            imuInfo.setHasOrientation(mSensorManager.hasOrientation()?1:0);
        }

        mSlamCameraInfo = new BefSlamInfo.SlamCameraInfo();

        mDetector.initCameraInfo(mSlamCameraInfo);
        String deviceName = Build.MODEL;

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.SLAM);
        mSlamCameraInfo.color = EffectsSDKEffectConstants.SlamImageColor.BEF_AI_SLAM_RGB.getValue();
        mSlamCameraInfo.orienation = EffectsSDKEffectConstants.SlamDeviceOrientation.BEF_AI_SLAM_Portrait.getValue();
        mSlamCameraInfo.isFront = 0;
        mSlamCameraInfo.resolution = EffectsSDKEffectConstants.SlamResolution.BEF_AI_SLAM_480P.getValue();
        mSlamCameraInfo.easyInit = 0;

        int ret = mDetector.init(mResourceProvider.slamModel(), deviceName, imuInfo, mSlamCameraInfo, mVersion);
        if (!checkResult("initSlam", ret)) return ret;
        if (mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.OFFLINE_LICENSE) {
            ret = mDetector.checklicense(mContext, licensePath, false);
        } else {
            ret = mDetector.checklicense(mContext, licensePath, true);
        }
        if (!checkResult("slamCheckLicense", ret)) return ret;

        initRenderInfo();
        mSlamCameraIntrinsic = null;
        return 0;
    }

    @Override
    public SlamRenderInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogUtils.e("current process api without timestamp is not implemented! please use the other process api");
        return null;
    }

    public SlamRenderInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation, long timeStamp) {
        LogTimerRecord.RECORD("slam");
        if (mSlamCameraIntrinsic == null) {
            mSlamCameraIntrinsic =  mDetector.getCameraIntrinsic(Build.MODEL, mResourceProvider.slamParam(), width, height);
        }

        mImageTimestamp = timeStamp;
        if (mImageTimestamp == 0){
            return slamRenderInfo;
        }
        mImageTimestamp = TimestampUtil.convertImageTimestamp(mImageTimestamp, 0, TimestampUtil.TIMESTAMP_UNKNOWN, imuDeltaTime);

        BefSlamInfo.SlamPose cameraPose =  mDetector.slamDetect(buffer, width, height, stride, stride / width,  EffectsSDKEffectConstants.SlamDeviceOrientation.values()[rotation.id], (double)mImageTimestamp /1.e9, mSlamClickFlag);
//        LogUtils.e("tracking state"+cameraPose.getTrackingState());
//        LogUtils.e(cameraPose.toString() + "cameraPose");

        BefSlamInfo retInfo = new BefSlamInfo();
        retInfo.cameraPose = cameraPose;
        retInfo.intrinsic = mSlamCameraIntrinsic;
        retInfo.featurePoints = mDetector.getFeaturePoints();
        if (mSlamClickFlag.isClicked > 0) {
            // get the CameraIntrinsic
            BefSlamInfo.SlamPose planePose =  mDetector.getPlanePose(cameraPose, 1, mSlamClickFlag);

//            LogUtils.d(planePose.toString() + "plane pose");
            retInfo.isClicked = true;
            retInfo.planePose = planePose;
            mSlamClickFlag.isClicked = 0;
        }
        LogTimerRecord.STOP("slam");
        if (slamRenderInfo != null) {
            slamRenderInfo.slamInfo = retInfo;
        }

        return slamRenderInfo;
    }

    @Override
    public int destroyTask() {
        if (mDetector != null) {
            mDetector.destory();
            mDetector = null;
        }
        if (mSensorManager != null) {
            mSensorManager.stopSlamSensor(mContext);
        }
        if (slamRenderInfo != null) {
            slamRenderInfo = null;
        }
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[]{1280, 720};
    }

    @Override
    public AlgorithmTaskKey key() {
        return SLAM;
    }


    @Override
    public void onAcceleratorChanged(double dx, double dy, double dz, double timeStamp) {
        if (mDetector != null) {
            BefSlamInfo.SlamImuData data = new BefSlamInfo.SlamImuData();
            data.x = dx;
            data.y = dy;
            data.z = dz;
            data.timeStamp = timeStamp;



//            LogUtils.i("Set Accelerator "+dx + " "+dy+ " "+dz+ " "+timeStamp);
            mDetector.setImuData(EffectsSDKEffectConstants.SlamImuDataType.BEF_AI_SLAM_IMU_ACCELEROMETER, data);
        }
    }

    @Override
    public void onGravityChanged(double dx, double dy, double dz, double timeStamp) {
        if (mDetector != null) {
            BefSlamInfo.SlamImuData data = new BefSlamInfo.SlamImuData();
            data.x = dx;
            data.y = dy;
            data.z = dz;
            data.timeStamp = timeStamp;
//            LogUtils.i("Set gravity "+dx + " "+dy+ " "+dz+ " "+timeStamp);
            mDetector.setImuData(EffectsSDKEffectConstants.SlamImuDataType.BEF_AI_SLAM_IMU_GRAVITY, data);
        }
    }

    @Override
    public void onGryoscopChanged(double dx, double dy, double dz, double timeStamp) {
        if (mDetector != null) {
            BefSlamInfo.SlamImuData data = new BefSlamInfo.SlamImuData();
            data.x = dx;
            data.y = dy;
            data.z = dz;
            data.timeStamp = timeStamp;

            if (!hasUpdateDelta) {
                imuDeltaTime = TimestampUtil.getDelta((long) ((long)timeStamp * (1.e9)));
                hasUpdateDelta = true;
            }

//            LogUtils.i("Set Gryoscop "+dx + " "+dy+ " "+dz+ " "+timeStamp);
            mDetector.setImuData(EffectsSDKEffectConstants.SlamImuDataType.BEF_AI_SLAM_IMU_GYROSCOPE, data);
        }
    }

    @Override
    public void onRotationVectorChanged(double[] d, double timeStamp) {
        if (mDetector != null) {
//            LogUtils.i("Set rotation: " + timeStamp + d.toString());
            mDetector.setRotationVector(d, timeStamp);
        }
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);
        if (key.getKey() == SLAM_CLICK_FLAG.getKey()) {
            if (p instanceof MotionEvent) {
                mSlamClickFlag.setX(((MotionEvent) p).getX());
                mSlamClickFlag.setY(((MotionEvent) p).getY());
                mSlamClickFlag.setIsClicked(1);
            }
        }
        if (key.getKey() == SLAM_REGION_TRACKING.getKey()) {
//            mDetector.resetStatues();
            if (getBoolConfig(SLAM_REGION_TRACKING)) {
                mDetector.setVersion(EffectsSDKEffectConstants.SlamVersion.BEF_AI_SLAM_RegionTracking);
            } else {
                mDetector.setVersion(EffectsSDKEffectConstants.SlamVersion.BEF_AI_SLAM_HorizontalPlaneTracking);
            }
        }

        if (slamRenderInfo != null) {
            slamRenderInfo.drawWorldCord = getBoolConfig(SLAM_WORLD_CORD);
            slamRenderInfo.drawPointsCloud = getBoolConfig(SLAM_FEATURE_POINTS);
        }


    }

    private void initRenderInfo(){
        if (slamRenderInfo == null) {
            slamRenderInfo = new SlamRenderInfo();
            slamRenderInfo.planeTexturePath = "planeTexture.png";
            slamRenderInfo.objectPath = "duck2021/duck.obj";
            slamRenderInfo.objectTexturePath = "duck2021/00_ytem_1621854935.png";
        }
    }
    public interface SlamResourceProvider extends AlgorithmResourceProvider {
        String slamModel();
        String slamParam();
    }


    public class SlamRenderInfo {
        public String objectPath;
        public String objectTexturePath;
        public String planeTexturePath;
        public BefSlamInfo slamInfo;
        public boolean drawWorldCord;
        public boolean drawPointsCloud;
    }

}

