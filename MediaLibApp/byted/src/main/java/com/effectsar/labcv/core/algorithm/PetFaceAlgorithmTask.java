package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefPetFaceInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.PetFaceDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.PetFaceDetectConfig.BEF_PET_FACE_DETECT_CAT;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.PetFaceDetectConfig.BEF_PET_FACE_DETECT_DOG;

public class PetFaceAlgorithmTask extends AlgorithmTask<PetFaceAlgorithmTask.PetFaceResourceProvider, BefPetFaceInfo> {

    public static final AlgorithmTaskKey PET_FACE = AlgorithmTaskKeyFactory.create("petFace", true);

    public static final int DETECT_CONFIG = BEF_PET_FACE_DETECT_CAT | BEF_PET_FACE_DETECT_DOG;

    private final PetFaceDetect mDetector;

    public PetFaceAlgorithmTask(Context context, PetFaceResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new PetFaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.PET_FACE);
        int ret = mDetector.init(mContext, mResourceProvider.petFaceModel(), DETECT_CONFIG,
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initPetFace", ret)) return ret;

        return ret;
    }

    @Override
    public BefPetFaceInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("petFace");
        BefPetFaceInfo petFaceInfo = mDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("petFace");
        return petFaceInfo;
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
        return PET_FACE;
    }

    public interface PetFaceResourceProvider extends AlgorithmResourceProvider {
        String petFaceModel();
    }
}
