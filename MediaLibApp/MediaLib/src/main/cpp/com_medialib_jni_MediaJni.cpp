#include <jni.h>
#include <string>
#include <map>
#include <time.h>
#include <queue>
#include <mutex>
#include <thread>
#include <android/log.h>
#include "include/mycodec/codec.h"
#include "include/rtmpsrv.h"
#include "include/rtmp_client.h"
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
static RTMP_CLIENT* _rtmpClient = 0;
static bool _needDetach = false;
static char _modulePath[255];
static FILE * _texFile = NULL;
static GLuint _texId[1];
static GLuint _fboId[1];
static EGLContext _eglCtx = NULL;
static EGLSurface _eglSurface = NULL;
static EGLDisplay _eglDisplay = NULL;
static GLubyte *_pixelsBuf = NULL;
static int _defaultBufSize = 1024*1024;
static bool _initContexOk = false;
typedef enum {
    VIDEO_DATA = 0,
    AUDIO_DATA = 1
}DataType;
typedef struct {
    unsigned int videoTimeStamp;
    uint8_t videoAbsTimeStamp;
    unsigned int timeStamp;
    uint8_t absTimeStamp;
    uint8_t * data;
    DataType type;
    int size;
}TimeStampData;
static std::queue<TimeStampData> _queueData;
static std::mutex _queueMutex;
static std::thread *_dataThread = NULL;
static bool _threadRun = false;

#define LogDebug(...) __android_log_print(ANDROID_LOG_DEBUG, "MediaJni", __VA_ARGS__)
#define LogError(...) __android_log_print(ANDROID_LOG_ERROR, "MediaJni", __VA_ARGS__)

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
    if (bytes == NULL) {
        LogError("ctojbyte::NewByteArray OOM, len=%zu", strlen(pat));
        return NULL;
    }
    env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    return bytes;
}
static u_long getTimeStamp()
{
    timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}
