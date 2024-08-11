package com.effectsar.labcv.core.lens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.effectsar.labcv.effectsdk.BefTextureResultInfo;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.R;
import com.effectsar.labcv.core.lens.util.ImageQualityUtil;
import com.effectsar.labcv.core.license.EffectLicenseHelper;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.ImageUtil;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.AdaptiveSharpen;
import com.effectsar.labcv.effectsdk.BefVideoSRInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.CineMove;
import com.effectsar.labcv.effectsdk.NightScene;
import com.effectsar.labcv.effectsdk.OnekeyEnhance;
import com.effectsar.labcv.effectsdk.RenderManager;
import com.effectsar.labcv.effectsdk.TaintSceneDetect;
import com.effectsar.labcv.effectsdk.Vida;
import com.effectsar.labcv.effectsdk.VideoLiteHdr;
import com.effectsar.labcv.effectsdk.VideoSR;
import com.effectsar.labcv.effectsdk.YUVUtils;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.ImageQualityOnekeyEnhanceSceneMode.SCENE_MODE_MOBILE_RECORDE;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.PixlFormat.RGBA8888;

public class ImageQualityManager implements ImageQualityInterface {
    protected VideoSR mVideoSRTask;
    protected NightScene mNightSceneTask;
    protected AdaptiveSharpen mAdaptiveSharpenTask;
    protected OnekeyEnhance mOnekeyEnhanceTask;
    protected Vida[] mVidas;
    protected TaintSceneDetect mTaintDetectTask;
    protected CineMove mCineMoveTask;
    protected VideoLiteHdr mVideoLiteHdrTask;

    public boolean mEnableVideoSr = false;
    public boolean mEnableNightScene = false;
    public boolean mEnableAdaptiveSharpen = false;
    public boolean mEnableOnekeyEnhance = false;
    public boolean mEnableVidas = false;
    public boolean mEnableTaintDetect = false;
    public boolean mEnableCineMove = false;
    public boolean mEnableVideoLiteHdr = false;

    protected boolean mPause = false; // pause means do nothing
    protected Context mContext;
    protected ImageQualityResourceProvider mResourceProvider;
    protected EffectLicenseProvider mLicenseProvider;

    //power level
    private final EffectsSDKEffectConstants.ImageQulityPowerLevel mPowerLevel = EffectsSDKEffectConstants.ImageQulityPowerLevel.POWER_LEVEL_AUTO;

    //   {zh} 这里只建议业务方调整两个参数：amount和over_ratio。       {en} Only two parameters are recommended for business parties to adjust: amount and over_ratio.  
    //asf config
    private final float mAmount = -1; //   {zh} 锐化强度增益，默认-1（无效值），即不调整。有效值为>0：当设置>1时，会增大锐化强度，设置<1时，减弱锐化强度。       {en} Sharpening strength gain, default -1 (invalid value), that is, it is not adjusted. The effective value is > 0: when setting > 1, the sharpening strength will be increased, and when setting < 1, the sharpening strength will be reduced.
    private final float mOverRatio = -1; //   {zh} 黑白边的容忍度增益，默认-1（无效值），即不调整。       {en} The tolerance gain of black and white edges, default -1 (invalid value), that is, it is not adjusted.
    private final float mEdgeWeightGamma = -1; //   {zh} 对中低频边缘的锐化强度进行调整， 默认-1（无效值），即 不调整。有效值为>0       {en} Adjust the sharpening intensity of the middle and low frequency edges, default -1 (invalid value), that is, no adjustment. Valid value is > 0
    private final int mDiffImgSmoothEnable = -1; //   {zh} 开启后减少锐化带来的边缘artifacts，但锐化强度会比关闭时弱一些， 默认-1（无效值），即保持内部设置，目前设置为开启。 有效值为0或1，0--关闭，1--开启       {en} After opening, the edge artifacts brought by sharpening will be reduced, but the sharpening intensity will be weaker than when closing. The default is -1 (invalid value), that is, the internal settings are maintained, and the current setting is on. Valid values are 0 or 1, 0 -- off, 1 -- on
    private final EffectsSDKEffectConstants.ImageQualityAsfSceneMode mAsfMode = EffectsSDKEffectConstants.ImageQualityAsfSceneMode.ASF_SCENE_MODE_LIVE_RECORED_FRONT;

