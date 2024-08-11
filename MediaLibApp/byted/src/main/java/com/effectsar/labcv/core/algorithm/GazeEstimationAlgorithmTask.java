package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.BefGazeEstimationInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.effectsdk.GazeEstimation;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL;

public class GazeEstimationAlgorithmTask extends AlgorithmTask<GazeEstimationAlgorithmTask.GazeEstimationResourceProvider, BefGazeEstimationInfo> {
    public static final AlgorithmTaskKey GAZE_ESTIMATION = AlgorithmTaskKeyFactory.create("gazeEstimation", true);
    public static final AlgorithmTaskKey GAZE_ESTIMATION_FOV = AlgorithmTaskKeyFactory.create("algorithm_fov");

    public static final int LINE_LEN = 0;

    public static final int FACE_DETECT_CONFIG = (EffectsSDKEffectConstants.FaceAction.BEF_FACE_DETECT |
            EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL);

    private final FaceDetect mFaceDetector;
    private final GazeEstimation mDetector;

    public GazeEstimationAlgorithmTask(Context context, GazeEstimationResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new GazeEstimation();
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.GAZE_ESTIMATION);
        int ret = mDetector.init(licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("init gaze", ret)) return ret;


        ret = mDetector.setModel(EffectsSDKEffectConstants.GazeEstimationModelType.BEF_GAZE_ESTIMATION_MODEL1, mResourceProvider.gazeEstimationModel());
        if (!checkResult("init gaze", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefGazeEstimationInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) {
            return null;
        }
        BefGazeEstimationInfo info = new BefGazeEstimationInfo();
        if (faceInfo != null && faceInfo.getFace106s().length > 0) {
            mDetector.setParam(EffectsSDKEffectConstants.GazeEstimationParamType.BEF_GAZE_ESTIMATION_CAMERA_FOV, getFloatConfig(GAZE_ESTIMATION_FOV));
            LogTimerRecord.RECORD("gazeEstimation");
            info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation, faceInfo, LINE_LEN);
            LogTimerRecord.STOP("gazeEstimation");
        }
        return info;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        mFaceDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return GAZE_ESTIMATION;
    }

    public interface GazeEstimationResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
        String gazeEstimationModel();
    }
}