static GLubyte* getGluBuf(int len)
{
    if(len > _defaultBufSize) {
        _defaultBufSize = len;
        if(_pixelsBuf) {
            free(_pixelsBuf);
            _pixelsBuf = NULL;
        }
    }
    if(_pixelsBuf == NULL) {
        _pixelsBuf = (GLubyte *) malloc(_defaultBufSize);
    }
    return _pixelsBuf;
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
            LogError("eglGetDisplay failed!");
            return -1;
        }
    }
    EGLint major, minor;
    if (!eglInitialize(_eglDisplay, &major, &minor)) {
        LogError("eglInitialize  false");
        return -1;
    }
    LogDebug("eglInitialize, major=%d, minor=%d", major, minor);
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
        LogError("eglContext create failed!!");
        return -1;
    }
    _eglSurface = eglCreatePbufferSurface(_eglDisplay, glcfg, surfaceAttr);
    if (_eglSurface == EGL_NO_SURFACE) {
        switch(eglGetError()) {
            case EGL_BAD_ALLOC:
                LogError("eglCreatePbufferSurface failed, err=EGL_BAD_ALLOC");
                break;
            case EGL_BAD_CONFIG:
                LogError("eglCreatePbufferSurface failed, err=EGL_BAD_CONFIG");
                break;
            case EGL_BAD_PARAMETER:
                LogError("eglCreatePbufferSurface failed, err=EGL_BAD_PARAMETER");
                break;
            case EGL_BAD_MATCH:
                LogError("eglCreatePbufferSurface failed, err=EGL_BAD_MATCH");
                break;
            default:
                LogError("eglCreatePbufferSurface failed, err=others");
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
    LogDebug("glGenTexturesId=%d", _texId[0]);
    if(_texId[0] == 0) {
        LogError("cann't create textures id, glGenTextures failed:%d", glGetError());
    }
    if(glIsEnabled(GL_TEXTURE_2D) == false) {
        glEnable(GL_TEXTURE_2D);
    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glGenFramebuffers(1, _fboId);
    LogDebug("glGenFramebuffers id = %d", _fboId[0]);
}
static void releaseTextureIds()
{
    if(_texId[0] > 0) {
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        glDeleteTextures(1, _texId);
        _texId[0] = 0;
    }
    if(_fboId[0] > 0) {
        glDeleteFramebuffers(1, _fboId);
        _fboId[0] = 0;
    }
}
static void toTexture(unsigned char* data, int len, int w, int h, int keyframe)
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
    jbyteArray jBuf = NULL;

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
            LogError("decodeYuvFunc, getobjectclass failed");
            goto exit;
        }
        jmethodID method = env->GetMethodID(jcls, "onRenderTextureId", "(III[BII)I");
        if (method == NULL) {
            LogError("unable to find method onDecodeCallback");
            goto exit;
        }
        glBindTexture(GL_TEXTURE_2D, _texId[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
        jBuf = ctojbyte(env, (char*)data);
        if (jBuf == NULL) {
            goto exit;
        }
        GLuint newId = env->CallIntMethod(_jcallBack, method, (int) _texId[0], 0, 0, jBuf, w, h);
        if (newId == 0) {
            LogError("call SDK render failed, return id=0");
            goto exit;
        }
        glBindTexture(GL_TEXTURE_2D, newId);
        glBindFramebuffer(GL_FRAMEBUFFER, _fboId[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, newId, 0);
        glReadBuffer(GL_COLOR_ATTACHMENT0);
        GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            LogError("glCheckFramebufferStatus is not complete!err=%d", glGetError());
        } else {
            glViewport(0, 0, w, h);
            GLubyte *pixels = getGluBuf(len);
            glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            encoder_from_rgba(_encodec, (vbyte8_ptr) pixels, len, w, h, keyframe);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    exit:
    if(_needDetach) {
        _javaVM->DetachCurrentThread();
    }
    if(jBuf) {
        env->DeleteLocalRef(jBuf);
    }
}
static void initRender()
{
    JNIEnv *env = 0;
    int envStat = 0;
    bool needDetach = false;
    envStat = _javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (envStat == JNI_EDETACHED) {
        if(_javaVM->AttachCurrentThread(&env, NULL) != 0) {
            goto exit;
        }
        needDetach = true;
    }
    if (_jcallBack != NULL) {
        jclass jcls = env->GetObjectClass(_jcallBack);
        if (jcls == NULL) {
            LogError("decodeYuvFunc, getobjectclass failed");
            goto exit;
        }
        jmethodID method = env->GetMethodID(jcls, "onRenderInit", "()V");
        if (method == NULL) {
            LogError("unable to find method onDecodeCallback");
            goto exit;
        }
        env->CallVoidMethod(_jcallBack, method);
    }

    exit:
    if(needDetach) {
        _javaVM->DetachCurrentThread();
    }
    uninitEGLContext();
    releaseTextureIds();
}

static int _hasDataFrame = 0;
static MetaData _metaData;
static uint32_t _curVideoTimestamp = 0;
static uint32_t _orgVideoTimestamp = 0;
static uint8_t _orgVideoAbsTimestamp = 1;
static uint8_t  _curVideoAbsTimestamp = 1;
static u_long _lastTime = 0;
static long _delayTime = 0;
static int _fpsUnit = 0;
static void checkAndSendAudioData()
{
    while(_queueData.size() > 0) {
        TimeStampData audioData = _queueData.front();
        if (audioData.videoTimeStamp <= _curVideoTimestamp) {
            LogDebug("audio, videoTimeStamp=%d, curVideoTimestamp=%d, audiotimestamp=%d", audioData.videoTimeStamp, _curVideoTimestamp, audioData.timeStamp);
            switch (audioData.type) {
                case AUDIO_DATA:
                    sendAudioData(_rtmpClient, reinterpret_cast<const char *>(audioData.data),
                                  audioData.size, audioData.timeStamp, audioData.absTimeStamp);
                    break;
            }
            free(audioData.data);
            _queueMutex.lock();
            _queueData.pop();
            _queueMutex.unlock();
        } else {
            break;
        }
    }
}
static void cleanQueue()
{
    _queueMutex.lock();
    while(_queueData.size() > 0) {
        TimeStampData audioData = _queueData.front();
        free(audioData.data);
        _queueData.pop();
    }
    _queueMutex.unlock();
}
static void encodeFunc(vbyte8_ptr data, vint32_t len, vint64_t pts, vint64_t dts, void* user_data)
{
    if(_curVideoAbsTimestamp == 1) {
        _curVideoTimestamp = 0;
    } else {
        _curVideoTimestamp += _fpsUnit;
    }
    _delayTime = getTimeStamp() - _lastTime;
    LogDebug("_videoTimestamp=%d, timeunit=%d, _delayTime=%d",  _curVideoTimestamp, _fpsUnit, _delayTime);
    bool hasSendVideo = false;
    while(_queueData.size() > 0) {
        TimeStampData audioData = _queueData.front();
        if (audioData.videoTimeStamp <= _curVideoTimestamp) {

            if (audioData.timeStamp >= _curVideoTimestamp && !hasSendVideo) {
                hasSendVideo = true;
                LogDebug("audio video, videoTimeStamp=%d, curVideoTimestamp=%d, abstimestamp=%d, audiotimestamp=%d", audioData.videoTimeStamp, _curVideoTimestamp, audioData.videoAbsTimeStamp,  audioData.timeStamp);
                sendX264VideoData(_rtmpClient, reinterpret_cast<const char *>(data), len, _curVideoTimestamp, audioData.videoAbsTimeStamp);
            }
            sendAudioData(_rtmpClient, reinterpret_cast<const char *>(audioData.data), audioData.size, audioData.timeStamp, audioData.absTimeStamp);
            free(audioData.data);
            _queueMutex.lock();
            _queueData.pop();
            _queueMutex.unlock();
        } else {
            break;
        }
    }
    if(!hasSendVideo) {
        sendX264VideoData(_rtmpClient, reinterpret_cast<const char *>(data), len, _curVideoTimestamp, _curVideoAbsTimestamp);
    }
    _curVideoAbsTimestamp = 0;
    _lastTime = getTimeStamp();
}
/*
 * 解码后回调的函数，RGBA格式
 */
static void decodeFunc(unsigned char* data, int len, int width, int height, int keyframe, void* user_data)
{
    toTexture(data, len, width, height, keyframe);
//    int ret = 0;
//    if(_texFile != NULL) {
//        ret = fwrite(data, len, 1, _texFile);
//        if (ret != 1) {
//            LogError("write file failed");
//        } else {
//            fflush(_texFile);
//        }
//    }
//    return;
//    encoder_from_rgba(_encodec, (vbyte8_ptr) data, len, width, height, keyframe);
}

static void rtmpBeginPublish(void* user_data)
{
    _hasDataFrame = 0;
    _curVideoTimestamp = 0;
    _curVideoAbsTimestamp = 1;
    _orgVideoTimestamp = 0;
    _orgVideoAbsTimestamp = 1;
    beforeSendData(_rtmpClient);
    cleanQueue();
    initRender();
}
/*
 * rtmp server 接收到视频后的回调，格式H264
 */
static void rtmpVideoReceiv(const char *data, int size, unsigned long timestamp, uint8_t absTimestamp, int key, void* user_data)
{
    if(_decodec == 0) {
        return;
    }
    if(_hasDataFrame == 0) {
        _hasDataFrame = 1;
        char *frameData = NULL;
        int dataSize = getRtmpMetaData(&frameData);
        if(dataSize > 0) {
            getRtmpDataFrame(&_metaData);
            setMetaData(_rtmpClient, frameData, dataSize);
        }
        _lastTime = getTimeStamp();
    }
    _fpsUnit = timestamp - _orgVideoTimestamp;
    _orgVideoTimestamp = timestamp;
    _orgVideoAbsTimestamp = absTimestamp;
    decode_to_rgba(_decodec, (vbyte8_ptr)data, size);
}
static void rtmpAudioReceiv(const char *data, int size, unsigned long timestamp, uint8_t absTimestamp,  void* user_data)
{
    if(_hasDataFrame == 0) {
        _hasDataFrame = 1;
        char *frameData = NULL;
        int dataSize = getRtmpMetaData(&frameData);
        if(dataSize > 0) {
            getRtmpDataFrame(&_metaData);
            setMetaData(_rtmpClient, frameData, dataSize);
        }
    }

    TimeStampData audioData;
    audioData.absTimeStamp = absTimestamp;
    audioData.timeStamp = timestamp;
    audioData.videoTimeStamp = _orgVideoTimestamp;
    audioData.videoAbsTimeStamp = _orgVideoAbsTimestamp;
    audioData.data = (uint8_t*)malloc(size + 16);
    audioData.size = size;
    memcpy(audioData.data, data, size);
    audioData.type = AUDIO_DATA;
    _queueMutex.lock();
    _queueData.push(audioData);
    _queueMutex.unlock();
}
static void handleThreadData()
{
//    while(_threadRun) {
//        if(_queueData.size() == 0) {
//            sleep_m(30);
//            continue;
//        }
//        checkAndSendAudioData();
//        sleep_m(1);
//    }
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
        LogError("codec_init failed");
        return ret;
    }
    _decodec = create_video_codec((VideoFmt)VIDEO_MEDIA_CODEC, (CodecType)VIDEO_DECODE_TYPE, 0, 0, 0, 0);
    if (_decodec == 0) {
        LogError("create_video_codec, decoder failed");
        goto error;
    }
    set_video_decode_callback(_decodec, decodeFunc, 0);
    _encodec = create_video_codec((VideoFmt)VIDEO_MEDIA_CODEC, (CodecType)VIDEO_ENCODE_TYPE, 0, 0, 0, 0);
    if (_encodec == 0) {
        LogError("create_video_codec, encoder failed");
        goto error;
    }
    set_video_encode_callback(_encodec, encodeFunc, 0);
    _rtmpServer = openRtmpServer(getDefaultRtmpRequest());
    if(_rtmpServer == NULL) {
        LogError("openRtmpServer failed");
        goto error;
    }
    _rtmpServer->needRawVideo = 0;
    setRtmpVideoCallback(rtmpVideoReceiv, NULL);
    setRtmpAudioCallback(rtmpAudioReceiv, NULL);
    setRtmpBeginPublishCallback(rtmpBeginPublish, NULL);
    char rtmpLog[255];
    sprintf(rtmpLog, "%srtmpclient.log", _modulePath);
    char ipaddr[255];
//    sprintf(ipaddr, "rtmp://120.79.139.90:1935/live/test");
    sprintf(ipaddr, "rtmp://192.168.1.103:1935/live/test");
    _rtmpClient = startRtmpClient(ipaddr, rtmpLog);
    if(_rtmpClient == NULL)
    {
        LogError("startRtmpClient failed, rtmp server ip=%s", ipaddr);
        goto error;
    }
    if (_pixelsBuf == NULL) {
        _pixelsBuf = (GLubyte*)malloc(_defaultBufSize);
    }
    _jcallBack = (*env).NewGlobalRef(callBack);
    _threadRun = true;
    _dataThread = new std::thread(handleThreadData);
    char name[255];
    sprintf(name, "%sdst.264", _modulePath);
//    _texFile = fopen(name, "wb");
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
    if(_rtmpClient != NULL) {
        stopRtmpClient(_rtmpClient);
        _rtmpClient = NULL;
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
//    if(_texFile != NULL) {
//        fclose(_texFile);
//        _texFile = NULL;
//    }
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
    if(_rtmpClient != NULL) {
        stopRtmpClient(_rtmpClient);
        _rtmpClient = NULL;
    }
    if(_pixelsBuf) {
        free(_pixelsBuf);
    }
    _dataThread->join();
    delete _dataThread;
    codec_unini();
}

/*
 * Class:     com_medialib_jni_MediaJni
 * Method:    testMediaCodec
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_medialib_jni_MediaJni_testMediaCodec(JNIEnv * env, jobject, jstring inputFile, jstring outFile)
{
    char * src = js2c(env, inputFile);
    char * dest = js2c(env, outFile);

    test_hw_decode(_decodec, src, dest);
//    test_muxing_decode(name, src, dest);
}