    //vide sr config
    private String mRWPermissionDir;

    // common config
    private final int mMaxFrameWidth = 720;
    private final int mMaxFrameHeight = 1280;

    //onekey enhance config

    private int mLastFrameWidth = 0;
    private int mLastFrameHeight = 0;

    private boolean mFirstFrame = true;
    private final boolean mDisableDenoise = true; // {zh} 打开降噪 {en} Turn on noise reduction
    private final boolean mDisableHdr = false; // {zh} 打开HDR {en} Turn on HDR
    private final boolean mOneKeyRecordHdrV2 = false; // {zh} 选择hdr v1,v2算法 {en} Select hdr v1, v2 algorithm
    private final boolean mAsnycProcess = false; // {zh} 算法异步执行开关 {en} Algorithm asynchronous execution switch
    private final boolean mDisableNightScene = false; // {zh} 算法白天隔离夜晚 {en} Algorithm day isolation night
    private final boolean mDisableDayScene = false; // {zh} 算法夜晚隔离白天 {en} Algorithm night isolates day
    private final boolean mDisableAsf = true; // {zh} 打开锐化 {en} Turn on sharpening
    private final int mCvDetectFrames = 3;
    private final boolean  mProtectFace = true;
    private final int mInitDecayFrames = 30; // {zh} 动态场景切换用淡入淡出效果来过度（默认值30） {en} Dynamic scene switching is overdone with a fade-in effect (default 30)
    private int mCurrentIso = 0, mMaxIso = 0, mMinIso = 0;
    private ImageUtil mImageUtil;

    //  {zh} 超分倍数  {en} Superfractional multiple
    private static final EffectsSDKEffectConstants.LensVideoAlgType lensVideoAlgType = EffectsSDKEffectConstants.LensVideoAlgType.SR_R15_TYPE;

    EffectsSDKEffectConstants.ImageQualityOnekeyEnhanceSceneMode mOnekeyEnhanceSceneMode = SCENE_MODE_MOBILE_RECORDE;
    OnekeyEnhance.ProcessConfig mOnekeyEnhanceProcessConfig;
    OnekeyEnhance.AlgParamStream mOnekeyEnhanceAlgParamStream;
    private final int mOneKeyEnahnceLuminance_target0 = 175;
    private final int mOneKeyEnahnceLuminance_target1 = 155;
    private final float contrast_factor_float = 0.3f;
    private final float saturation_factor_float = 0.3f;

    public ImageQualityManager (Context context, ImageQualityResourceProvider provider){
        mContext = context;
        mResourceProvider = provider;
        mLicenseProvider = EffectLicenseHelper.getInstance(mContext);
    }
    public int init(String dir, ImageUtil imageUtil){
        mRWPermissionDir = dir;
        mImageUtil = imageUtil;
        return 0;
    }

    public int destroy(){
        if(mNightSceneTask != null){
            mNightSceneTask.release();
            mNightSceneTask = null;
        }

        if(mVideoSRTask != null){
            mVideoSRTask.release();
            mVideoSRTask = null;
        }

        if (mAdaptiveSharpenTask != null){
            mAdaptiveSharpenTask.release();
            mAdaptiveSharpenTask = null;
        }

        if (mOnekeyEnhanceTask != null) {
            mOnekeyEnhanceTask.release();
            mOnekeyEnhanceTask = null;
        }

        if (mTaintDetectTask != null) {
            mTaintDetectTask.release();
            mTaintDetectTask = null;
        }

        if (mCineMoveTask != null) {
            mCineMoveTask.release();
            mCineMoveTask = null;
        }

        if (mVideoLiteHdrTask != null) {
            mVideoLiteHdrTask.release();
            mVideoLiteHdrTask = null;
        }

        if (mVidas != null) {
            for (int i = 0; i < mVidas.length; i ++) {
                mVidas[i].destroy();
            }
            mVidas = null;
        }

        return EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC;
    }

