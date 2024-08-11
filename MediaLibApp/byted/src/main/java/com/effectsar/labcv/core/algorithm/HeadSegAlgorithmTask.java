package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.BefHeadSegInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceDetect;
import com.effectsar.labcv.effectsdk.HeadSegment;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL;

public class HeadSegAlgorithmTask extends AlgorithmTask<HeadSegAlgorithmTask.HeadSegResourceProvider, BefHeadSegInfo> {
    public static final AlgorithmTaskKey HEAD_SEGMENT = AlgorithmTaskKeyFactory.create("headSeg", true);

    public static final int FACE_DETECT_CONFIG = (EffectsSDKEffectConstants.FaceAction.BEF_FACE_DETECT |
            EffectsSDKEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            EffectsSDKEffectConstants.FaceAction.BEF_DETECT_FULL);

    private final HeadSegment mHeadSegDetector;
    private final FaceDetect mFaceDetector;

    public HeadSegAlgorithmTask(Context context, HeadSegResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mFaceDetector = new FaceDetect();
        mHeadSegDetector = new HeadSegment();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.HEAD_SEG);
        int ret = mHeadSegDetector.init(mContext, mResourceProvider.headSegModel(), licensePath,
                            mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHeadSegment", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefHeadSegInfo process(ByteBuffer buffer, int width, int height, int stride,
                                  EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) {
            return null;
        }
        LogTimerRecord.RECORD("headSegment");
        BefHeadSegInfo segInfo = mHeadSegDetector.process(buffer, pixlFormat, width, height,
                stride, rotation, faceInfo.getFace106s());
        LogTimerRecord.STOP("headSegment");
        return segInfo;
    }

    @Override
    public int destroyTask() {
        mFaceDetector.release();
        mHeadSegDetector.release();
        return 0;
    }

    @Override
    public AlgorithmTaskKey key() {
        return HEAD_SEGMENT;
    }

    @Override
    public int[] preferBufferSize() {
        return null;
    }

    public interface HeadSegResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
        String headSegModel();
    }
}
