package com.effectsar.labcv.core.algorithm;

import android.content.Context;
import android.os.Build;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefDistanceInfo;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.effectsdk.HumanDistance;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL;

public class HumanDistanceAlgorithmTask extends AlgorithmTask<HumanDistanceAlgorithmTask.HumanDistanceResourceProvider, BefDistanceInfo> {

    public static final AlgorithmTaskKey HUMAN_DISTANCE = AlgorithmTaskKeyFactory.create("humanDistance", true);
    // TODO: move to algorithm task, for global config
    public static final AlgorithmTaskKey HUMAN_DISTANCE_FRONT = AlgorithmTaskKeyFactory.create("humanDistanceFront");
    public static final AlgorithmTaskKey HUMAN_DISTANCE_FOV = AlgorithmTaskKeyFactory.create("algorithm_fov");

    public static final int FACE_DETECT_CONFIG = (EffectsSDKEffectConstants.FaceAction.BEF_FACE_DETECT |
            EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL);
    private final FaceDetect mFaceDetector;
    private final HumanDistance mDetector;
    private float mFov;
    private boolean mIsFront;

    public HumanDistanceAlgorithmTask(Context context, HumanDistanceResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new HumanDistance();
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.HUMAN_DISTANCE);
        int ret = mDetector.init(mContext, mResourceProvider.faceModel(),
                mResourceProvider.faceAttrModel(),
                mResourceProvider.humanDistanceModel(),
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHumanDistance", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL,
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefDistanceInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) {
            return null;
        }
        String deviceName = Build.MODEL;
        mDetector.setParam(EffectsSDKEffectConstants.HumanDistanceParamType.BEF_HumanDistanceCameraFov.getValue(), getFloatConfig(HUMAN_DISTANCE_FOV));
        LogTimerRecord.RECORD("humanDistance");
        BefDistanceInfo distanceInfo = mDetector.detectDistance(buffer, pixlFormat, width, height, stride, deviceName, getBoolConfig(HUMAN_DISTANCE_FRONT), rotation);
        LogTimerRecord.STOP("humanDistance");
        return distanceInfo;
    }

    @Override
    public int destroyTask() {
        mFaceDetector.release();
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return HUMAN_DISTANCE;
    }

    public interface HumanDistanceResourceProvider extends AlgorithmResourceProvider {

        String faceModel();

        String faceAttrModel();

        String humanDistanceModel();
    }
}
