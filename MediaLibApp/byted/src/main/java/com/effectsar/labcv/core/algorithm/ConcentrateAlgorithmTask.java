package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL;

public class ConcentrateAlgorithmTask extends AlgorithmTask<ConcentrateAlgorithmTask.ConcentrateResourceProvider, ConcentrateAlgorithmTask.BefConcentrationInfo> {

    public static final AlgorithmTaskKey CONCENTRATION = AlgorithmTaskKeyFactory.create("concentration", true);
    public static final int INTERVAL = 1000;
    public static final float MIN_YAW = -14;
    public static final float MAX_YAW = 7;
    public static final float MIN_PITCH = -12;
    public static final float MAX_PITCH = 12;

    public static final int FACE_DETECT_CONFIG = (EffectsSDKEffectConstants.FaceAction.BEF_FACE_DETECT |
            EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL);
    private final FaceDetect mFaceDetector;
    private int mTotalCount;
    private int mConcentrationCount;
    private long mLastProcess;

    public ConcentrateAlgorithmTask(Context context, ConcentrateResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        mTotalCount = 0;
        mConcentrationCount = 0;
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.FACE);
        int ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;

        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefConcentrationInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("concentration");
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("concentration");
        if (faceInfo == null) {
            return null;
        }
        if ((System.currentTimeMillis() - mLastProcess) < INTERVAL) {
            return null;
        }
        mLastProcess = System.currentTimeMillis();
        if (faceInfo != null && faceInfo.getFace106s().length > 0) {
            BefFaceInfo.Face106 face106 = faceInfo.getFace106s()[0];
            boolean available = face106.getYaw() <= MAX_YAW && face106.getYaw() >= MIN_YAW && face106.getPitch() <= MAX_PITCH && face106.getPitch() >= MIN_PITCH;
            mTotalCount += 1;
            if (available) {
                mConcentrationCount += 1;
            }
        } else {
            mConcentrationCount = 0;
            mTotalCount = 0;
        }
        return new BefConcentrationInfo(mTotalCount, mConcentrationCount);
    }

    @Override
    public int destroyTask() {
        mTotalCount = 0;
        mConcentrationCount = 0;
        mFaceDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return CONCENTRATION;
    }

    public interface ConcentrateResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
    }

    public static class BefConcentrationInfo {
        public int total;
        public int concentration;

        public BefConcentrationInfo(int total, int concentration) {
            this.total = total;
            this.concentration = concentration;
        }
    }
}
