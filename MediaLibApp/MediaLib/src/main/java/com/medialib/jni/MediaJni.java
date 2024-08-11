package com.medialib.jni;

import android.view.Surface;

/**
 */

public class MediaJni {

    static {
        try {
            System.loadLibrary("c++_shared");
            System.loadLibrary("x264");
            System.loadLibrary("yuv");
            System.loadLibrary("rtmpsvr");
            System.loadLibrary("avcodec");
            System.loadLibrary("avdevice");
            System.loadLibrary("avformat");
            System.loadLibrary("avutil");
            System.loadLibrary("swscale");
            System.loadLibrary("mycodec");
            System.loadLibrary("MediaJni");
        } catch(UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e.getMessage() + " " + e.getCause());
        }
    }

    public interface IDecodeListener {
        void onStartServerCallback(int result);
        void onDecodeCallback(byte[] data, int len, int w, int h, int keyFrame);
        int onRenderTextureId(int textureId, int len, int w, int h);
        int onRenderBuffer(byte[]data, int len, int w, int h);
        void onRenderInit();
        void onRenderStop();
    }

    public native void setParams(int useSdk, int enableCodec, int localPort, String remoteUrl, int w, int h, int chunkSize);
    public native int openMediaServer(String logfile, IDecodeListener listener);
    public native void closeMediaServer();
    public native void testMediaCodec(String inputFile, String outputFile);
    public native void putTestMediaFile(String testFile);
    public native  void stopTestMedia();
}
