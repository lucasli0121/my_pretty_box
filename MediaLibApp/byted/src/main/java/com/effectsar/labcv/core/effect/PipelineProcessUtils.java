package com.effectsar.labcv.core.effect;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.effectsar.labcv.core.util.LogUtils;


public class PipelineProcessUtils {
    static private EGLConfig getConfig( EGLDisplay eglDisplay) {
        int renderableType = EGLExt.EGL_OPENGL_ES3_BIT_KHR | EGL14.EGL_OPENGL_ES2_BIT;

        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.
        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16,
                //EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL14.EGL_NONE
        };

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            return null;
        }
        return configs[0];
    }

    static boolean isSupportPipeline(int glesVersion, EGLContext context) {
        if (glesVersion < 1) {
            return false;
        }

        EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            return false;
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            LogUtils.e("fail to eglInitialize");
            return false;
        }

        int[] surfaceAttrib = {
                EGL14.EGL_WIDTH, 4,
                EGL14.EGL_HEIGHT, 4,
                EGL14.EGL_NONE
        };

        EGLConfig config = getConfig(eglDisplay);
        if (config == null) return false;

        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, config, surfaceAttrib, 0);
        if (eglSurface == null) {
            return false;
        }

        if (config != null) {
            int[] attrib3_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                    EGL14.EGL_NONE
            };
            EGLContext sharedContext = EGL14.eglCreateContext(eglDisplay, config, context,
                    attrib3_list, 0);

            if (EGL14.eglGetError() == EGL14.EGL_SUCCESS && sharedContext != EGL14.EGL_NO_CONTEXT) {
                EGL14.eglDestroyContext(eglDisplay, sharedContext);
                EGL14.eglDestroySurface(eglDisplay, eglSurface);
                return true;
            }
        }
        EGL14.eglDestroySurface(eglDisplay, eglSurface);

        return true;
    }
}
