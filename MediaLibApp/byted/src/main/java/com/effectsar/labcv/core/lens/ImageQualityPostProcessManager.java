package com.effectsar.labcv.core.lens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.R;
import com.effectsar.labcv.core.lens.util.ImageQualityUtil;
import com.effectsar.labcv.core.license.EffectLicenseHelper;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.ImageUtil;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefTextureResultInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.RenderManager;
import com.effectsar.labcv.effectsdk.VideoAS;
import com.effectsar.labcv.effectsdk.VideoDeflicker;
import com.effectsar.labcv.effectsdk.VideoFI;
import com.effectsar.labcv.effectsdk.VideoStab;
import com.effectsar.labcv.effectsdk.YUVUtils;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.ImageQualityVfiDataType.IMAGE_QUALITY_VFI_DATA_TYPE_TEXTURE_RGBA8;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.ImageQualityVfiType.IMAGE_QUALITY_VFI_TYPE_UM;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.PixlFormat.BGR888;
import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.PixlFormat.RGBA8888;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageQualityPostProcessManager {

    protected Context mContext;

    protected ImageQualityResourceProvider mResourceProvider;
    protected EffectLicenseProvider mLicenseProvider;
    protected ImageUtil mImageUtil;
    private final String mRWPermissionDir;

    private VideoFI mVideoFI = null;
    private boolean mEnableVideoFI = false;

    private VideoStab mVideoStab = null;
    private VideoAS mVideoAS = null;
    private boolean mEnableVideoStab = false;
    public static final int MAXVASWIDTH = 2048;
    public static final int MAXVASHEIGHT = 2048;

    private VideoDeflicker mVideoDeflicker = null;
    private boolean mEnableVideoDeflicker = false;

    public ImageQualityPostProcessManager(Context context, ImageQualityResourceProvider provider) {
        mContext = context;
        mResourceProvider = provider;
        mLicenseProvider = EffectLicenseHelper.getInstance(mContext);
        mImageUtil = new ImageUtil();
        mRWPermissionDir = mContext.getExternalFilesDir("assets").getAbsolutePath();
    }

    public void setPostProcessType (EffectsSDKEffectConstants.ImageQualityPostProcessType type, boolean on) {
        if (type == EffectsSDKEffectConstants.ImageQualityPostProcessType.IMAGE_QUALITY_POST_PROCESS_TYPE_VFI ||
                type == EffectsSDKEffectConstants.ImageQualityPostProcessType.IMAGE_QUALITY_POST_PROCESS_TYPE_VIDEO_STAB){

        }
        else if (on && (ImageQualityUtil.isPixelSeriesDevices() || !ImageQualityUtil.isOpenCLSupport())) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.video_lens_pixel_not_support, Toast.LENGTH_SHORT).show();
                }
            });
            return ;
        }
        if (type == EffectsSDKEffectConstants.ImageQualityPostProcessType.IMAGE_QUALITY_POST_PROCESS_TYPE_VFI) {
            if (mEnableVideoFI == on) return ;
            mEnableVideoFI = on;
            if (on) {
                if (mVideoFI == null) {
                    mVideoFI = new VideoFI();
                    int ret = mVideoFI.create(mResourceProvider.getRWDirPath(), IMAGE_QUALITY_VFI_TYPE_UM, IMAGE_QUALITY_VFI_DATA_TYPE_TEXTURE_RGBA8, 2, EffectsSDKEffectConstants.ImageQulityPowerLevel.POWER_LEVEL_HIGH);
                    if (!checkResult("Vfi init", ret)) return ;
                    ret = mVideoFI.checkLicense(mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.VFI), mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
                    if (!checkResult("Vfi checkLicense", ret)) {
                    }
                } else {
                    if (mVideoFI != null) {
                        mVideoFI.destroy();
                        mVideoFI = null;
                    }
                }
            }
        } else if (type == EffectsSDKEffectConstants.ImageQualityPostProcessType.IMAGE_QUALITY_POST_PROCESS_TYPE_VIDEO_STAB) {
            if (mEnableVideoStab == on) return;
            mEnableVideoStab = on;
            if (on) {
                if (mVideoAS == null) {
                    mVideoAS = new VideoAS();
                    VideoAS.VideoASLevel level = new VideoAS.VideoASLevel(60, 0.25f, 1);
                    VideoAS.VideoASInitConfig config = new VideoAS.VideoASInitConfig(level, MAXVASWIDTH, MAXVASHEIGHT, 4);
                    int ret = mVideoAS.create(config, mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.VIDEO_STAB), mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
                    if (!checkResult("VideoStab checkLicense", ret)) {
                    }
                } else {
                    mVideoAS.destroy();
                    mVideoAS = null;
                }
            }
        } else if (type == EffectsSDKEffectConstants.ImageQualityPostProcessType.IMAGE_QUALITY_POST_PROCESS_TYPE_VIDEO_DEFLICKER) {
            if (mEnableVideoDeflicker == on) return;
            mEnableVideoDeflicker = on;
            if (on) {
                if (mVideoDeflicker == null) {
                    mVideoDeflicker = new VideoDeflicker();
                    VideoDeflicker.VideoDeflickerInitConfig config = new VideoDeflicker.VideoDeflickerInitConfig(mRWPermissionDir, false, 0, 0, RGBA8888.getValue(),
                            EffectsSDKEffectConstants.ImageQulityPowerLevel.POWER_LEVEL_HIGH.getLevel(), EffectsSDKEffectConstants.ImageQulityBackendType.IMAGE_QUALITY_BACKEND_GPU.getType(),
                            EffectsSDKEffectConstants.VideoDeflickerAlgType.LENS_DEFLICKER_ALG_DELAY.getValue());
                    int ret = mVideoDeflicker.create(config, mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.VIDEO_DEFLICKER), mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
                    if (!checkResult("VideoDeflicker checkLicense", ret)) {
                    }
                } else {
                    mVideoDeflicker.destroy();
                    mVideoDeflicker = null;
                }
            }
        }

    }

    public int processTexture(PostProcessData processData) {
        int srcTexture = processData.texture;
        int width = processData.textureWidth;
        int height = processData.textureHeight;
        if (mEnableVideoFI && mVideoFI != null) {
            LogTimerRecord.RECORD("VideoFi");
            int ret = mVideoFI.processTexture(processData.textureP, srcTexture, width, height, processData.vfiFlag
                ,processData.scaleX, processData.scaleY, processData.timeStamp);
            LogTimerRecord.STOP("VideoFi");
            if (ret == -70) {
                LogUtils.e("no need to process"+ processData);
            } else {
                LogUtils.e("add frame"+ processData);
            }
            if (ret < 0 || ret == -70) {
                return 0;
            }
            return ret;
        }
        if (mEnableVideoStab && mVideoAS != null) {
            ByteBuffer inputBuffer = mImageUtil.transferTextureToBuffer(srcTexture, EffectsSDKEffectConstants.TextureFormat.Texure2D, RGBA8888, width, height, 1);
            ByteBuffer bgrBuffer = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder());
            YUVUtils.RGBA2BGR(inputBuffer, bgrBuffer, BGR888.getValue(), width, height);

            VideoAS.VideoASProcessParam param = new VideoAS.VideoASProcessParam();
            VideoAS.VideoASOutput algoOutput = new VideoAS.VideoASOutput();
            param.width = width;
            param.height = height;
            param.strideW = width * 3;
            param.open = true;
            param.scaleX = 1.0f;
            param.scaleY = 1.0f;

            int outTexture = 0;
            if (processData.videoStabTrackingState) {
                LogUtils.d(" VAS cameraTracking...");
                param.frameType = EffectsSDKEffectConstants.VASProcessType.BEF_LENS_VAS_PROCESS_EST;
                param.frameIdx = processData.frameIdx;
                LogTimerRecord.RECORD("VASTracking");
                int ret = mVideoAS.cameraTracking(bgrBuffer, param, algoOutput);
                LogTimerRecord.STOP("VASTracking");
                if (ret != 0) {
                    LogUtils.e("VAS cameraTracking failed: "+ ret);
                    return 0;
                }
                return srcTexture;
            } else {
                LogUtils.d("VAS videoDeforming...");
                param.frameType = EffectsSDKEffectConstants.VASProcessType.BEF_LENS_VAS_PROCESS_WARP;
                param.frameIdx = processData.frameIdx;
                ByteBuffer outputBuffer = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder());
                LogTimerRecord.RECORD("frameDeforming");
                int ret = mVideoAS.frameDeforming(bgrBuffer, param, algoOutput, outputBuffer);
                LogTimerRecord.STOP("frameDeforming");
                ByteBuffer rgbaBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
                YUVUtils.BGR2RGBA(outputBuffer, rgbaBuffer, RGBA8888.getValue(), width, height);
                outTexture = mImageUtil.transferBufferToTexture(rgbaBuffer, RGBA8888, EffectsSDKEffectConstants.TextureFormat.Texure2D, width, height, true);
                if (ret != 0) {
                    LogUtils.e("VideoStab videoStabDeforming failed: "+ ret);
                    return 0;
                }
                return outTexture;
            }
        }
        if (mEnableVideoDeflicker && mVideoDeflicker != null) {
            VideoDeflicker.VideoDeflickerProcessParam param = new VideoDeflicker.VideoDeflickerProcessParam(width, height, width*4, 1, true, srcTexture, 0.80f, 41);
            BefTextureResultInfo dstTexture = new BefTextureResultInfo();
            LogTimerRecord.RECORD("VideoDeflicker");
            int ret = mVideoDeflicker.processTexture(param, dstTexture);
            LogTimerRecord.STOP("VideoDeflicker");
            if (ret != 0) {
                LogUtils.e("VideoDeflicker process failed: "+ ret);
                return 0;
            }
            return dstTexture.getDestTextureId();
        }
        return processData.texture;
    }

    public void destroy() {
        if (mVideoFI != null) {
            mVideoFI.destroy();
            mVideoFI = null;
        }
        if (mVideoStab != null) {
            mVideoStab.destroy();
            mVideoStab = null;
        }
        if (mVideoDeflicker != null) {
            mVideoDeflicker.destroy();
            mVideoDeflicker = null;
        }
        if (mVideoAS != null) {
            mVideoAS.destroy();
            mVideoAS = null;
        }
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

    static public class PostProcessData {
        public int texture;
        public int textureP;
        public int textureHeight;
        public int textureWidth;
        public float timeStamp;
        public float scaleX;
        public float scaleY;
        public int vfiFlag; // {zh} 0表示第一帧，1表示更新帧，2表示不更新帧，更新timeStamp（用于两帧间插多帧的case） {en} 0 means the first frame, 1 means the update frame, 2 means the frame is not updated, and the timeStamp is updated (for the case of interpolating multiple-frames between two frames)
        public boolean videoStabTrackingState;

        public int frameIdx;

        @Override
        public String toString() {
            return "PostProcessData{" +
                    "texture=" + texture +
                    ", textureHeight=" + textureHeight +
                    ", textureWidth=" + textureWidth +
                    ", timeStamp=" + timeStamp +
                    ", scaleX=" + scaleX +
                    ", scaleY=" + scaleY +
                    ", vfiFlag=" + vfiFlag +
                    ", videoStabTrackingState=" + videoStabTrackingState +
                    ", frameIdx=" + frameIdx +
                    '}';
        }
    }

}
