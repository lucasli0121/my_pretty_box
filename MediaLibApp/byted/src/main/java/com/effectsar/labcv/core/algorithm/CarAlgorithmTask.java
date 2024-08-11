package com.effectsar.labcv.core.algorithm;

import android.content.Context;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefCarDetectInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.CarDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class CarAlgorithmTask extends AlgorithmTask<CarAlgorithmTask.CarResourceProvider, BefCarDetectInfo> {
    //  {zh} 总开关  {en} Master switch
    public static final AlgorithmTaskKey CAR_ALGO = AlgorithmTaskKeyFactory.create("carAlgo");
    public static final AlgorithmTaskKey CAR_RECOG = AlgorithmTaskKeyFactory.create("carRecog");
    public static final AlgorithmTaskKey BRAND_RECOG = AlgorithmTaskKeyFactory.create("carBrand");

    public static final double GREY_THREHOLD = 40.0;
    public static final double BLUR_THREHOLD = 5.0;

    private final CarDetect mDetector;

    public CarAlgorithmTask(Context context, CarResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new CarDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.CAR_DETECT);
        int ret = mDetector.createHandle(licensePath, mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initCarDetect", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.CarModelType.DetectModel, mResourceProvider.carModel());
        if (!checkResult("set DetectModel ", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.CarModelType.BrandNodel, mResourceProvider.carBrandModel());
        if (!checkResult("set BrandNodel ", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.CarModelType.OCRModel, mResourceProvider.brandOcrModel());
        if (!checkResult("set OCRModel ", ret)) return ret;
        ret = mDetector.setModel(EffectsSDKEffectConstants.CarModelType.TrackModel, mResourceProvider.carTrackModel());
        if (!checkResult("set TrackModel ", ret)) return ret;
        return ret;
    }

    @Override
    public BefCarDetectInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectCar");
        LogUtils.d("process car");
        BefCarDetectInfo carDetectInfo = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectCar");
        return carDetectInfo;
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);
        LogUtils.d("setConfig car");

        mDetector.setParam(EffectsSDKEffectConstants.CarParamType.BEF_Car_Detect, getBoolConfig(CAR_RECOG) ? 1f : -1f);
        mDetector.setParam(EffectsSDKEffectConstants.CarParamType.BEF_Brand_Rec, getBoolConfig(BRAND_RECOG) ? 1f : -1f);
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
        return CAR_RECOG;
    }

    public interface CarResourceProvider extends AlgorithmResourceProvider {
        String carModel();

        String carBrandModel();

        String brandOcrModel();

        String carTrackModel();
    }
}
