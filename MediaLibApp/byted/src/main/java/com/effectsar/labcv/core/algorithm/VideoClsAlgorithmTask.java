package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefVideoClsInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.VideoClsDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class VideoClsAlgorithmTask extends AlgorithmTask<VideoClsAlgorithmTask.VideoClsResourceProvider, BefVideoClsInfo> {

    public static final AlgorithmTaskKey VIDEO_CLS = AlgorithmTaskKeyFactory.create("videoCls", true);
    public static final int FRAME_INTERVAL = 5;

    private final VideoClsDetect mDetector;
    private long mFrameCount = 0;

    public VideoClsAlgorithmTask(Context context, VideoClsResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new VideoClsDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.VIDEO_CLS);
        int ret = mDetector.init(EffectsSDKEffectConstants.VideoClsModelType.BEF_AI_kVideoClsModel1,
                mResourceProvider.videoClsModel(),
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initVideoCls", ret)) return ret;
        return ret;
    }

    @Override
    public BefVideoClsInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("videoCls");
        boolean isLast = (++mFrameCount % FRAME_INTERVAL) == 0;
        BefVideoClsInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, isLast, rotation);
        LogTimerRecord.STOP("videoCls");
        if (isLast) {
            return info;
        }
        return null;
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
        return VIDEO_CLS;
    }

    public interface VideoClsResourceProvider extends AlgorithmResourceProvider {
        String videoClsModel();
    }
}
