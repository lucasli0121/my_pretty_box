package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefHandInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.HandDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class HandAlgorithmTask extends AlgorithmTask<HandAlgorithmTask.HandResourceProvider, BefHandInfo> {
    public static final AlgorithmTaskKey HAND = AlgorithmTaskKeyFactory.create("hand", true);

    private static final int MAX_HAND = 1;
    private static final float ENLARGE_FACTOR = 2.f;
    private static final int NARUTO_GESTURE = 1;
    private static final int HAND_DETECT_DELAY_FRAME_COUNT = 0;

    private final HandDetect mDetector;
    private int mDetectConfig;

    public HandAlgorithmTask(Context context, HandResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new HandDetect();
        mDetectConfig = EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_DETECT.getValue() |
                EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_BOX_REG.getValue() |
                EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_GESTURE_CLS.getValue() |
                EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_KEY_POINT.getValue();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.HAND_DETECT);
        int ret = mDetector.createHandle(mContext, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHand", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_DETECT, mResourceProvider.handModel());
        if (!checkResult("initHand", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_BOX_REG, mResourceProvider.handBoxModel());
        if (!checkResult("initHand", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_GESTURE_CLS, mResourceProvider.handGestureModel());
        if (!checkResult("initHand", ret)) {
            mDetectConfig &= ~EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_GESTURE_CLS.getValue();
        }
        ret = mDetector.setModel(EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_KEY_POINT, mResourceProvider.handKeyPointModel());
        if (!checkResult("initHand", ret)) {
            mDetectConfig &= ~EffectsSDKEffectConstants.HandModelType.BEF_HAND_MODEL_KEY_POINT.getValue();
        }

        ret = mDetector.setParam(EffectsSDKEffectConstants.HandParamType.BEF_HAND_MAX_HAND_NUM, MAX_HAND);
        if (!checkResult("initHand", ret)) return ret;
        ret = mDetector.setParam(EffectsSDKEffectConstants.HandParamType.BEF_HNAD_ENLARGE_FACTOR_REG, ENLARGE_FACTOR);
        if (!checkResult("initHand", ret)) return ret;
        ret = mDetector.setParam(EffectsSDKEffectConstants.HandParamType.BEF_HAND_NARUTO_GESTUER, NARUTO_GESTURE);
        if (!checkResult("initHand", ret)) return ret;
        return ret;
    }

    @Override
    public BefHandInfo process(ByteBuffer buffer, int width, int height, int stride,
                                  EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectHand");
        BefHandInfo handInfo = mDetector.detectHand(buffer, pixlFormat,width, height,
                stride, rotation, mDetectConfig, HAND_DETECT_DELAY_FRAME_COUNT);
        LogTimerRecord.STOP("detectHand");
        return handInfo;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return HAND;
    }

    public interface HandResourceProvider extends AlgorithmResourceProvider {
        String handModel();
        String handBoxModel();
        String handGestureModel();
        String handKeyPointModel();
    }
}
