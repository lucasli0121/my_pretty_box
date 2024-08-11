package com.effectsar.labcv.common.imgsrc.camera;

import android.hardware.camera2.TotalCaptureResult;


public interface CameraListener {
    void onOpenSuccess();

    void onOpenFail();

    void onFrameArrived(TotalCaptureResult result);
}
