package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.effectsdk.BefFaceFeature;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.FaceVerify;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class FaceVerifyAlgorithmTask extends AlgorithmTask<FaceVerifyAlgorithmTask.FaceVerifyResourceProvider, Object> {

    public static final AlgorithmTaskKey FACE_VERIFY = AlgorithmTaskKeyFactory.create("faceVerify", true);

    private static final int MAX_FACE = 10;

    private final FaceVerify mDetector;
    public FaceVerifyAlgorithmTask(Context context, FaceVerifyResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new FaceVerify();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.FACE_VERIFY);
        int ret = mDetector.init(mContext, mResourceProvider.faceModel(),
                mResourceProvider.faceVerifyModel(), MAX_FACE, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFaceVerify", ret)) return ret;

        return ret;
    }

    @Override
    public Object process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        return new FaceVerifyCaptureResult(buffer, width, height);
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
        return FACE_VERIFY;
    }

    public BefFaceFeature extractFeatureSingle(ByteBuffer buffer, EffectsSDKEffectConstants.PixlFormat pixelFormat, int width, int height, int stride, EffectsSDKEffectConstants.Rotation rotation) {
        return mDetector.extractFeatureSingle(buffer, pixelFormat, width, height, stride, rotation);
    }

    public BefFaceFeature extractFeature(ByteBuffer buffer, EffectsSDKEffectConstants.PixlFormat pixelFormat, int width, int height, int stride, EffectsSDKEffectConstants.Rotation rotation) {
        return mDetector.extractFeature(buffer, pixelFormat, width, height, stride, rotation);
    }

    public double verify(float[] f1, float[] f2) {
        return mDetector.verify(f1, f2);
    }

    public double distToScore(double dist) {
        return mDetector.distToScore(dist);
    }

    public interface FaceVerifyResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
        String faceVerifyModel();
    }


    public static class FaceVerifyCaptureResult {
        public ByteBuffer buffer;
        public int width;
        public int height;

        public FaceVerifyCaptureResult(ByteBuffer buffer, int width, int height) {
            this.buffer = buffer;
            this.width = width;
            this.height = height;
        }
    }
}
