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
//#include <GLES3/gl3.h>
//#include <GLES3/gl3ext.h>
#include <GLES2/gl2.h>
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
static int _defaultBufSize = (2*1024*1024);
static bool _initContexOk = false;
static bool _needGLContext = true;
static bool _setJvmToCodec = true;
static jbyteArray _jbyteAry = NULL;
static int _jbyteSize = (2*1024*1024);
static bool _useSdk = true;
static bool _enableCodec = true;
static int _localPort = 1935;
static char _remoteUrl[255];
static int _width = 0;
static int _height = 0;
static int _chunkSize = 128;

static int _hasDataFrame = 0;
static MetaData _metaData;
static uint32_t _curVideoTimestamp = 0;
static uint8_t  _curVideoAbsTimestamp = 1;
static uint32_t _orgVideoTimestamp = 0;
static uint8_t _orgVideoAbsTimestamp = 1;
static uint32_t _lastAudioTimestamp = 0;
static int _fpsUnit = 0;  // 根据接收到的每帧间隔，计算的帧间隔时间
static int _orgFpsUnit = 0; // 接收到推流开始提交的帧间隔时间

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
    int key;
}TimeStampData;
static std::queue<TimeStampData> _queueAudio; // 音频数据队列
static std::queue<TimeStampData> _queueVideo; // 视频数据队列
static std::mutex _queueVideoMutex;
static std::mutex _queueAudioMutex;
static bool _queueThreadRun = false;
static std::thread _queueThread;

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
static jbyteArray ctojbyte(JNIEnv* env, const char* pat, int len)
{
    if (len > _jbyteSize) {
        if(_jbyteAry != nullptr) {
            env->DeleteGlobalRef(_jbyteAry);
            _jbyteAry = nullptr;
        }
        _jbyteSize = len;
    }
    if (_jbyteAry == nullptr) {
        jbyteArray jb = env->NewByteArray(_jbyteSize);
        _jbyteAry = static_cast<jbyteArray>(env->NewGlobalRef(jb));
    }
    if(_jbyteAry == nullptr) {
        LogError("ctojbyte::NewByteArray OOM, len=%d", _jbyteSize);
        return nullptr;
    }
    env->SetByteArrayRegion(_jbyteAry, 0, len, (jbyte*)pat);
    return _jbyteAry;
}
static u_long getTimeStamp()
{
    timeval tv = {0,0};
    gettimeofday(&tv, nullptr);
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
    if(_eglDisplay != nullptr) {
        eglTerminate(_eglDisplay);
        _eglDisplay = nullptr;
    }
    eglReleaseThread();
    _initContexOk = false;
}
/*
 * 生成一个纹理ID，并保存在_texId
 */
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
    // 阿里的美艳SDK不能用EGL3.0，需要注释下面代码，否则SDK输不出数据
//    if(glIsEnabled(GL_TEXTURE_2D) == false) {
//        glEnable(GL_TEXTURE_2D);
//    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glGenFramebuffers(1, _fboId);
    LogDebug("glGenFramebuffers id = %d", _fboId[0]);
}
/*
 * 释放纹理
 */
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
/*
 *  对视频数据做纹理处理
 *  视频数据是RGBA格式
 */
