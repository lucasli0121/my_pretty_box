package com.effectsar.labcv.core.lens;

public interface ImageQualityResourceProvider {
    String getLicensePath();
    String getSkinSegPath();
    String getFaceDetectPath();
    String getRWDirPath();
    String getFaceModelPath();
    String getAESModelPath();
    String getClarityModelPath();
    String getTaintModelPath();
    String getLutPath();
}
