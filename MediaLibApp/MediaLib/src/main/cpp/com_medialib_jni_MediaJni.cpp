#include <jni.h>
#include <string>
#include <android/log.h>
#include "include/mycodec/codec.h"
#include "include/rtmpsrv.h"
#include "com_medialib_jni_MediaJni.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>

static JavaVM* _javaVM = NULL;
static jobject _jcallBack = NULL;
static long _decodec = 0;
static long _encodec = 0;
static RTMP_SERVER * _rtmpServer = NULL;
static bool _needDetach = false;
static char _modulePath[255];
static FILE * _texFile = NULL;
static GLuint _texId[1];
static GLuint _fboId[1];
static EGLContext _eglCtx = NULL;
static EGLSurface _eglSurface = NULL;
static EGLDisplay _eglDisplay = NULL;
static bool _initContexOk = false;

#define Log(...) __android_log_print(ANDROID_LOG_DEBUG, "MediaJni", __VA_ARGS__)

/*
 *
 *把JNI的jstring类型转换成char* 字符串，用于jni和c类型转换
 */
static char* js2c(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}
/*
 *  把char* 类型转换成java的jstring类型
 */
static jstring ctojs(JNIEnv* env, const char* pat)
{
    jclass strClass = env->FindClass("java/lang/String");
    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = env->NewByteArray(strlen(pat));
    env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    jstring encoding = env->NewStringUTF("utf-8");
    return (jstring)env->NewObject(strClass, ctorID, bytes, encoding);
}
static jbyteArray ctojbyte(JNIEnv* env, const char* pat)
{
    jbyteArray bytes = env->NewByteArray(strlen(pat));
    env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    return bytes;
}
static int initEGLContext()
{
    if(_initContexOk) {
        return 0;
    }
    const EGLint confAttr[] = {
            EGL_RENDERABLE_TYPE,
            EGL_OPENGL_ES3_BIT_KHR,
            EGL_SURFACE_TYPE,
            EGL_PBUFFER_BIT,//EGL_WINDOW_BIT EGL_PBUFFER_BIT we will create a pixelbuffer surface
            EGL_RED_SIZE,   8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE,  8,
            EGL_ALPHA_SIZE, 8,// if you need the alpha channel
            EGL_DEPTH_SIZE, 8,// if you need the depth buffer
            EGL_STENCIL_SIZE,8,
            EGL_NONE
    };
    const EGLint surfaceAttr[] = {
            EGL_WIDTH, 1,
            EGL_HEIGHT,1,
            EGL_NONE
    };
    _eglCtx = eglGetCurrentContext();
    _eglDisplay = eglGetCurrentDisplay();
    if (_eglDisplay == EGL_NO_DISPLAY) {
        if((_eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY)) == EGL_NO_DISPLAY) {
            Log("eglGetDisplay failed!");
            return -1;
        }
    }
    EGLint major, minor;
    if (!eglInitialize(_eglDisplay, &major, &minor)) {
        Log("eglInitialize  false");
        return -1;
    }
    Log("eglInitialize, major=%d, minor=%d", major, minor);
    EGLConfig glcfg ;
    EGLint numCfg = 0;
    eglChooseConfig(_eglDisplay, confAttr, &glcfg, 1, &numCfg);
    if (_eglCtx == NULL) {
        EGLint ctxAttrs[] = {
                EGL_CONTEXT_CLIENT_VERSION, 3,
                EGL_NONE
        };
        _eglCtx = eglCreateContext(_eglDisplay, glcfg,EGL_NO_CONTEXT, ctxAttrs);
    }
    if (_eglCtx == NULL) {
        Log("eglContext create failed!!");
        return -1;
    }
    _eglSurface = eglCreatePbufferSurface(_eglDisplay, glcfg, surfaceAttr);
    if (_eglSurface == EGL_NO_SURFACE) {
        switch(eglGetError()) {
            case EGL_BAD_ALLOC:
                Log("eglCreatePbufferSurface failed, err=EGL_BAD_ALLOC");
                break;
            case EGL_BAD_CONFIG:
                Log("eglCreatePbufferSurface failed, err=EGL_BAD_CONFIG");
                break;
            case EGL_BAD_PARAMETER:
                Log("eglCreatePbufferSurface failed, err=EGL_BAD_PARAMETER");
                break;
            case EGL_BAD_MATCH:
                Log("eglCreatePbufferSurface failed, err=EGL_BAD_MATCH");
                break;
            default:
                Log("eglCreatePbufferSurface failed, err=others");
        }
        eglDestroyContext(_eglDisplay, _eglCtx);
        _eglCtx = NULL;
        return -1;
    }
    eglMakeCurrent(_eglDisplay, _eglSurface, _eglSurface, _eglCtx);
    _initContexOk = true;
    return 0;
}
static void uninitEGLContext()
{
    if(_eglSurface != NULL) {
        eglDestroySurface(_eglDisplay, _eglSurface);
        _eglSurface = NULL;
    }
    if(_eglCtx != NULL) {
        eglDestroyContext(_eglDisplay, _eglCtx);
        _eglCtx = NULL;
    }
    _initContexOk = false;
}
static void loadTextureIds()
{
    if(_texId[0] > 0) {
        return;
    }
    glGenTextures(1, _texId);
    Log("glGenTexturesId=%d", _texId[0]);
    if(_texId[0] == 0) {
        Log("cann't create textures id, glGenTextures failed:%d", glGetError());
    }
    if(glIsEnabled(GL_TEXTURE_2D) == false) {
        glEnable(GL_TEXTURE_2D);
    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glGenFramebuffers(1, _fboId);
    Log("glGenFramebuffers id = %d", _fboId[0]);
}
static void releaseTextureIds()
{
    if(_texId[0] > 0) {
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        glDeleteTextures(1, _texId);
    }
    if(_fboId[0] > 0) {
        glDeleteFramebuffers(1, _fboId);
    }
}
static void toTexture(unsigned char* data, int len, int w, int h)
{
    if(initEGLContext() == -1) {
        return;
    }
    loadTextureIds();
    if (_texId[0] == 0) {
        return;
    }
    JNIEnv *env = 0;
    int envStat = 0;
    int ret = 0;

    envStat = _javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (envStat == JNI_EDETACHED) {
        if(_javaVM->AttachCurrentThread(&env, NULL) != 0) {
            goto exit;
        }
        _needDetach = true;
    }
    if (_jcallBack != NULL) {
        jclass jcls = env->GetObjectClass(_jcallBack);
        if (jcls == NULL) {
            Log("decodeYuvFunc, getobjectclass failed");
            goto exit;
        }
        jmethodID method = env->GetMethodID(jcls, "onRenderTextureId", "(III[BII)I");
        if (method == NULL) {
            Log("unable to find method onDecodeCallback");
            goto exit;
        }
        glBindTexture(GL_TEXTURE_2D, _texId[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
        GLuint newId = env->CallIntMethod(_jcallBack, method,  (int)_texId[0], 0, 0, ctojbyte(env, (char*)data), w, h);
        if (newId == 0) {
            Log("call SDK render failed, return id=0");
            goto exit;
        }
        glBindTexture(GL_TEXTURE_2D, newId);
        glBindFramebuffer(GL_FRAMEBUFFER, _fboId[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, newId, 0);
        glReadBuffer(GL_COLOR_ATTACHMENT0);
        GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            Log("glCheckFramebufferStatus is not complete!");
        }
        glViewport(0, 0, w, h);
        GLubyte * pixels = (GLubyte*)malloc(len);
        glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        vbyte8_ptr out = NULL;
        vint32_t size = rgba_to_yuv420p(_decodec, (vbyte8_ptr)pixels, len, w, h, w, h, &out);
        ret = fwrite(out, size, 1, _texFile);
        if(ret != 1) {
            Log("write file failed");
        }
        free(pixels);
    }
    exit:
    if(_needDetach) {
        _javaVM->DetachCurrentThread();
    }
}
/*
 * 解码后回调的函数，RGBA格式
 */
static void decodeFunc(unsigned char* data, int len, int width, int height, int keyframe, void* user_data)
{
    toTexture(data, len, width, height);
}
/*
 * rtmp server 接收到视频后的回调，格式H264
 */
static void rtmpVideoReceiv(const char *data, int size, unsigned long timestamp, int key, void* user_data)
{
    if(_decodec == 0) {
        return;
    }
//    decode_to_yuv420p(_decodec, (vbyte8_ptr)data, size);
    decode_to_rgba(_decodec, (vbyte8_ptr)data, size);
}
static void rtmpAudioReceiv(const char *data, int size, unsigned long timestamp, void* user_data)
{

}
/*
 *  jni 初次调用的onLoad
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    _javaVM = vm;
    set_jni_env(vm);
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM *jvm, void *reserved) {

}

/*
 * Class:     com_medialib_jni_MediaJni
 * Method:    openMediaServer
 * Signature: (Ljava/lang/String;Lcom/medialib/jni/MediaJni/IDecodeYuvListener;)I
 */
extern "C" JNIEXPORT jint JNICALL Java_com_medialib_jni_MediaJni_openMediaServer
        (JNIEnv *env, jobject jthe, jstring path, jobject callBack)
{
    int ret = 0;
    char* cPath = js2c(env, path);
    sprintf(_modulePath, "%s", cPath);
    char log[255];
    sprintf(log, "%smedialib.log", _modulePath);
    if ((ret = codec_init(log)) != 0) {
        return ret;
    }
    _decodec = create_video_codec((VideoFmt)VIDEO_H264_CODEC, (CodecType)VIDEO_DECODE_TYPE, 0, 0, 0, 0);
    if (_decodec == 0) {
        goto error;
    }
    set_video_decode_callback(_decodec, decodeFunc, 0);
    _encodec = create_video_codec((VideoFmt)VIDEO_H264_CODEC, (CodecType)VIDEO_ENCODE_TYPE, 0, 0, 0, 0);
    if (_encodec == 0) {
        goto error;
    }
    _rtmpServer = openRtmpServer(getDefaultRtmpRequest());
    if(_rtmpServer == NULL) {
        goto error;
    }
//    if(initEGLContext() == -1) {
//        goto error;
//    }
//    loadTextureIds();
    setRtmpVideoCallback(rtmpVideoReceiv, NULL);
    setRtmpAudioCallback(rtmpAudioReceiv, NULL);
    _jcallBack = (*env).NewGlobalRef(callBack);
    char name[255];
    sprintf(name, "%sdst.yuv", _modulePath);
    _texFile = fopen(name, "wb");
    return 0;
error:
    if(_decodec != 0) {
        release_video_codec(_decodec);
        _decodec = 0;
    }
    if(_encodec != 0) {
        release_video_codec(_encodec);
        _encodec = 0;
    }
    if(_rtmpServer != NULL) {
        closeRtmpServer(_rtmpServer);
        _rtmpServer = NULL;
    }
    codec_unini();
    return -1;
}

/*
 * Class:     com_medialib_jni_MediaJni
 * Method:    closeMediaServer
 * Signature: ()V
 */
extern "C" JNIEXPORT void JNICALL Java_com_medialib_jni_MediaJni_closeMediaServer(JNIEnv *, jobject)
{
    if(_texFile != NULL) {
        fclose(_texFile);
        _texFile = NULL;
    }
    releaseTextureIds();
    uninitEGLContext();
    if(_decodec != 0) {
        release_video_codec(_decodec);
        _decodec = 0;
    }
    if(_rtmpServer != NULL) {
        closeRtmpServer(_rtmpServer);
        _rtmpServer = NULL;
    }
    codec_unini();
}

/*
 * Class:     com_medialib_jni_MediaJni
 * Method:    testMediaCodec
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_medialib_jni_MediaJni_testMediaCodec(JNIEnv * env, jobject, jstring codeName, jstring inputFile, jstring outFile)
//{
//    char * name = js2c(env, codeName);
//    char * src = js2c(env, inputFile);
//    char * dest = js2c(env, outFile);
//
//    test_hw_decode(name, src, dest);
////    test_muxing_decode(name, src, dest);
//}
