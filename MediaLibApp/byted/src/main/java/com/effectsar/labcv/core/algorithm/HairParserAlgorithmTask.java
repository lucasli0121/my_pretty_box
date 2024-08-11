package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.HairParser;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class HairParserAlgorithmTask extends AlgorithmTask<HairParserAlgorithmTask.HairParserResourceProvider, HairParser.HairMask> {
    public static final AlgorithmTaskKey HAIR_PARSER = AlgorithmTaskKeyFactory.create("hairParser", true);

    public static final boolean FLIP_ALPHA = false;

    private final HairParser mDetector;

    public HairParserAlgorithmTask(Context context, HairParserResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new HairParser();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.HAIR_PARSE);
        int ret = mDetector.init(mContext, mResourceProvider.hairParserModel(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHairParser", ret)) return ret;

        ret = mDetector.setParam(preferBufferSize()[0], preferBufferSize()[1], true, true);
        if (!checkResult("initHairParser", ret)) return ret;
        return ret;
    }

    @Override
    public HairParser.HairMask process(ByteBuffer buffer, int width, int height,
                                       int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat,
                                       EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("parseHair");
        HairParser.HairMask hairMask = mDetector.parseHair(buffer, pixlFormat, width, height, stride,
                rotation, FLIP_ALPHA);
        LogTimerRecord.STOP("parseHair");
        return hairMask;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[] {128, 224};
    }

    @Override
    public AlgorithmTaskKey key() {
        return HAIR_PARSER;
    }

    public interface HairParserResourceProvider extends AlgorithmResourceProvider {
        String hairParserModel();
    }
}