    public void selectImageQuality(EffectsSDKEffectConstants.ImageQualityType imageQualityType, boolean on){

        //   {zh} 判断是否支持视频超分       {en} Determine whether video super score is supported  
        if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDEO_SR || imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ADAPTIVE_SHARPEN) {
            if (on & !ImageQualityUtil.isSupportVideoSR(mContext)) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, R.string.video_sr_not_support, Toast.LENGTH_SHORT).show();
                    }
                });
                return ;
            }

        }

        if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_SNAKE_V8 ||
                imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_HEART_BEAT_V9 ||
                imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_BREATH_V10 ||
                imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_ROT360_V11){

        }
        else if (on && ImageQualityUtil.isPixelSeriesDevices()) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.video_lens_pixel_not_support, Toast.LENGTH_SHORT).show();
                }
            });
            return ;
        }

        if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ONEKEY_ENHANCE) {
            if (on && !ImageQualityUtil.isOsVersionHigherThan(8)) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, R.string.onekey_not_support, Toast.LENGTH_SHORT).show();
                    }
                });
                return ;
            }
        }

        setImageQuality(imageQualityType, on);
    }

    private boolean setImageQuality(EffectsSDKEffectConstants.ImageQualityType imageQualityType, boolean open)
    {
        //   {zh} 打开或者关闭夜景增强       {en} Turn on or off Nightscape Enhancement  
        if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_NIGHT_SCENE){
            mEnableNightScene = open;

            if (open){
                if (mNightSceneTask == null){
                    mNightSceneTask = new NightScene();
                    if (mLicenseProvider.checkLicenseResult("getLicensePath")) {
                        int ret = mNightSceneTask.init(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.NIGHT_SCENE), mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
                        if (!checkResult("init night scene", ret)) {
                            mNightSceneTask.release();
                            mNightSceneTask = null;
                        }
                    }
                }
            }else {
               if(mNightSceneTask != null){
                    mNightSceneTask.release();
                    mNightSceneTask = null;
                }
            }
        }
        //   {zh} 打开或者关闭视频超分       {en} Turn on or off video super score  
        else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDEO_SR) {
            mEnableVideoSr = open;
            if (open) {
                if (mVideoSRTask == null) {
                    mVideoSRTask = new VideoSR();
                    if (mLicenseProvider.checkLicenseResult("getLicensePath")) {
                        boolean mailG = ImageQualityUtil.isMailG();
                        int ret = mVideoSRTask.init(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.VIDEO_SR), mRWPermissionDir, mMaxFrameHeight, mMaxFrameWidth, mPowerLevel,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE,!mailG,lensVideoAlgType);
                        if (!checkResult("init video sr", ret)) {
                            mVideoSRTask.release();
                            mVideoSRTask = null;
                        }
                    }
                }
            } else {
                if (mVideoSRTask != null) {
                    mVideoSRTask.release();
                    mVideoSRTask = null;
                }
            }
        }else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ADAPTIVE_SHARPEN){
            mEnableAdaptiveSharpen = open;
            if (open) {
                if (mAdaptiveSharpenTask == null){
                    mAdaptiveSharpenTask = new AdaptiveSharpen();
                    if (mLicenseProvider.checkLicenseResult("getLicensePath")) {
                        int ret = mAdaptiveSharpenTask.init(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.ADAPTIVE_SHARPEN), mMaxFrameHeight, mMaxFrameWidth,
                                mAsfMode.getMode(), mPowerLevel.getLevel(), mAmount, mOverRatio, mEdgeWeightGamma, mDiffImgSmoothEnable,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);

                        if (!checkResult("init adaptive sharpen", ret)) {
                            mAdaptiveSharpenTask.release();
                            mAdaptiveSharpenTask = null;
                        }
                    }
                }
            } else {
                if (mAdaptiveSharpenTask != null){
                    mAdaptiveSharpenTask.release();
                    mAdaptiveSharpenTask = null;
                    mLastFrameHeight = 0;
                    mLastFrameWidth = 0;
                }
            }
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ONEKEY_ENHANCE) {
            mEnableOnekeyEnhance = open;
            if (open) {
            } else {
                if (mOnekeyEnhanceTask != null) {
                    mOnekeyEnhanceTask.release();
                    mOnekeyEnhanceTask = null;
                    mFirstFrame = true;
                }
            }
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDAS) {
            mEnableVidas = open;
            if (open) {
                initMutilVidas();
            } else {
                for (int i = 0; i < mVidas.length; i ++) {
                    mVidas[i].destroy();
                }
                mVidas = null;
            }
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_TAINT_DETECT) {
            mEnableTaintDetect = open;
            if (open) {
                initTaintDetect();
            } else {
                if (mTaintDetectTask != null) {
                    mTaintDetectTask.release();
                    mTaintDetectTask = null;
                }
            }
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_SNAKE_V8 ||
                imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_HEART_BEAT_V9 ||
                imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_BREATH_V10 ||
                imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_ROT360_V11) {
            mEnableCineMove = open;
            if (open) {
                initCineMoveDetect(imageQualityType);
            } else {
                if (mCineMoveTask != null) {
                    mCineMoveTask.release();
                    mCineMoveTask = null;
                }
            }
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDEO_LITE_HDR) {
            mEnableVideoLiteHdr = open;
            if (mVideoLiteHdrTask != null) {
                mVideoLiteHdrTask.release();
                mVideoLiteHdrTask = null;
            }
            if (open) {
                initVideoLiteHdr();
            }
        }
        LogUtils.e("setImageQuality:" + imageQualityType);

        return true;
    }

    public int processTexture(int srcTextureId,
                              int srcTextureWidth, int srcTextureHeight, ImageQualityResult result)
    {
        // If pause, just return src result
        if (mPause){
            result.texture = srcTextureId;
            result.width = srcTextureWidth;
            result.height = srcTextureHeight;
            return 0;
        }

        if (mEnableVideoSr){
            if (mVideoSRTask != null){
                LogUtils.d("超分处理");

                // This step to judge if the resolution larger than 720p, if true, we release the task, and disable it
                {
                    if ((srcTextureWidth * srcTextureHeight) > 1280 * 720){
                        mEnableVideoSr = false;
                        mVideoSRTask.release();
                        mVideoSRTask = null;

                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.video_sr_resolution_not_support, Toast.LENGTH_SHORT).show();
                            }
                        });
                        //  {zh} 如果超过720p,返回输入纹理，保证视频编码器不会因为输入无效纹理导致的崩溃  {en} If it exceeds 720p, return the input texture to ensure that the video encoder will not crash due to invalid texture input
                        result.texture = srcTextureId;
                        result.width = srcTextureWidth;
                        result.height = srcTextureHeight;
                        return 0;                    }
                }

                LogTimerRecord.RECORD("video_sr");
                BefVideoSRInfo videoSrResult = mVideoSRTask.process(srcTextureId, srcTextureWidth, srcTextureHeight);
                if (videoSrResult == null){
                    return EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_FAIL;
                }
                LogTimerRecord.STOP("video_sr");

                result.height  = videoSrResult.getHeight();
                result.width   = videoSrResult.getWidth();
                result.texture = videoSrResult.getDestTextureId();

                return EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC;
            }

        }else if (mEnableNightScene){
            if (mNightSceneTask != null){
                Integer destTextureId = Integer.valueOf(0);
                LogTimerRecord.RECORD("night_scene");
                int ret = mNightSceneTask.process(srcTextureId, destTextureId, srcTextureWidth, srcTextureHeight);
                if (ret != EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC){
                    return ret;
                }
                LogTimerRecord.STOP("night_scene");
                result.height  = srcTextureHeight;
                result.width   = srcTextureWidth;
                result.texture = destTextureId.intValue();
                return ret;

            }
        } else if (mEnableAdaptiveSharpen && mAdaptiveSharpenTask != null){
            if (mLastFrameWidth != srcTextureWidth || mLastFrameHeight != srcTextureHeight){
                int ret = mAdaptiveSharpenTask.setProperty(mAdaptiveSharpenTask.getmSceneMode(), mAdaptiveSharpenTask.getmPowerLevel(),
                        srcTextureWidth, srcTextureHeight, mAdaptiveSharpenTask.getmAmount(), mAdaptiveSharpenTask.getmOverRatio(),
                        mAdaptiveSharpenTask.getmEdgeWeightGamma(), mAdaptiveSharpenTask.getmDiffImgSmoothEnable());

                if (ret != EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC){
                    Log.e("mAdaptiveSharpenTask", "setProperty: " + ret);
                    return ret;
                }
                mLastFrameWidth = srcTextureWidth;
                mLastFrameHeight = srcTextureHeight;
            }


            BefTextureResultInfo destTextureId = new BefTextureResultInfo();

            LogTimerRecord.RECORD("adaptive_sharpen");
            int ret = mAdaptiveSharpenTask.process(srcTextureId, destTextureId);
            Log.e("mAdaptiveSharpenTask", "processTexture: " + ret);
            LogTimerRecord.STOP("adaptive_sharpen");

            result.height = srcTextureHeight;
            result.width  = srcTextureWidth;
            if (ret == EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC) {
                result.texture = destTextureId.getDestTextureId();
            }else {
                result.texture = srcTextureId;
            }
            return ret;
        } else if (mEnableOnekeyEnhance) {
            if (mOnekeyEnhanceTask == null) {
                initOnekeyEnhance(srcTextureWidth, srcTextureHeight);
                mLastFrameHeight = srcTextureHeight;
                mLastFrameWidth = srcTextureWidth;
            }
            if (mLastFrameWidth != srcTextureWidth || mLastFrameHeight != srcTextureHeight){
                initOnekeyEnhance(srcTextureWidth, srcTextureHeight);
                mLastFrameHeight = srcTextureHeight;
                mLastFrameWidth = srcTextureWidth;
            }

            if (mOnekeyEnhanceTask == null) {
                return -1;
            }

            mOnekeyEnhanceProcessConfig.setFirstFrame(mFirstFrame);
//            LogTimerRecord.RECORD("onekey enhance");
            int ret = mOnekeyEnhanceTask.process(srcTextureId, mOnekeyEnhanceAlgParamStream, mOnekeyEnhanceProcessConfig);
//            LogTimerRecord.STOP("onekey enhance");
            if (ret <= 0) {
                result.texture = srcTextureId;
                result.width = srcTextureWidth;
                result.height = srcTextureHeight;
            } else {
                result.texture = ret;
                result.width = srcTextureWidth;
                result.height = srcTextureHeight;
            }
            if (mFirstFrame) {
                mFirstFrame = false;
            }
            return 0;
        } else if (mEnableVidas) {
            if (mEnableVidas ) {
                if (mVidas != null) {
                    ByteBuffer buffer = mImageUtil.transferTextureToBuffer(srcTextureId, EffectsSDKEffectConstants.TextureFormat.Texure2D, RGBA8888, srcTextureWidth, srcTextureHeight, 1.0f);
                    buffer.position(0);
                    LogTimerRecord.RECORD("vidas");
                    float face = mVidas[0].process(buffer, srcTextureWidth, srcTextureHeight);
                    float aes = mVidas[1].process(buffer, srcTextureWidth, srcTextureHeight);
                    float clarity = mVidas[2].process(buffer, srcTextureWidth, srcTextureHeight);
                    LogTimerRecord.STOP("vidas");

                    result.texture = srcTextureId;
                    result.width = srcTextureWidth;
                    result.height = srcTextureHeight;
                    result.aes = aes;
                    result.face = face;
                    result.clarity = clarity;
                }
            }
            return 0;
        } else if (mEnableTaintDetect) {
            if (mTaintDetectTask != null) {
                ByteBuffer buffer = mImageUtil.transferTextureToBuffer(srcTextureId, EffectsSDKEffectConstants.TextureFormat.Texure2D, RGBA8888, 224, 224, 1.f);
                buffer.position(0);

                //  {zh} 图像格式转换，算法需要224 * 224 BGR格式小图  {en} Image format conversion, the algorithm needs 224 * 224 BGR format small image
                ByteBuffer bgrBuffer = ByteBuffer.allocateDirect(224 * 224 * 3).order(ByteOrder.nativeOrder());
                bgrBuffer.position(0);
                YUVUtils.RGBA2BGR(buffer, bgrBuffer, EffectsSDKEffectConstants.PixlFormat.BGR888.getValue(), 224, 224);
                LogTimerRecord.RECORD("TaintDetect");
                float score = mTaintDetectTask.process(bgrBuffer);
                LogTimerRecord.STOP("TaintDetect");
                result.texture = srcTextureId;
                result.width = srcTextureWidth;
                result.height = srcTextureHeight;
                result.score = score;
            }
            return 0;
        } else if (mEnableCineMove) {
            if (mCineMoveTask != null) {
                BefTextureResultInfo destTextureId = new BefTextureResultInfo();
                LogTimerRecord.RECORD("cineMove");
                int ret = mCineMoveTask.process(srcTextureId, RGBA8888, srcTextureWidth, srcTextureHeight, srcTextureWidth * 4, true, destTextureId);
                LogTimerRecord.STOP("cineMove");
                if (ret == 0) {
                    result.texture = destTextureId.getDestTextureId();
                } else {
                    result.texture = srcTextureId;
                }
                result.width = srcTextureWidth;
                result.height = srcTextureHeight;
            }
            return 0;
        } else if (mEnableVideoLiteHdr) {
            if (mVideoLiteHdrTask != null) {
                LogTimerRecord.RECORD("videoLiteHdr");
                BefVideoSRInfo info = mVideoLiteHdrTask.process(srcTextureId, srcTextureWidth, srcTextureHeight, mFirstFrame, 1.0f);
                LogTimerRecord.STOP("videoLiteHdr");
                if (info != null) {
                    result.texture = info.getDestTextureId();
                } else {
                    result.texture = srcTextureId;
                }
                result.width = srcTextureWidth;
                result.height = srcTextureHeight;
                mFirstFrame = false;
                return 0;
            }
        }

        return EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_FAIL;
    }

    public void recoverStatus(){
        if(mEnableNightScene){
            mNightSceneTask = new NightScene();
            mNightSceneTask.init(mResourceProvider.getLicensePath());
        }

        if (mEnableVideoSr){
            mVideoSRTask = new VideoSR();
            boolean mailG = ImageQualityUtil.isMailG();
            mVideoSRTask.init(mResourceProvider.getLicensePath(), mRWPermissionDir, mMaxFrameHeight, mMaxFrameWidth, mPowerLevel,!mailG,lensVideoAlgType);
        }

        if (mEnableAdaptiveSharpen) {
            mAdaptiveSharpenTask = new AdaptiveSharpen();
            mLastFrameWidth = 0;
            mLastFrameHeight = 0;

            mAdaptiveSharpenTask.init(mResourceProvider.getLicensePath(), mMaxFrameHeight, mMaxFrameWidth,
                    mAsfMode.getMode(), mPowerLevel.getLevel(), mAmount, mOverRatio, mEdgeWeightGamma, mDiffImgSmoothEnable);
        }

        if (mEnableOnekeyEnhance) {
            initOnekeyEnhance(mLastFrameWidth, mLastFrameHeight);
        }

        if (mEnableVidas) {
            initMutilVidas();
        }

        if (mEnableTaintDetect) {
            initTaintDetect();
        }

        if (mEnableVideoLiteHdr) {
            initVideoLiteHdr();
        }
    }

    @Override
    public void setFrameInfo(TotalCaptureResult result) {
        Integer iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
        if (iso == null) {
            mCurrentIso = 0;
        } else {
            mCurrentIso = iso.intValue();
        }
        if (mOnekeyEnhanceProcessConfig !=null) {
            mOnekeyEnhanceProcessConfig.setIso(mCurrentIso);
        }
    }

    private int initOnekeyEnhance(int width, int height) {
        if (mOnekeyEnhanceTask != null) {
            mOnekeyEnhanceTask.release();
        }
        mFirstFrame = true;

        OnekeyEnhance.InitConfig config = new OnekeyEnhance.InitConfig(width, height, mResourceProvider.getRWDirPath(),
                mDisableDenoise, mDisableHdr, mOneKeyRecordHdrV2, mAsnycProcess, mDisableNightScene, mDisableDayScene, mDisableAsf,
                mPowerLevel.getLevel(), mOnekeyEnhanceSceneMode.getMode());
        mOnekeyEnhanceProcessConfig = new OnekeyEnhance.ProcessConfig(mCurrentIso, mMaxIso, mMinIso, mCvDetectFrames, width, height, true, mProtectFace, mInitDecayFrames, 0, null);
        mOnekeyEnhanceAlgParamStream = new OnekeyEnhance.AlgParamStream(mOneKeyEnahnceLuminance_target0, mOneKeyEnahnceLuminance_target1, contrast_factor_float, saturation_factor_float, 2.0f, 0.02f, 1.0f, 0.5f, 2, 37.8f,-1, -1, 5);

        mOnekeyEnhanceTask = new OnekeyEnhance();
        int ret = mOnekeyEnhanceTask.create(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.ONEKEY_ENHANCE_STR),
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE,
                mOnekeyEnhanceAlgParamStream, config);

        if (!checkResult("init onekey enhance", ret)) {
            mOnekeyEnhanceTask.release();
            mOnekeyEnhanceTask = null;
        }

        return ret;
    }

    private int initMutilVidas() {
        Vida.VidaInitConfig config = new Vida.VidaInitConfig(mResourceProvider.getFaceModelPath(), mResourceProvider.getRWDirPath(),
                EffectsSDKEffectConstants.ImageQulityBackendType.IMAGE_QUALITY_BACKEND_GPU.getType(),
                EffectsSDKEffectConstants.ImageQualityVidaType.VIDA_TYPE_FACE.getType(), 1);
        mVidas = new Vida[3];

        mVidas[0] = new Vida();
        int ret = mVidas[0].init(mContext, config, mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.LENS_VIDA), mLicenseProvider.getLicenseMode() ==  EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (ret != 0) {
            checkResult("init vida face", ret);
//            return ret;
        }

        mVidas[1] = new Vida();
        config.setModelPath(mResourceProvider.getAESModelPath());
        config.setVidaType(EffectsSDKEffectConstants.ImageQualityVidaType.VIDA_TYPE_AES.getType());
        ret = mVidas[1].init(mContext, config, mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.LENS_VIDA), mLicenseProvider.getLicenseMode() ==  EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);

        if (ret != 0) {
            checkResult("init vida aes", ret);
//            return ret;
        }

        mVidas[2] = new Vida();
        config.setModelPath(mResourceProvider.getClarityModelPath());
        config.setVidaType(EffectsSDKEffectConstants.ImageQualityVidaType.VIDA_TYPE_Clarity.getType());
        ret = mVidas[2].init(mContext, config, mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.LENS_VIDA), mLicenseProvider.getLicenseMode() ==  EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);

        if (ret != 0) {
            checkResult("init vida Clarity", ret);
//            return ret;
        }
        return ret;
    }

    private int initTaintDetect() {
        TaintSceneDetect.TaintDetectParam param = new TaintSceneDetect.TaintDetectParam(3, mResourceProvider.getTaintModelPath(),"", EffectsSDKEffectConstants.ImageQulityBackendType.IMAGE_QUALITY_BACKEND_CPU.getType(), 2);
        mTaintDetectTask = new TaintSceneDetect();
        int ret = mTaintDetectTask.init(mContext, param, mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.TAINT_DETECT), mLicenseProvider.getLicenseMode() ==  EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);

        LogUtils.e("initTaintDetect code" + ret);
        if (ret != 0) {
            checkResult("init taint detect", ret);
        }
        return ret;
    }

    private int initCineMoveDetect(EffectsSDKEffectConstants.ImageQualityType imageQualityType) {
        if (mCineMoveTask != null) {
            mCineMoveTask.release();
        }

        mCineMoveTask = new CineMove();
        int type = CineMove.CINE_MOVE_TYPE.CINE_MOVE_ALG_SNAKE_V8.getType();
        if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_SNAKE_V8) {
            type = CineMove.CINE_MOVE_TYPE.CINE_MOVE_ALG_SNAKE_V8.getType();
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_HEART_BEAT_V9) {
            type = CineMove.CINE_MOVE_TYPE.CINE_MOVE_ALG_HEART_BEAT_V9.getType();
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_BREATH_V10) {
            type = CineMove.CINE_MOVE_TYPE.CINE_MOVE_ALG_BREATH_V10.getType();
        } else if (imageQualityType == EffectsSDKEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_CINE_MOVE_ALG_ROT360_V11) {
            type = CineMove.CINE_MOVE_TYPE.CINE_MOVE_ALG_ROT360_V11.getType();
        }
        int ret = mCineMoveTask.init(mContext, type,  mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.CINE_MOVE), mLicenseProvider.getLicenseMode() ==  EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        LogUtils.e("init cine move task" + ret);
        if (ret != 0) {
            checkResult("init cine move", ret);
        }
        return ret;
    }

    private void initVideoLiteHdr() {
        if (mVideoLiteHdrTask != null) {
            mVideoLiteHdrTask.release();
        }

        mVideoLiteHdrTask = new VideoLiteHdr();
        int ret = mVideoLiteHdrTask.init(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.VIDEO_HDR_LITE), mResourceProvider.getRWDirPath(), mMaxFrameWidth, mMaxFrameHeight,
                mPowerLevel,
                mResourceProvider.getLutPath(),
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (ret != 0) {
            checkResult("init initVideoLiteHdr", ret);
        }
        mFirstFrame = true;
    }

    @Override
    public void setCameraIsoInfo(int maxIso, int minIso) {
        mMaxIso = maxIso;
        mMinIso = minIso;
    }

    public void setPause(boolean pause) {
        this.mPause = pause;
    }

    protected boolean checkResult(String msg, int ret) {
        if (ret != 0 && ret != -11 && ret != 1) {
            String log = msg + " error: " + ret;
            LogUtils.e(log);
            String toast = RenderManager.formatErrorCode(ret);
            if (toast == null) {
                toast = log;
            }
            Intent intent = new Intent(Config.CHECK_RESULT_BROADCAST_ACTION);
            intent.putExtra("msg", toast);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return false;
        }
        return true;
    }

}
