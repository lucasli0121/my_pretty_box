package com.effectsar.labcv.core.lens;

import android.content.Context;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.ResourceHelper;

import java.io.File;

public class ImageQualityResourceHelper extends ResourceHelper implements ImageQualityResourceProvider {
    public static final String RESOURCE = "resource";
    public static final String SKIN_SEGMENTATION = "skin_seg/algo_gglus5cluha_v5.0.model";
    public static final String FACE = "ttfacemodel/algo_ggl1pqh_v11.1.model";
    public static final String FACE_POINT = "lensVida/algo_r5vpl1pqhl8tvh9.bytenn";
    public static final String AES = "lensVida/algo_r5vplphul8tvh9.bytenn";
    public static final String CLARITY = "lensVida/algo_9hrh9ylaik.bytenn";
    public static final String TAINT = "lens_taint_scene_detect/algo_9hculgp5cgluqhchlvhghqg_v2.0.model";
    public static final String LUT_PATH = "lens_vhdr_image_lut_rgb.bin";
    public ImageQualityResourceHelper(Context mContext) {
        super(mContext);
    }

    @Override
    public String getLicensePath() {
        return new File(new File(getResourcePath(), "LicenseBag.bundle"), Config.LICENSE_NAME).getAbsolutePath();
    }

    @Override
    public String getFaceDetectPath() {
        return getModelPath(FACE);
    }

    @Override
    public String getSkinSegPath() {
        return getModelPath(SKIN_SEGMENTATION);
    }

    @Override
    public String getRWDirPath() {
        return mContext.getExternalFilesDir("assets").getAbsolutePath();
    }

    @Override
    public String getFaceModelPath() {
        return getModelPath(FACE_POINT);
    }

    @Override
    public String getLutPath() {
        return new File(new File(getResourcePath(), "vhdr.bundle"), LUT_PATH).getAbsolutePath();
    }

    @Override
    public String getTaintModelPath() {
        return getModelPath(TAINT);
    }

    @Override
    public String getAESModelPath() {
        return getModelPath(AES);
    }

    @Override
    public String getClarityModelPath() {
        return getModelPath(CLARITY);
    }


}
