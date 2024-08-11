package com.effectsar.labcv.core.algorithm;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.effectsar.labcv.R;
import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.lens.util.ImageQualityUtil;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.SaliencyMatting;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class SaliencyMattingAlgorithmTask extends AlgorithmTask<SaliencyMattingAlgorithmTask.SaliencyMattingResourceProvider, SaliencyMatting.MattingMask> {

    public static final AlgorithmTaskKey SALIENCY_MATTING = AlgorithmTaskKeyFactory.create("saliencyMatting", true);
    public static final AlgorithmTaskKey SALIENCY_MATTING_SMALL_MODEL = AlgorithmTaskKeyFactory.create("saliencyMattingSmallModel", false);
    public static final AlgorithmTaskKey SALIENCY_MATTING_MEDIUM_MODEL = AlgorithmTaskKeyFactory.create("saliencyMattingMediumModel", false);
    public static final AlgorithmTaskKey SALIENCY_MATTING_LARGE_MODEL = AlgorithmTaskKeyFactory.create("saliencyMattingLargeModel", false);

    public static final String TAG = "SaliencyMattingAlgoTask";

    private final SaliencyMatting mImpl;
    private final AlgorithmTaskKey mCurrentModel;
    private byte[] alignedAlpha;
    private boolean hasAvaliableModel;

    public SaliencyMattingAlgorithmTask(Context context, SaliencyMattingResourceProvider resourceProvider, EffectLicenseProvider licenseProvider){
        super(context, resourceProvider, licenseProvider);

        mImpl = new SaliencyMatting();
        mCurrentModel = SALIENCY_MATTING_LARGE_MODEL;
        hasAvaliableModel = false;
    }

    @Override
    public int initTask() {
        // Only support Android version greater than 8
        if(!ImageQualityUtil.isOsVersionHigherThan(8)){
            checkResult(mContext.getString(R.string.saliency_matting_not_support), -200);
            return -200;
        }

        if (!mLicenseProvider.checkLicenseResult("getLicensePath")){
            return mLicenseProvider.getLastErrorCode();
        }

        int ret = mImpl.init();
        if(!checkResult("initTask: native init failed", ret)){
            return ret;
        }

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.SALIENCY_MATTING);
        if(mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE){
            ret = mImpl.checkOnlineLicense(licensePath);
        }else{
            ret = mImpl.checkOfflineLicense(licensePath);
        }
        if(!checkResult("initTask: native check license failed", ret)){
            return ret;
        }

        ret = mImpl.setModel(mResourceProvider.saliencyModel(), cvtModelKey2Type(mCurrentModel));
        if(!checkResult("initTask: set default model failed", ret)){
            return ret;
        }
        hasAvaliableModel = true;
        return 0;
    }

    @Override
    public SaliencyMatting.MattingMask process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {

        LogTimerRecord.RECORD(TAG);
        SaliencyMatting.MattingMask res = mImpl.process(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP(TAG);

        int numByetsPerRow = (width + 3) & (-4);
        if(alignedAlpha == null || alignedAlpha.length != numByetsPerRow * res.getHeight()){
            alignedAlpha = new byte[numByetsPerRow * res.getHeight()];
        }
        for(int iR = 0; iR < res.getHeight(); ++iR){
            System.arraycopy(res.getBuffer(), iR * res.getWidth(),
                    alignedAlpha, iR * numByetsPerRow, res.getWidth());
        }
        res.setBuffer(alignedAlpha);

        return res;
    }

    @Override
    public int destroyTask() {
        mImpl.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return SALIENCY_MATTING;
    }

    public interface SaliencyMattingResourceProvider extends AlgorithmResourceProvider {
        String saliencyModel();
    }

    private EffectsSDKEffectConstants.SaliencyMattingModelType cvtModelKey2Type(AlgorithmTaskKey key){
            if(key.equals(SALIENCY_MATTING_SMALL_MODEL))
                return EffectsSDKEffectConstants.SaliencyMattingModelType.BEF_SALIENCY_MATTING_SMALL_MODEL;
            if(key.equals(SALIENCY_MATTING_MEDIUM_MODEL))
                return EffectsSDKEffectConstants.SaliencyMattingModelType.BEF_SALIENCY_MATTING_MEDIUM_MODEL;
            if(key.equals(SALIENCY_MATTING_LARGE_MODEL))
                return EffectsSDKEffectConstants.SaliencyMattingModelType.BEF_SALIENCY_MATTING_LARGE_MODEL;
            return EffectsSDKEffectConstants.SaliencyMattingModelType.BEF_SALIENCY_MATTING_SMALL_MODEL;
    }
}