static void toTexture(unsigned char* data, int len, int w, int h, int keyframe)
{
    LogDebug("toTexture, len=%d, w=%d, h=%d", len, w, h);

    u_long beginTm = getTimeStamp();
    if (_needGLContext) {
        if (initEGLContext() == -1) {
            return;
        }
        loadTextureIds();
        if (_texId[0] == 0) {
            return;
        }
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
        jmethodID method = nullptr;
        GLuint newId = 0;
        if(_needGLContext && _texId[0] > 0) {
            method = env->GetMethodID(jcls, "onRenderTextureId", "(IIII)I");
            if (method == nullptr) {
                LogError("unable to find method onDecodeCallback");
                goto exit;
            }
            glBindTexture(GL_TEXTURE_2D, _texId[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0);
            newId = env->CallIntMethod(_jcallBack, method, (int) _texId[0], len, w, h);
            if (newId == 0) {
                LogError("call SDK render failed, return id=0");
                goto exit;
            }
        } else {
            method = env->GetMethodID(jcls, "onRenderBuffer", "([BIII)I");
            if (method == nullptr) {
                LogError("unable to find method onDecodeCallback");
                goto exit;
            }
            jBuf = ctojbyte(env, (char*)data, len);
            if (jBuf == nullptr) {
                goto exit;
            }
            newId = env->CallIntMethod(_jcallBack, method, jBuf, len, w, h);
            if (newId == 0) {
                LogError("call SDK render failed, return id=0");
                goto exit;
            }
        }
        if(_fboId[0] > 0) {
            glBindTexture(GL_TEXTURE_2D, newId);
            glBindFramebuffer(GL_FRAMEBUFFER, _fboId[0]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, newId, 0);
//            glReadBuffer(GL_COLOR_ATTACHMENT0);
            GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                LogError("glCheckFramebufferStatus is not complete!err=%d", glGetError());
            } else {
                glViewport(0, 0, w, h);
                GLubyte *pixels = getGluBuf(len);
                glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                u_long diff = getTimeStamp() - beginTm;
                LogDebug("toTexture 回调纹理处理函数后的时间=%lu", diff);
                encoder_from_rgba(_encodec, (vbyte8_ptr) pixels, len, w, h, keyframe);
            }
            glBindTexture(GL_TEXTURE_2D, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
    }
    exit:
//    if(jBuf && _setJvmToCodec) {
//        env->DeleteLocalRef(jBuf);
//    }
    if(_needDetach) {
        _javaVM->DetachCurrentThread();
    }
}
/*
 * 发起一个JNI回调，目的是做JAVA层的初始化工作
 */
static void initRender()
{
    LogDebug("enter initRender");
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
            LogError("initRender, getobjectclass failed");
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

}

static void unInitRender()
{
    JNIEnv *env = nullptr;
    int envStat = 0;
    bool needDetach = false;
    envStat = _javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (envStat == JNI_EDETACHED) {
        if(_javaVM->AttachCurrentThread(&env, NULL) != 0) {
            goto exit;
        }
        needDetach = true;
    }
    if (_jcallBack != nullptr) {
        jclass jcls = env->GetObjectClass(_jcallBack);
        if (jcls == nullptr) {
            LogError("unInitRender, getobjectclass failed");
            goto exit;
        }
        jmethodID method = env->GetMethodID(jcls, "onRenderStop", "()V");
        if (method == NULL) {
            LogError("unable to find method onRenderStop");
            goto exit;
        }
        env->CallVoidMethod(_jcallBack, method);
    }

    exit:
    if(needDetach) {
        _javaVM->DetachCurrentThread();
    }
}


/*
 * 清除队列
 */
static void cleanQueue()
{
    _queueVideoMutex.lock();
    while(!_queueVideo.empty()) {
        TimeStampData audioData = _queueVideo.front();
        free(audioData.data);
        _queueVideo.pop();
    }
    _queueVideoMutex.unlock();

    _queueAudioMutex.lock();
    while(!_queueAudio.empty()) {
        TimeStampData data = _queueAudio.front();
        free(data.data);
        _queueAudio.pop();
    }
    _queueAudioMutex.unlock();
}
/*
 *  encodeFunc
 *  处理视频编码后的回调
 *  此回调来自纹理美颜完成后，重新编码后的回调
 */
static void encodeFunc(vbyte8_ptr data, vint32_t len, vint64_t pts, vint64_t dts, vint32_t keyframe, void* user_data)
{
    int ret = 0;
//    if(_texFile != NULL) {
//        ret = fwrite(data, len, 1, _texFile);
//        if (ret != 1) {
//            LogError("write file failed");
//        } else {
//            fflush(_texFile);
//        }
//    }
//    return;

    static u_long encodeTime = getTimeStamp();
    int diff = getTimeStamp() - encodeTime - _orgFpsUnit;
    if(diff < 0) {
        diff = 0;
    }
    encodeTime = getTimeStamp();

    int newUnit = _fpsUnit;
    if(_curVideoAbsTimestamp == 1) {
        _curVideoTimestamp = 0;
    } else {
        newUnit = (_orgFpsUnit + _fpsUnit) / 2;
        _curVideoTimestamp += newUnit;
    }
    LogDebug("新视频时间戳=%d, 新视频帧率间隔时间=%d, 旧的帧间隔=%d, 每帧提交发送时相比帧率延迟时间=%d",  _curVideoTimestamp, newUnit, _orgFpsUnit, diff);
    bool hasSendVideo = false;
    // 检查此视频对应的音频, 通过音频绑定的视频时间戳来判断
    // 如果音频绑定的是同一个视频，则全部发送
    while(!_queueAudio.empty()) {
        TimeStampData audioData = _queueAudio.front();
        if (audioData.videoAbsTimeStamp > _curVideoTimestamp) {
            break;
        }
        // 是否对应了同一个视频
        if (audioData.videoTimeStamp <= _curVideoTimestamp) {
            // 如果此视频时间戳小于音频时间戳，则发送视频
            if (audioData.timeStamp >= _curVideoTimestamp && !hasSendVideo) {
                hasSendVideo = true;
                LogDebug("audio video, videoTimeStamp=%d, curVideoTimestamp=%d, abstimestamp=%d, audiotimestamp=%d", audioData.videoTimeStamp, _curVideoTimestamp, audioData.videoAbsTimeStamp,  audioData.timeStamp);
                sendX264VideoData(_rtmpClient, reinterpret_cast<const char *>(data), len, _curVideoTimestamp, audioData.videoAbsTimeStamp, keyframe);
            }
            // 发送绑定的音频
            sendAudioData(_rtmpClient, reinterpret_cast<const char *>(audioData.data), audioData.size, audioData.timeStamp, audioData.absTimeStamp);
            free(audioData.data);
            _queueAudioMutex.lock();
            _queueAudio.pop();
            _queueAudioMutex.unlock();
        }
    }
    // 如果此视频没发送，则发送
    if(!hasSendVideo) {
        sendX264VideoData(_rtmpClient, reinterpret_cast<const char *>(data), len, _curVideoTimestamp, _curVideoAbsTimestamp, keyframe);
    }
    _curVideoAbsTimestamp = 0;

    diff = getTimeStamp() - encodeTime;
    LogDebug("encodeFunc 函数执行时间=%d",  diff);
}
/*
 * 解码后回调的函数，RGBA格式
 * 在此函数中，需要调用toTexture进行美颜处理
 *
 */
static void decodeFunc(unsigned char* data, int len, int width, int height, int keyframe, void* user_data)
{
    u_long decodeTime = getTimeStamp();
    if (len == 0 || data == NULL) {
        return;
    }
    if(_useSdk) {
        toTexture(data, len, width, height, keyframe);
    } else {
        encoder_from_rgba(_encodec, (vbyte8_ptr) data, len, width, height, keyframe);
    }
    int diff = getTimeStamp() - decodeTime;
    LogDebug(" decodeFunc 函数执行时间:%d", diff);
}
/*
 *  创建编解码器
 */
static bool createVideoCodec()
{
    if(_decodec != 0) {
        release_video_codec(_decodec);
        _decodec = 0;
    }
    if(_encodec != 0) {
        release_video_codec(_encodec);
        _encodec = 0;
    }
    if(_width <= 0) {
        _width = 640;
    }
    if(_height <= 0) {
        _height = 480;
    }
    _decodec = create_video_codec((VideoFmt)VIDEO_MEDIA_CODEC, (CodecType)VIDEO_DECODE_TYPE, _width, _height, 0, 0);
    if (_decodec == 0) {
        LogError("create_video_codec, decoder failed");
        return false;
    }
    set_video_decode_callback(_decodec, decodeFunc, 0);
    _encodec = create_video_codec((VideoFmt)VIDEO_MEDIA_CODEC, (CodecType)VIDEO_ENCODE_TYPE, _width, _height, 0, 0);
    if (_encodec == 0) {
        LogError("create_video_codec, encoder failed");
        return false;
    }
    set_video_encode_callback(_encodec, encodeFunc, 0);
    return true;
}
/*
 *  释放编码器
 */
static void releaseVideoCodec()
{
    if(_decodec != 0) {
        release_video_codec(_decodec);
        _decodec = 0;
    }
    if(_encodec != 0) {
        release_video_codec(_encodec);
        _encodec = 0;
    }
}

/*
 *  queueThreadFunction
 *  这个函数是一个线程函数，用于处理视频队列
 *  RTMP视频接收函数
 */
static void queueThreadFunction(void* arg)
{
    int64_t cores = sysconf(_SC_NPROCESSORS_CONF);
    LogDebug( "查询到CPU个数为: %lu\n", cores);
    int cpuid = 1;
    if(cores >= 4) {
        cpuid = cores - 3;
    }
    if (cpuid < 0) {
        cpuid = 1;
    }
    LogDebug( "queueThreadFunction 线程绑定CPU index: %ld\n", cpuid);
    bindToCpu(cpuid);

    createVideoCodec();
    initRender();
    while(_queueThreadRun) {
        if(!_queueVideo.empty()) {
            TimeStampData videoData = _queueVideo.front();
            if (_enableCodec) {
                decode_to_rgba(_decodec, (vbyte8_ptr) videoData.data, videoData.size);
            } else {
                encodeFunc((vbyte8_ptr) videoData.data, videoData.size, 0, 0, videoData.key, 0);
            }
            _queueVideoMutex.lock();
            free(videoData.data);
            _queueVideo.pop();
            _queueVideoMutex.unlock();
        } else {
            std::this_thread::sleep_for(std::chrono::microseconds(10));
        }

    }
    unInitRender();
    releaseVideoCodec();
    uninitEGLContext();
    releaseTextureIds();
    cleanQueue();
}

//
// 每次开始直播时会回调这个函数，在这个函数中做所有每次初始化的工作
//
static void rtmpBeginPublish(void* user_data)
{
    LogDebug("enter rtmpBeginPublish");

    // 启动一个线程，用来保存以及处理RTMP回调的视频数据
    if(_queueThreadRun) {
        _queueThreadRun = false;
        if(_queueThread.joinable()) {
            _queueThread.join();
        }
    }
    // 初始化变量
    _hasDataFrame = 0;
    _curVideoTimestamp = 0;
    _curVideoAbsTimestamp = 1;
    _orgVideoTimestamp = 0;
    _orgVideoAbsTimestamp = 1;
    beforeSendData(_rtmpClient);
    // 启动线程
    _queueThreadRun = true;
    _queueThread = std::thread(queueThreadFunction, nullptr);
}
// rtmpsvr 推送线程退出时回调此函数
static void rtmpThreadExitCallback(void *user_data)
{
    LogDebug("enter rtmpThreadExitCallback");
    _queueThreadRun = false;
    if(_queueThread.joinable()) {
        _queueThread.join();
    }
    LogDebug("rtmpThreadExitCallback, 释放了视频处理线程");
}

/*
 * rtmp server 接收到视频后的回调，格式H264
 */
static void rtmpVideoReceiv(const char *data, int size, unsigned long timestamp, uint8_t absTimestamp, int key, void* user_data)
{
    if(_decodec == 0) {
        return;
    }
    static u_long t1 = getTimeStamp();
    int diff = getTimeStamp() - t1 - _fpsUnit;
    if(diff < 0) {
        diff = 0;
    }
    t1 = getTimeStamp();
    LogDebug("RTMP回调函数，每次被回调时间间隔%d", diff);

    if(_hasDataFrame == 0) {
        _hasDataFrame = 1;
        char *frameData = NULL;
        int dataSize = getRtmpMetaData(&frameData);
        if(dataSize > 0) {
            getRtmpDataFrame(&_metaData);
            if(_rtmpClient) {
            	setMetaData(_rtmpClient, frameData, dataSize);
                setDataFrame(_rtmpClient, _metaData.duration, _width, _height, _metaData.fps, _metaData.encoder, _metaData.audioid, _metaData.audioVolume);
            }
            _orgFpsUnit = 1000 / _metaData.fps;
        }
    }

    if(_orgVideoTimestamp == 0) {
        _fpsUnit = _orgFpsUnit;
    } else if(timestamp > _orgVideoTimestamp) {
        _fpsUnit = timestamp - _orgVideoTimestamp;
    }
    _orgVideoTimestamp = timestamp;
    _orgVideoAbsTimestamp = absTimestamp;

    TimeStampData videoData;
    videoData.absTimeStamp = absTimestamp;
    videoData.timeStamp = timestamp;
    videoData.data = (uint8_t*)malloc(size + 16);
    videoData.size = size;
    videoData.key = key;
    memcpy(videoData.data, data, size);
    videoData.type = VIDEO_DATA;
    _queueVideoMutex.lock();
    _queueVideo.push(videoData);
    _queueVideoMutex.unlock();
}
static void rtmpAudioReceiv(const char *data, int size, unsigned long timestamp, uint8_t absTimestamp,  void* user_data)
{
    if(_hasDataFrame == 0) {
        _hasDataFrame = 1;
        char *frameData = NULL;
        int dataSize = getRtmpMetaData(&frameData);
        if(dataSize > 0) {
            getRtmpDataFrame(&_metaData);
            if(_rtmpClient) {
                setMetaData(_rtmpClient, frameData, dataSize);
            }
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
    _queueAudioMutex.lock();
    _queueAudio.push(audioData);
    _queueAudioMutex.unlock();
}

/*
 *  jni 初次调用的onLoad
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    _javaVM = vm;
    if(_setJvmToCodec) {
        set_jni_env(vm);
    }
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM *jvm, void *reserved) {

}

/*
 * Class:     com_medialib_jni_MediaJni
 * Method:    setParams
 * Signature: (III;Ljava/lang/string;III)V
 */
extern "C" JNIEXPORT void JNICALL Java_com_medialib_jni_MediaJni_setParams
        (JNIEnv *env, jobject, int use_sdk, int enable_codec,int local_port, jstring url,int w, int h, int chunkSize)
{
    _useSdk = use_sdk;
    _enableCodec = enable_codec;
    _localPort = local_port;
    _width = w;
    _height = h;
    _chunkSize = chunkSize;
    char* curl = js2c(env, url);
    snprintf(_remoteUrl, sizeof(_remoteUrl), "%s", curl);
}
/*
 * Class:     com_medialib_jni_MediaJni
 * Method:    openMediaServer
 * Signature: (Ljava/lang/String;Lcom/medialib/jni/MediaJni/IDecodeYuvListener;)I
 */
extern "C" JNIEXPORT jint JNICALL Java_com_medialib_jni_MediaJni_openMediaServer
        (JNIEnv *env, jobject jthe, jstring path, jobject callBack)
{
    LogDebug("enter openMediaServer");
    int ret = 0;
    RTMP_REQUEST *request = NULL;
    char* cPath = js2c(env, path);
    sprintf(_modulePath, "%s", cPath);
    char log[255];
    sprintf(log, "%smedialib.log", _modulePath);
    if ((ret = codec_init(log)) != 0) {
        LogError("codec_init failed");
        return ret;
    }
    if (!createVideoCodec()) {
        goto error;
    }
    char rtmpLog[255];
    sprintf(rtmpLog, "%srtmpclient.log", _modulePath);
//    sprintf(ipaddr, "rtmp://120.79.139.90:1935/live/test");
    _rtmpClient = startRtmpClient(_remoteUrl, rtmpLog);
    if(_rtmpClient == NULL)
    {
        LogError("startRtmpClient failed, rtmp server ip=%s", _remoteUrl);
        goto error;
    }
    changeChunkSize(_rtmpClient, _chunkSize);
    request = getDefaultRtmpRequest();
    request->rtmpport = _localPort;
    sprintf(rtmpLog, "%srtmpsvr.log", _modulePath);
    _rtmpServer = openRtmpServer(request, rtmpLog);
    if(_rtmpServer == NULL) {
        LogError("openRtmpServer failed, rtmpport=%d", request->rtmpport);
        goto error;
    }
    _rtmpServer->needRawVideo = 0;
    setRtmpVideoCallback(rtmpVideoReceiv, NULL);
    setRtmpAudioCallback(rtmpAudioReceiv, NULL);
    setRtmpBeginPublishCallback(rtmpBeginPublish, NULL);
    setExitPublishThreadCallback(rtmpThreadExitCallback, nullptr);
    if (_pixelsBuf == NULL) {
        _pixelsBuf = (GLubyte*)malloc(_defaultBufSize);
    }
    _jcallBack = (*env).NewGlobalRef(callBack);
    char name[255];
    sprintf(name, "%sdst.264", _modulePath);
    _texFile = fopen(name, "wb");

    //启动成功发送一个回调，告诉java层结果
    if (_jcallBack != NULL) {
        jclass jcls = env->GetObjectClass(_jcallBack);
        if (jcls == NULL) {
            LogError("decodeYuvFunc, getobjectclass failed");
            goto error;
        }
        jmethodID method = nullptr;
        method = env->GetMethodID(jcls, "onStartServerCallback", "(I)V");
        if (method == nullptr) {
            LogError("unable to find method onDecodeCallback");
            goto error;
        }
        env->CallVoidMethod(_jcallBack, method, 1);

    }

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
    if(_pixelsBuf) {
        free(_pixelsBuf);
    }
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

/*
 *  put一个测试的264视频文件，启动一个线程读取文件内容然后发送，进行简单测试流程
 */
static bool runTextMediaFile = false;
static std::thread testMediaThread;
extern "C"
JNIEXPORT void JNICALL
Java_com_medialib_jni_MediaJni_putTestMediaFile(JNIEnv *env, jobject thiz, jstring test_file) {
    //启动一个线程
    const char* fileName = js2c(env, test_file);
    if(runTextMediaFile) {
        runTextMediaFile = false;
    }
    testMediaThread = std::thread ([&fileName]{
        FILE * f = fopen(fileName, "rb");
        if(f != nullptr) {
            char buf[1024];
            size_t len = 0;
            runTextMediaFile = true;
            rtmpBeginPublish(nullptr);
            do {
                len = fread(buf, 1, 1024, f);
                if(len > 0) {
                    rtmpVideoReceiv(buf, len, 0, 0, 1, 0);
                } else {
                    fseek(f, 0, SEEK_SET);
                }
                sleep_m(1);
            } while(len > 0 && runTextMediaFile);
            fclose(f);
            rtmpThreadExitCallback(0);
            runTextMediaFile = false;
        }
    });
    testMediaThread.join();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_medialib_jni_MediaJni_stopTestMedia(JNIEnv *env, jobject thiz) {
    runTextMediaFile = false;
}