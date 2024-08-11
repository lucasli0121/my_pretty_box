package com.effectsar.labcv.core.algorithm;

import android.content.Context;
import android.util.Log;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.license.EffectLicenseHelper;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefSkeleton3DInfo;
import com.effectsar.labcv.effectsdk.BefSkeletonInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.Skeleton3dDetect;
import com.effectsar.labcv.effectsdk.SkeletonDetect;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class Skeleton3DAlgorithmTask extends AlgorithmTask<Skeleton3DAlgorithmTask.Skeleton3DResourceProvider, BefSkeleton3DInfo>
{
    public static final AlgorithmTaskKey SKELETON3D = AlgorithmTaskKey.createKey("SKELETON3D", true);
    private final SkeletonDetect mSkeletonDetect;
    private final Skeleton3dDetect mSkeleton3dDetect;
    private final Skeleton3dDetect.InputParam mInputParam;
    private volatile boolean mNeedDetect = true;
    private BefSkeletonInfo mSkeletonInfo;

    public Skeleton3DAlgorithmTask(Context context, Skeleton3DResourceProvider resourceProvider, EffectLicenseProvider provider){
        super(context, resourceProvider, provider);
        mSkeletonDetect = new SkeletonDetect();
        mSkeleton3dDetect = new Skeleton3dDetect();
        mInputParam = new Skeleton3dDetect.InputParam();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.SKELETON_3D);
        boolean onlineLicense = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mSkeletonDetect.init(mContext, mResourceProvider.skeletonModel(), licensePath, onlineLicense
                );
        if (!checkResult("initSkeleton", ret)) return ret;

        ret = mSkeleton3dDetect.init(mContext, licensePath, onlineLicense, mResourceProvider.skeleton3DModel());
        if (!checkResult("initSkeleton3d", ret)) return ret;

        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_WHOLEBODY.getValue(), 1);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_MAXTARGETNUM.getValue(), 1);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_TARGETSPEFRAME.getValue(), 1);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_WITHHANDS.getValue(), 0);

        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_CHECKROOTINVERSE.getValue(), 0);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_TASKPERTICK.getValue(), 1);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_SMOOTHWINSIZE.getValue(), 11);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_SMOOTHORIGINSIGMAXY.getValue(), 0.04f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_SMOOTHORIGINSIGMAZ.getValue(), 0.5f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_SMOOTHSIGMABETAS.getValue(), 0.5f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_WITHWRISTOFFSET.getValue(), 0f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_WRISTSCORETHRES.getValue(), 0.7f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_HSWRISTSCORETHRES.getValue(), 0.4f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_HANDPROBTHRES.getValue(), 0.55f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_CHECKWRISTROT.getValue(), 0f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_FITTINGENABLE.getValue(), 1f);
        mSkeleton3dDetect.setParam(EffectsSDKEffectConstants.Skeleton3DParamType.BEF_SELETON3D_FITTINGROOTENABLE.getValue(), 1f);

        return 0;
    }

    @Override
    public BefSkeleton3DInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        if (!mSkeletonDetect.isInited()) return null;

        if (!mSkeleton3dDetect.isInited()) return null;

        BefSkeletonInfo skeletonInfo = null;
        if (mNeedDetect) {
            LogTimerRecord.RECORD("detectSkeleton");
            skeletonInfo = mSkeletonDetect.detectSkeletonImageMode(buffer, pixlFormat, width, height, stride, rotation);
            LogTimerRecord.STOP("detectSkeleton");
        }

        float scoreThres = 0.8f;
        int targetNumber = 0;
        if (skeletonInfo != null) {
            BefSkeletonInfo.Skeleton[] skeletons = skeletonInfo.getSkeletons();
            float[] points2d = mInputParam.getPoints2d();
            int[] pointValid = mInputParam.getPoint_valid();
            int points2dIndex = 0;
            int pointValidIndex = 0;

            mInputParam.setTarget_num(0);
//            LogUtils.e(skeletonInfo.toString());

            for (int i = 0; i < skeletons.length; i ++){
                BefSkeletonInfo.SkeletonPoint[] points = skeletons[i].getKeypoints();
                int valid_num = 0;
                for (BefSkeletonInfo.SkeletonPoint p:points) {
                    if (p.getScore() > scoreThres) {
                        valid_num ++;
                    }
                }

                if (valid_num > 6){
                    for (BefSkeletonInfo.SkeletonPoint p:points) {
                        points2d[points2dIndex++] = p.getX();
                        points2d[points2dIndex++] = p.getY();
                        if (p.getY() >= 0 && p.getX() >= 0){
                            pointValid[pointValidIndex++] = 1;
                        } else {
                            pointValid[pointValidIndex++] = 0;
                        }
                    }
                    targetNumber ++;
                }
            }
//            LogUtils.e(skeletonInfo.toString());
        } else {
            float[] points2d = mInputParam.getPoints2d();
            int[] pointValid = mInputParam.getPoint_valid();
            for (int i = 0; i < points2d.length; i++) {
                points2d[i] = 0.f;
            }
            for (int i = 0; i < pointValid.length; i++) {
                pointValid[i] = 0;
            }
        }

        mInputParam.setTarget_num(targetNumber);
        mInputParam.setKeypoint_num(18);
        mInputParam.setImage_width(width);
        mInputParam.setImage_height(height);
        mInputParam.setBuffer(buffer);
        mInputParam.setOrientation(rotation.id);
        mInputParam.setPixel_format(pixlFormat.getValue());
        mInputParam.setImage_stride(stride);

        LogTimerRecord.RECORD("detectSkeleton3d");
//        LogUtils.e(mInputParam.toString());
        int ret = mSkeleton3dDetect.detectSkeleton3d(mInputParam); {
            if (!checkResult("detectSkeleton3d", ret)) return null;
        }
        LogTimerRecord.STOP("detectSkeleton3d");

        BefSkeleton3DInfo result = mSkeleton3dDetect.getSkeleton3DInfo();
//        LogUtils.e(result.toString());
        mNeedDetect = result.getTracking() == 0;
        return result;
    }

    @Override
    public int destroyTask() {
        mSkeletonDetect.release();
        mSkeleton3dDetect.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[]{360, 640};
    }

    @Override
    public AlgorithmTaskKey key() {
        return SKELETON3D;
    }

    public interface Skeleton3DResourceProvider extends AlgorithmResourceProvider{
        String skeletonModel();
        String skeleton3DModel();
    }
}