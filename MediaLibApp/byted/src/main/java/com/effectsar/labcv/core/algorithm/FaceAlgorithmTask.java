package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_IMAGE_SLOW;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_AGE;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_ATTRACTIVE;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_CONFUSE;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_EXPRESSION;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_GENDER;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_HAPPINESS;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceExtraModel.BEF_MOBILE_FACE_240_DETECT;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceExtraModel.BEF_MOBILE_FACE_280_DETECT;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceSegmentConfig.BEFF_MOBILE_FACE_REST_MASK;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceSegmentConfig.BEF_MOBILE_FACE_MOUTH_MASK;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceSegmentConfig.BEF_MOBILE_FACE_TEETH_MASK;

public class FaceAlgorithmTask extends AlgorithmTask<FaceAlgorithmTask.FaceResourceProvider, BefFaceInfo> {
    public static final AlgorithmTaskKey FACE = AlgorithmTaskKey.createKey("face", true);
    public static final AlgorithmTaskKey FACE_280 = AlgorithmTaskKey.createKey("face280");
    public static final AlgorithmTaskKey FACE_ATTR = AlgorithmTaskKey.createKey("faceAttr");
    public static final AlgorithmTaskKey FACE_MASK = AlgorithmTaskKey.createKey("faceMask");
    public static final AlgorithmTaskKey MOUTH_MASK = AlgorithmTaskKey.createKey("mouthMask");
    public static final AlgorithmTaskKey TEETH_MASK = AlgorithmTaskKey.createKey("teethMask");

    private final FaceDetect mDetector;

    public FaceAlgorithmTask(Context context, FaceResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.FACE);
        int ret = mDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL | BEF_DETECT_MODE_IMAGE_SLOW, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        ret = mDetector.initExtra(mContext, mResourceProvider.faceExtraModel(), BEF_MOBILE_FACE_280_DETECT);
        if (!checkResult("initFaceExtra", ret)) return ret;

        ret = mDetector.initAttri(mContext, mResourceProvider.faceAttrModel(), licensePath,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFaceAttr", ret)) {return ret;}


        return ret;
    }

    @Override
    public BefFaceInfo process(ByteBuffer buffer, int width, int height, int stride,
                               EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectFace");
        BefFaceInfo faceInfo = mDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectFace");
        if (getBoolConfig(FACE_MASK)) {
            LogTimerRecord.RECORD("detectFaceMask");
            mDetector.getFaceMask(faceInfo, EffectsSDKEffectConstants.FaceSegmentType.BEF_FACE_FACE_MASK);
            LogTimerRecord.STOP("detectFaceMask");
        }
        if (getBoolConfig(MOUTH_MASK)) {
            LogTimerRecord.RECORD("detectMouthMask");
            mDetector.getFaceMask(faceInfo, EffectsSDKEffectConstants.FaceSegmentType.BEF_FACE_MOUTH_MASK);
            LogTimerRecord.STOP("detectMouthMask");
        }
        if (getBoolConfig(TEETH_MASK)) {
            LogTimerRecord.RECORD("detectTeethMask");
            mDetector.getFaceMask(faceInfo, EffectsSDKEffectConstants.FaceSegmentType.BEF_FACE_TEETH_MASK);
            LogTimerRecord.STOP("detectTeethMask");
        }
        return faceInfo;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);

        int detectConfig = (EffectsSDKEffectConstants.FaceAction.BEF_FACE_DETECT |
                EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
                EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL);
        if (getBoolConfig(FACE_280)) {
            detectConfig |= BEF_MOBILE_FACE_280_DETECT;
        }

        if (getBoolConfig(FACE_MASK)) {
            detectConfig |= BEF_MOBILE_FACE_240_DETECT | BEFF_MOBILE_FACE_REST_MASK;
        }
        if (getBoolConfig(MOUTH_MASK)) {
            detectConfig |= BEF_MOBILE_FACE_240_DETECT | BEF_MOBILE_FACE_MOUTH_MASK;
        }
        if (getBoolConfig(TEETH_MASK)) {
            detectConfig |= BEF_MOBILE_FACE_240_DETECT | BEF_MOBILE_FACE_TEETH_MASK;
        }

        mDetector.setFaceDetectConfig(detectConfig);

        int attrConfig = 0;
        if (getBoolConfig(FACE_ATTR)) {
            attrConfig |= (BEF_FACE_ATTRIBUTE_EXPRESSION | BEF_FACE_ATTRIBUTE_HAPPINESS |
                    BEF_FACE_ATTRIBUTE_AGE | BEF_FACE_ATTRIBUTE_GENDER | BEF_FACE_ATTRIBUTE_ATTRACTIVE |
                    BEF_FACE_ATTRIBUTE_CONFUSE);
        }
        mDetector.setAttriDetectConfig(attrConfig);
    }

    @Override
    public int[] preferBufferSize() {
        if (getBoolConfig(FACE_ATTR) || getBoolConfig(FACE_280) || getBoolConfig(FACE_MASK)) {
            return new int[]{360, 640};
        } else {
            return new int[]{128, 224};
        }
    }

    @Override
    public AlgorithmTaskKey key() {
        return FACE;
    }

    public interface FaceResourceProvider extends AlgorithmResourceProvider {
        String faceModel();

        String faceExtraModel();

        String faceAttrModel();
    }
}
