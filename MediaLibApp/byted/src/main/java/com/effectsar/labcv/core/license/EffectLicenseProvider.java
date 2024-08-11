package com.effectsar.labcv.core.license;

import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

public interface EffectLicenseProvider {
    enum LICENSE_MODE_ENUM{
        OFFLINE_LICENSE,
        ONLINE_LICENSE
    }

    String getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME function_name);
    String updateLicensePath();
    LICENSE_MODE_ENUM getLicenseMode();
    int getLastErrorCode();
    boolean checkLicenseResult(String msg);
}
