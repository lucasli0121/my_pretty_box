package com.effectsar.labcv.common.imgsrc.camera.focus;

import android.hardware.Camera;
import androidx.annotation.NonNull;


public interface FocusStrategy {
    void focusCamera(@NonNull Camera camera, @NonNull Camera.Parameters parameters);
}