package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.effectsdk.FaceFitting;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_IMAGE_SLOW;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_FACE_DETECT;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceExtraModel.BEF_MOBILE_FACE_280_DETECT;

public class FaceFittingAlgorithmTask extends AlgorithmTask<FaceFittingAlgorithmTask.FaceFittingResourceProvider, FaceFitting.FaceFittingResult> {
    public static final AlgorithmTaskKey FACE_FITTING = AlgorithmTaskKeyFactory.create("faceFitting", true);

    private final FaceFitting mDetector;
    private final FaceDetect mFaceDetector;

    public FaceFittingAlgorithmTask(Context context, FaceFittingResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new FaceFitting();
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.FACEFITTING);

        int ret = mDetector.init(mContext, mResourceProvider.faceFittingModel(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFaceFitting", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL | BEF_DETECT_MODE_IMAGE_SLOW, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        ret = mFaceDetector.initExtra(mContext, mResourceProvider.faceExtraModel(), BEF_MOBILE_FACE_280_DETECT);
        if (!checkResult("initFaceExtra", ret)) return ret;

        mFaceDetector.setFaceDetectConfig(BEF_DETECT_SMALL_MODEL | BEF_FACE_DETECT | BEF_DETECT_MODE_VIDEO|BEF_MOBILE_FACE_280_DETECT);

        return ret;
    }

    @Override
    public FaceFitting.FaceFittingResult process(ByteBuffer buffer, int width, int height,
                                               int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {

        LogTimerRecord.RECORD("faceFitting");
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) return null;
        mDetector.detect(faceInfo, width, height, new float[] {height, width / 2, height/ 2});
        LogTimerRecord.STOP("faceFitting");
        return mDetector.getFaceFittingResult();
    }

    @Override
    public int destroyTask() {
        mFaceDetector.release();
        mDetector.destroy();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return FACE_FITTING;
    }

    public interface FaceFittingResourceProvider extends AlgorithmResourceProvider {
        String faceFittingModel();
        String faceModel();
        String faceExtraModel();
    }
}
