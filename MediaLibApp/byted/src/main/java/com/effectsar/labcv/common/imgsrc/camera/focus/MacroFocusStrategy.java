package com.effectsar.labcv.common.imgsrc.camera.focus;

import android.hardware.Camera;
import androidx.annotation.NonNull;


class MacroFocusStrategy implements FocusStrategy {
    private static final String TAG = "FocusStrategy";

    @Override
    public void focusCamera(@NonNull Camera camera, @NonNull Camera.Parameters parameters) {
        try {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            camera.setParameters(parameters);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        // ignore
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
