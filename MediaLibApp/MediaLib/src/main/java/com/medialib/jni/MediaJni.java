package com.medialib.jni;

import android.view.Surface;

/**
 */

public class MediaJni {

    static {
        try {
            System.loadLibrary("c++_shared");
//            System.loadLibrary("yuv");
            System.loadLibrary("x264");
            System.loadLibrary("mycodec");
            System.loadLibrary("rtmpsvr");
            System.loadLibrary("MediaJni");
        } catch(UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e.getMessage() + " " + e.getCause());
        }
    }

    public interface IDecodeListener {
        void onDecodeCallback(byte[] data, int len, int w, int h, int keyFrame);
        int onRenderTextureId(int textureId1, int textureId2, int textureId3, byte[]data, int w, int h);
    }

    public native int openMediaServer(String logfile, IDecodeListener listener);
    public native void closeMediaServer();
}
