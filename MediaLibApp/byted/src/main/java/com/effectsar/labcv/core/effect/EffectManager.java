package com.effectsar.labcv.core.effect;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.text.TextUtils;
import android.util.Log;


import com.bef.effectsdk.message.MessageCenter;
import com.effectsar.labcv.core.external_lib.ExternalLibraryLoader;
import com.effectsar.labcv.core.external_lib.SdCardLibrarySource;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.BefFaceInfo;
import com.effectsar.labcv.effectsdk.BefHandInfo;
import com.effectsar.labcv.effectsdk.BefPublicDefine;
import com.effectsar.labcv.effectsdk.BefSkeletonInfo;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.RenderManager;
import com.effectsar.labcv.effectsdk.LogCallBack;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC;

import org.json.JSONObject;

/** {zh}
 * Created on 5/8/21 10:53 AM
 * 特效接入封装类，确保所有调用在GL线程中使用
 */
/** {en}
 * Created on 5/8/21 10:53 AM
 *  Effect access encapsulation class, ensure that all calls are used in GL threads
 */

public class EffectManager {
    protected Context mContext;
    protected RenderManager mRenderManager;
    private final EffectResourceProvider mResourceProvider;
    private final EffectLicenseProvider mLicenseProvider;
    private OnEffectListener mOnEffectListener;
    public static final boolean USE_PIPELINE = true;
    public static final boolean USE_SO_LIB = false;
    public static final boolean USE_MODEL_FROM_ASSET = false;
    private String mFilterResource;
    private String mStickerResource;
    private final Set<SavedComposerItem> mSavedComposerNodes = new HashSet<>();
    private final Set<String> mExistResourcePath = new HashSet<>();
    private boolean mNeedLoadResource = false;
    private boolean mEnableSyncLoadResource = false;
    private float mFilterIntensity = 0f;
    public String resourcePath = "";
    private EGLContext currentContext;

    public static final int MSG_ID_HAIR_DYE = 0x00000044;
    public static final int MSG_ID_CAPTURE_IMAGE = 2200;
    public static final int MSG_ID_CAPTURE_IMAGE_RESULT = 0x45;
    public static final int MSG_ID_DEVICE_ERROR = 0x100;

    public static final int RENDER_MSG_TYPE_RESOURCE = 0x11;
    public static final int BEF_MSG_TYPE_LICENSE_CHECK = 0x2b0010;
    public static final int BEF_MSG_TYPE_MODEL_MISS = 0x2b0011;
    public static final int BEF_MSG_TYPE_EFFECT_INIT = 0x2b0012;

    private static boolean hasTestUsePipeLine = false;
    private static boolean canUsePipeLine = true;


    public EffectManager(Context context, EffectResourceProvider mResourceProvider, EffectLicenseProvider mLicenseProvider) {
        mContext = context;
        this.mResourceProvider = mResourceProvider;
        this.mLicenseProvider = mLicenseProvider;

        if (USE_SO_LIB) {
            ExternalLibraryLoader externalLibraryLoader = new ExternalLibraryLoader(new SdCardLibrarySource(context));
            externalLibraryLoader.loadLib();
        }
        mRenderManager = new RenderManager();
    }

    /** {zh}
     * @brief 初始化特效SDK，确保在gl线程中执行
     * initialize SDK, must run in gl thread
     */
    /** {en}
     * @brief Initialize the special effects SDK, make sure to execute
     * initialize SDK in gl thread, must run in gl thread
     */

    public static void setAssetManager(Context context) {
        RenderManager.setAssetManager(context);
    }
   public int init(){
        setCanUsePipeline();
        currentContext = EGL14.eglGetCurrentContext();
        if (currentContext == EGL14.EGL_NO_CONTEXT) {
            LogUtils.e("EffectManager init is not running in GL thread, please check your GL environment!!!");
        }
        String SDKVerison = RenderManager.getSDKVersion();
        LogUtils.e("Effect SDK version =" + SDKVerison);
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String filePath = mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.EFFECT);
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        int renderapi = (ci.reqGlEsVersion >= 0x30000)?1:0;

        int ret = 0;
        if (USE_MODEL_FROM_ASSET) {
            ret = mRenderManager.init(mContext, mResourceProvider.getAssetModelPath(), filePath, mContext.getCacheDir().getAbsolutePath(), USE_PIPELINE & canUsePipeLine, onlineLicenseFlag, true, renderapi);
        } else {
            ret = mRenderManager.init(mContext, mResourceProvider.getModelPath(), filePath, mContext.getCacheDir().getAbsolutePath(), USE_PIPELINE & canUsePipeLine, onlineLicenseFlag, renderapi);
        }

       if (!checkResult("mRenderManager.init", ret)) return ret;



        //  {zh} AR 启用内置陀螺仪采集  {en} AR enables built-in gyroscope acquisition
        setUseBuiltinSensor(true);
        set3Buffer(false);
        if (mOnEffectListener != null) {
            mOnEffectListener.onEffectInitialized();
        }
        return ret;

    }

    public static class LogCallBackImpl implements LogCallBack {
        @Override
        //  {zh} 自定义返回log的业务逻辑  {en} Customize the business logic that returns the log
        public void callback(int level, String message) {
            Log.i("EffectSDK",  message);
        }
    }



    /** {zh}
     * @param srcTexture     输入纹理 ID
     *                       input texture ID
     * @param dstTexture 输出纹理ID
     *                   output texture ID
     * @param width  width
     * @param height height
     * @param sensorRotation   phone rotation
     * @param timestamp        timeStamp
     * @return 输出 2D 纹理 output texture
     * @brief 处理纹理
     * process texture
     * @details 此函数只可处理人脸为正的2D格式纹理，确保在gl线程中执行
     */
    /** {en}
     * @param srcTexture      input texture ID
     *                       input texture ID
     * @param dstTexture  output texture ID
     *                   output texture ID
     * @param width  width
     * @param height height
     * @param sensorRotation   phone rotation
     * @param timestamp        timeStamp
     * @return  output 2D texture output texture
     * @brief  processing texture
     * processing texture
     * @details This function can only process 2D format textures with positive faces, ensuring execution in gl threads
     */

    public boolean process(int srcTexture,int dstTexture, int width, int height, EffectsSDKEffectConstants.Rotation sensorRotation, long timestamp){
        if (!EGL14.eglGetCurrentContext().equals(currentContext)) {
            LogUtils.e("EffectManager init and process are not runing in the same glContext");
            EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            EGLSurface surface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
            EGL14.eglMakeCurrent(display, surface, surface, currentContext);
        }
        LogTimerRecord.RECORD("effectProcess");
        if (mEnableSyncLoadResource && mNeedLoadResource){
            mRenderManager.loadResourceWithTimeout(-1);
            mNeedLoadResource = false;
        }
        //  {zh} 注册Sensor监听，SLAM AR需要  {en} Registered Sensor Listening, SLAM AR Required
//       setUseBuiltinSensor(true);
        boolean ret = mRenderManager.processTexture(srcTexture,dstTexture, width,height, sensorRotation, timestamp);
        LogTimerRecord.STOP("effectProcess");
        return ret;
    }

    /** {zh}
     * @brief 销毁特效 SDK，确保在 gl 线程中执行
     * destroy SDK，must run in gl thread
     */
    /** {en}
     * @brief Destroy special effects SDK, make sure to execute
     * destroy SDK in gl thread, must run in gl thread
     */

    public int destroy(){
        if (!EGL14.eglGetCurrentContext().equals(currentContext)) {
            LogUtils.e("EffectManager init and destroy are not running in the same glContext");
            EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            EGLSurface surface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
            EGL14.eglMakeCurrent(display, surface, surface, currentContext);
        }
        LogUtils.d("destroyEffectSDK");
        mRenderManager.release();
        mNeedLoadResource = false;
        mExistResourcePath.clear();
        LogUtils.d("destroyEffectSDK finish");
        return 0;

    }

    /** {zh}
     * @param listener 监听函数
     *                 listener
     * @brief 设置特效监听回调接口
     * set listener
     */
    /** {en}
     * @param listener  Listener function
     *                 Listener
     * @brief  Set special effects monitor callback interface
     * set listener
     */

    public void setOnEffectListener(OnEffectListener listener){
        mOnEffectListener = listener;

    }

    /** {zh}
     * @param path 相对路径
     *             relative path
     * @brief 设置滤镜路径
     * set filter path
     * @details 相对 FilterResource.bundle/Filter 路径，为 null 时关闭滤镜
     * relative path to FilterResource.bundle/Filter, close filter if null
     */
    /** {en}
     * @param path Relative path
     *             Relative path
     * @brief Set filter path
     * Set filter path
     * @details Relative FilterResource .bundle/Filter path, close filter when null
     * relative path to FilterResource.bundle/Filter, close filter if null
     */

    public boolean setFilter(String path){
        if (!TextUtils.isEmpty(path)) {
            path = mResourceProvider.getFilterPath(path);
        }
        mFilterResource = path;
        return mRenderManager.setFilter(path);

    }

    public boolean setFilterAbs(String absPath) {
        mFilterResource = absPath;
        return mRenderManager.setFilter(absPath);
    }

    /** {zh}
     * @param intensity 滤镜强度，0-1
     *                  filter intensity, from 0 to 1
     * @brief 设置滤镜强度
     * set filter intensity
     */
    /** {en}
     * @param intensity Filter intensity, 0-1
     *                  filter intensity, from 0 to 1
     * @brief Set filter intensity
     * Set filter intensity
     */

    public boolean updateFilterIntensity(float intensity){
        boolean result = mRenderManager.updateIntensity(EffectsSDKEffectConstants.IntensityType.Filter.getId(), intensity);
        if (result) {
            mFilterIntensity = intensity;
        }
        return result;
    }

    /** {zh}
     * @param nodes 特效素材相对 ComposeMakeup.bundle/ComposeMakeup 的路径
     *              effect resource relative path to ComposeMakeup.bundle/ComposeMakeup
     * @brief 设置组合特效
     * set composer effects
     * @details 设置 ComposeMakeup.bundle 下的所有功能，包含美颜、美形、美体、美妆等
     * set effects below ComposeMakeup.bundle, includes beauty, reshape, body and makeup ect.
     */
    /** {en}
     * @param nodes The path of the special effects material relative to ComposeMakeup.bundle/ComposeMakeup
     *              effect resource relative path to ComposeMakeup.bundle/ComposeMakeup
     * @brief Set the combination effects
     * set composer effects
     * @details Set all the functions under ComposeMakeup.bundle, including beauty, beauty, body beauty, beauty makeup, etc.
     * set effects below ComposeMakeup.bundle, includes beauty, reshape, body and makeup ect.
     */

    public boolean setComposeNodes(String[] nodes){
        return setComposeNodes(nodes, null);

    }

    /** {zh}
     * @param nodes 特效素材相对 ComposeMakeup.bundle/ComposeMakeup 的路径
     *              effect resource relative path to ComposeMakeup.bundle/ComposeMakeup
     * @brief 设置组合特效
     * set composer effects
     * @details 设置 ComposeMakeup.bundle 下的所有功能，包含美颜、美形、美体、美妆等
     * set effects below ComposeMakeup.bundle, includes beauty, reshape, body and makeup ect.
     */
    /** {en}
     * @param nodes The path of the special effects material relative to ComposeMakeup.bundle/ComposeMakeup
     *              effect resource relative path to ComposeMakeup.bundle/ComposeMakeup
     * @brief Set combination effects
     * set composer effects
     * @details Set all the functions under ComposeMakeup.bundle, including beauty, beauty, body beauty, beauty makeup, etc.
     * set effects below ComposeMakeup.bundle, includes beauty, reshape, body and makeup ect.
     */

    public boolean appendComposeNodes(String[] nodes){
        String prefix = mResourceProvider.getComposePath();
        String[] path = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            if (!TextUtils.isEmpty(nodes[i]) && !nodes[i].contains(mContext.getPackageName())) {
                path[i] = prefix + nodes[i];
            } else {
                path[i] = nodes[i];
            }
        }
        if (mEnableSyncLoadResource){
            for (String item: path){
                if (!mExistResourcePath.contains(item)){
                    mNeedLoadResource = true;
                    break;
                }
            }
            mExistResourcePath.addAll(Arrays.asList(path));
        }


        boolean result =  mRenderManager.appendComposerNodes(path) == BEF_RESULT_SUC;
        if (mEnableSyncLoadResource && mNeedLoadResource){
            if (EGL14.eglGetCurrentContext() != EGL14.EGL_NO_CONTEXT) {
                mRenderManager.loadResourceWithTimeout(-1);
                mNeedLoadResource = false;
            } else {
                LogUtils.e("loadResourceWithTimeout api must be called with GL context");
            }
        }
        return result;

    }

    /** {zh}
     * 删除特效节点
     * @param nodes
     * @return
     * @details 删除节点同时，需要维护保存节点的状态
     */
    /** {en}
     * Delete effect nodes
     * @param nodes
     * @return
     * @details Delete nodes at the same time, you need to maintain the state of the saved nodes
     */
    public boolean removeComposeNodes(String[] nodes){
        String prefix = mResourceProvider.getComposePath();
        String[] path = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            if (!TextUtils.isEmpty(nodes[i]) && !nodes[i].contains(mContext.getPackageName())) {
                path[i] = prefix + nodes[i];
            } else {
                path[i] = nodes[i];
            }
            Iterator<SavedComposerItem>iterator = mSavedComposerNodes.iterator();
            while (iterator.hasNext()){
                if (TextUtils.equals(iterator.next().node, nodes[i])){
                    iterator.remove();
                }
            }
        }
        if (mEnableSyncLoadResource){
            mExistResourcePath.removeAll(Arrays.asList(path));
        }

        return mRenderManager.removeComposerNodes(path) == BEF_RESULT_SUC;
    }

    /** {zh}
     * 重置特效节点
     * @param nodes
     * @param tags
     * @return
     * @details 重置节点同时，需要维护保存节点的状态，删除不在节点数组中的节点
     */
    /** {en}
     * Reset effect node
     * @param nodes
     * @param tags
     * @return
     * @details Reset node at the same time, you need to maintain the state of the saved node and delete the node that is not in the node array
     */
    public boolean setComposeNodes(String[] nodes, String[] tags){
        Iterator<SavedComposerItem>iterator = mSavedComposerNodes.iterator();
        while (iterator.hasNext()){
            if (!contains(nodes,iterator.next().node)){
                iterator.remove();
            }
        }

        String prefix = mResourceProvider.getComposePath();
        String[] path = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            if (!TextUtils.isEmpty(nodes[i]) && !nodes[i].contains(mContext.getPackageName())) {
                path[i] = prefix + nodes[i];
            } else {
                path[i] = nodes[i];
            }
        }
        if (mEnableSyncLoadResource){
            for (String item: path){
                if (!mExistResourcePath.contains(item)){
                    mNeedLoadResource = true;
                    break;
                }
            }
            mExistResourcePath.clear();
            mExistResourcePath.addAll(Arrays.asList(path));
        }

        boolean result =  mRenderManager.setComposerNodesWithTags(path, tags) == BEF_RESULT_SUC;
        if (mEnableSyncLoadResource && mNeedLoadResource){
            if (EGL14.eglGetCurrentContext() != EGL14.EGL_NO_CONTEXT) {
                mRenderManager.loadResourceWithTimeout(-1);
                mNeedLoadResource = false;
            } else {
                LogUtils.e("loadResourceWithTimeout api must be called with GL context");
            }
        }
        return result;
    }

    /** {zh}
     * 判断数组中是否包含某个元素
     * @param nodes
     * @param node
     * @return
     */
    /** {en}
     * Determine whether the array contains an element
     * @param nodes
     * @param node
     * @return
     */
    private boolean contains(String[] nodes, String node){
        boolean contain = false;
        for (String item:nodes){
            if (TextUtils.equals(item,node)){
                contain = true;
            }
        }
        return contain;
    }




    /** {zh}
     * 更新特效节点的强度
     * @param node 特效节点的名称
     * @param key key名称
     * @param intensity 强度值
     * @return 成功返回true 失败返回false
     */
    /** {en}
     * Update the strength of the effect node
     * @param node The name of the effect node
     * @param key key name
     * @param intensity The strength value
     * @return Successful return true Failure return false
     */
    public boolean updateComposerNodeIntensity(String node, String key, float intensity){
        if(node.equals("face_defaut")) return false;
        SavedComposerItem item = new SavedComposerItem(node, key, intensity);
        mSavedComposerNodes.remove(item);
        mSavedComposerNodes.add(item);

        String path = node;
        if (!TextUtils.isEmpty(node) && !node.contains(mContext.getPackageName())) {
            path = mResourceProvider.getComposePath() + node;
        }

        LogUtils.d("updateComposerNodes node ="+path+" key = "+ key + " intensity ="+intensity);

        return mRenderManager.updateComposerNodes(path, key, intensity) == BEF_RESULT_SUC;
    }

    /** {zh}
     * @param path 贴纸路径
     *             relative path of sticker
     * @brief 设置贴纸路径
     * set sticker path
     * @details 贴纸素材的文件路径，相对 StickerResource.bundle 路径，为 null 时为关闭贴纸
     * relative path of sticker resource to StickerResource.bundle, close sticker if null
     */
    /** {en}
     * @param path Sticker path
     *             Relative path of sticker
     * @brief Set sticker path
     * Sticker path
     * @details Sticker material file path, relative StickerResource .bundle path, closed sticker when null
     * relative path of sticker resource to StickerResource.bundle, close sticker if null
     */

    public boolean setSticker(String path){
        if (!TextUtils.isEmpty(path)) {
            path = mResourceProvider.getStickerPath(path);
        }
        mStickerResource = path;
        return mRenderManager.setSticker(path);
    }

    /** {zh}
     * @brief 设置贴纸的绝对路径
     * @details 贴纸素材的文件路径，在 SD 卡上的绝对路径，为 null 是为关闭贴纸
     * @param absPath 贴纸路径
     *                sticker path
     */
    /** {en}
     * @brief Set the absolute path of the sticker
     * @details the file path of the sticker material, the absolute path on the SD card, null is to close the sticker
     * @param absPath  sticker path
     *                sticker path
     */

    public boolean setStickerAbs(String absPath){
        mStickerResource = absPath;
        return mRenderManager.setSticker(absPath);

    }

    /** {zh}
     * @param features 功能数组，外部分配大小
     *                 features array, allocate in outer
     * @brief 获取 SDK 支持的功能，一般为测试用
     * get available features in SDK, just for test
     */
    /** {en}
     * @param features  function array, external allocation size
     *                 features array, allocated in outer
     * @brief  get SDK support functions, generally for testing
     * get available features in SDK, just for test
     */

    public boolean getAvailableFeatures(String[] features){
        return mRenderManager.getAvailableFeatures(features);

    }

    /** {zh}
     * @return 人脸检测结果 face result
     * @brief 获取特效 SDK 中的人脸检测结果
     * get face result in effect SDK
     */
    /** {en}
     * @return Face detection result face result
     * @brief get special effects face detection result in SDK
     * get face result in effect SDK
     */

    public BefFaceInfo getFaceDetectResult(){
        return mRenderManager.getFaceDetectResult();
    }

    /** {zh}
     * @return 手势检测结果 hand result
     * @brief 获取特效 SDK 中的手势检测结果
     * get hand result in effect SDK
     */
    /** {en}
     * @return Gesture detection result hand result
     * @brief get special effects gesture detection result in SDK
     * get hand result in effect SDK
     */

    public BefHandInfo getHandDetectResult(){
        return mRenderManager.getHandDetectResult();

    }

    /** {zh}
     * @return 人体检测结果 skeleton result
     * @brief 获取特效 SDK 中的人体检测结果
     * get skeleton result in effect SDK
     */
    /** {en}
     * @return Human body detection result skeleton result
     * @brief get special effects human body detection result in SDK
     * get skeleton result in effect SDK
     */

    public BefSkeletonInfo getSkeletonDetectResult(){
        return mRenderManager.getSkeletonDetectResult();

    }

    /** {zh} 
     * @param stickerType mask 类型，具体查看 EffectsSDKEffectConstants.FaceMaskType
     *             stickerType of mask, see more in EffectsSDKEffectConstants.FaceMaskType
     * @return 人脸检测结果 face mask result
     * @brief 获取特效 SDK 中的人脸 mask 结果
     * get face mask result in effect SDK
     */
    /** {en} 
     * @param stickerType mask type, specifically view EffectsSDKEffectConstants.FaceMaskType
     *             stickerType of mask, see more in EffectsSDKEffectConstants.FaceMaskType
     * @return  face detection results face mask results
     * @brief  get special effects face mask results in SDK
     * get face mask results in effect SDK
     */

    public BefFaceInfo getFaceMaskResult(EffectsSDKEffectConstants.FaceMaskType stickerType){
        BefFaceInfo faceInfo = new BefFaceInfo();
        mRenderManager.getFaceMaskResult(stickerType, faceInfo);
        return faceInfo;
    }

    /** {zh}
     * @brief 处理触摸事件
     * @param eventCode 触摸事件类型
     * @param x 触摸位置，0-1
     * @param y 触摸位置，0-1
     * @param force 压力值
     * @param majorRadius 触摸范围
     * @param pointerId 触摸点 id
     * @param pointerCount 触摸点数量
     */
    /** {en}
     * @brief Handle touch events
     * @param eventCode Touch event type
     * @param x Touch position, 0-1
     * @param y Touch position, 0-1
     * @param force Pressure value
     * @param majorRadius Touch range
     * @param pointerId Touch point id
     * @param pointerCount Touch point number
     */
    public boolean processTouch(EffectsSDKEffectConstants.TouchEventCode eventCode, float x, float y, float force, float majorRadius, int pointerId, int pointerCount) {
        return mRenderManager.processTouch(eventCode, x, y, force, majorRadius, pointerId, pointerCount) == BEF_RESULT_SUC;
    }

    /** {zh}
     * @brief 处理触摸手势事件
     * @param eventCode 手势事件类型
     * @param x 触摸位置，0-1，缩放手势表示缩放比例，旋转手势表示旋转角度
     * @param y 触摸位置，0-1
     * @param dx 移动距离
     * @param dy 移动距离
     * @param factor 缩放因数
     */
    /** {en}
     * @brief Handle touch gesture events
     * @param eventCode gesture event type
     * @param x touch position, 0-1, zoom gesture indicates zoom scale, rotation gesture indicates rotation angle
     * @param y touch position, 0-1
     * @param dx moving distance
     * @param dy moving distance
     * @param factor zoom factor
     */
    public boolean processGesture(EffectsSDKEffectConstants.GestureEventCode eventCode, float x, float y, float dx, float dy, float factor) {
        return mRenderManager.processGesture(eventCode, x, y, dx, dy, factor) == BEF_RESULT_SUC;
    }

    /** {zh}
     * @param key       纹理名称
     *                  name of texture
     * @param imagePath 图像路径
     *                  path of texture
     * @brief 通过文件设置 render cache texture
     * @details 传入一个固定名字的纹理给到 SDK，传入图片路径，SDK 会将其解析成纹理,不支持解析文件exf角度信息,
     * 对于带旋转角度的图片，请使用传入ByteBuffer的接口
     * Send a certain name texture to SDK. use image path, and SDK will convert it to texture，but
     * not support decode exf rotation param，please use another override method which use ByteBuffer as input
     */
    /** {en}
     * @param key        texture name
     *                  name of texture
     * @param imagePath image path
     *                  path of texture
     * @brief through file settings render cache texture
     * @details pass a texture with a fixed name to SDK, pass in picture path, SDK will parse it into texture, does not support parsing file exf rotation information,
     *  For pictures with rotation angle, please use the interface passed into ByteBuffer
     * Send a certain name texture to SDK. use image path, and SDK will convert it to texture, but
     * not support decoding exf rotation param, please use another override method which use ByteBuffer as input
     */

    public boolean setRenderCacheTexture(String key, String imagePath){
        return mRenderManager.setRenderCacheTexture(key, imagePath) == BEF_RESULT_SUC;

    }

    /** {zh}
     * @param key      key 纹理名称
     *                 name of texture
     * @param buffer   ByteBuffer
     * @param width    width
     * @param height   height
     * @param stride   stride
     * @param format   format，仅支持 RGBA
     *                 only RGBA supported
     * @param rotation rotation
     * @brief 通过 buffer 设置 render cache texture
     * @details 传入一个固定名字的纹理给到 SDK，传入 BEBuffer，SDK 会将其解析成纹理
     */
    /** {en}
     * @param key      key texture name
     *                 name of texture
     * @param buffer   ByteBuffer
     * @param width    width
     * @param height   height
     * @param height   stride
     * @param stride   format
     *                 format, only supports RGBA
     * @param rotation rotation
     * @brief rotation Set rendering cache texture
     * @details through buffer Incoming a texture with a fixed name to
     */

    public boolean setRenderCacheTexture(String key, ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat format, EffectsSDKEffectConstants.Rotation rotation){
        return mRenderManager.setRenderCacheTextureWithBuffer(key, buffer, width, height, stride, format, rotation) == BEF_RESULT_SUC;

    }



    /** {zh}
     * @param usePipeline 是否开启并行渲染
     *                    whether use pipeline
     * @brief 是否开启并行渲染
     * whether use pipeline
     * @details 特效 SDK 内部工作分为两部分，算法检测和特效渲染，当开启并行渲染之后，
     * 算法检测和特效渲染将在不同线程执行，以充分利用多线程进行加速，
     * 但会导致渲染效果延迟一帧
     * there are two parts of effect SDK, algorithm detector and effect render,
     * when usePipeline is true, they will work in different thread to accelerate,
     * with one frame delay
     */
    /** {en}
     * @param usePipeline  whether to turn on parallel rendering
     *                    whether to use pipeline
     * @brief whether to turn on parallel rendering
     * whether to use pipeline
     * @details special effects SDK internal work is divided into two parts, algorithm detection and special effects rendering. After turning on parallel rendering,
     * algorithm detection and special effects rendering will be executed in different threads to make full use of multi-threads for acceleration,
     * but it will cause the rendering effect to be delayed by one frame
     * there are two parts of effect SDK, algorithm detector and effect render,
     * when usePipeline is true, they will work in different thread to accelerate,
     * with one frame delay
     */

    public boolean setPipeline(boolean usePipeline){
        return mRenderManager.setPipeline(usePipeline);

    }

    /** {zh}
     * @param use3Buffer 是否开启 3buffer
     *                   whether use 3buffer
     * @brief 是否开启 3-buffer
     * whether use 3-buffer
     * @details 当开启并行渲染之后，由于算法和特效在不同线程执行，所以需要一些线程同步的工作。
     * 当不开启 3buffer 的时候，SDK 会将传进来的每一帧进行拷贝，
     * 当开启 3buffer 的时候，SDK 不会拷贝每一帧纹理，要求外部传进来的纹理是一个循环的队列，
     * 即连续的 3 帧纹理 ID 不能相同
     * when we use pipeline, we should do something for thread safe,
     * if we use 3buffer, SDK will copy every input frame,
     * otherwise, SDK will not copy it, but we should confirm every 3 continuous
     * texture ID not same
     */
    /** {en}
     * @param use3Buffer  Whether to turn on 3buffer
     *                   Whether to use 3buffer
     * @brief  Whether to turn on 3-buffer
     * Whether to use 3-buffer
     * @details When parallel rendering is turned on, since the algorithm and special effects are executed in different threads, some thread synchronization work is required.
     *  When 3buffer is not turned on, the SDK will copy every frame passed in,
     *  When 3buffer is turned on, the SDK will not copy every frame texture, requiring the external incoming texture to be a circular queue,
     *  That is, 3 consecutive frames of texture ID cannot be the same
     * when we use pipeline, we should do something for thread safe,
     * if we use 3buffer, SDK will copy every input frame,
     * otherwise, SDK will not copy it, but we should confirm every 3 continuous
     * texture ID is not the same
     */

    public boolean set3Buffer(boolean use3Buffer){
        return mRenderManager.set3Buffer(use3Buffer);

    }

    /** {zh}
     * @param msgId 消息类型
     *  message type
     * @param arg1 消息参数
     *  message parameter
     * @param arg2 消息参数
     * message parameter
     * @param arg3 消息参数
     * message parameter
     * @brief 客户端发送消息给sdk
     * client sends message to sdk
     * @details
     */
    /** {en}
     * @param msgId message type
     *  message type
     * @param arg1 message parameter
     *  message parameter
     * @param arg2 message parameter
     * message parameter
     * @param arg3 message parameter
     * message parameter
     * @brief client sends message to sdk
     * client sends message to sdk
     * @details
     */

    public void sendMessage(int msgId, long arg1, long arg2, String arg3){
        mRenderManager.sendMessage(msgId,arg1,arg2,arg3);

    }

    /** {zh}
     * @param key 截图 key
     *  snapshot key
     * @param width 截图宽
     *  snapshot width
     * @param height 截图高
     * snapshot height
     * @brief 客户端获取贴纸内部截图
     * client gets snapshot from sticker
     * @details
     */
    /** {en}
     * @param key snapshot key
     * @param width snapshot width
     * @param height snapshot height
     * @brief client gets snapshot from sticker
     * @details
     */
    public Bitmap getCapturedImageWithKey(String key, int width, int height)
    {
        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
        BefPublicDefine.BefCapturedImageInfo info = new BefPublicDefine.BefCapturedImageInfo();
        int ret = mRenderManager.getCapturedImageWithKey(key,buf,info);
        if(ret != 0)
        {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buf);
        return bitmap;
    }

    /** {zh}
     * @param key 截图 key
     *  snapshot key
     * @param width 截图宽
     *  snapshot width
     * @param height 截图高
     * snapshot height
     * @brief 客户端获取贴纸内部截图
     * client gets snapshot from sticker
     * @details
     */
    /** {en}
     * @param key snapshot key
     * @param width snapshot width
     * @param height snapshot height
     * @brief client gets snapshot from sticker
     * @details
     */
    public ByteBuffer getCapturedImageByteBufferWithKey(String key, int width, int height)
    {
        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
        BefPublicDefine.BefCapturedImageInfo info = new BefPublicDefine.BefCapturedImageInfo();
        int ret = mRenderManager.getCapturedImageWithKey(key,buf,info);
        if(ret != 0)
        {
            return null;
        }

        return buf;
    }

    public void setSyncLoadResource(boolean flag){
        mEnableSyncLoadResource = flag;
    }

    /** {zh}
     * @brief 相机切换回调
     * called when camera changed
     */
    /** {en}
     * @brief Camera toggle callback
     * called when camera changed
     */

    public void onCameraChanged(){
        mRenderManager.cleanPipeline();
    }

    /** {zh}
     * @brief 清除process算法缓存
     */

    public void cleanPipeline(){
        mRenderManager.cleanPipeline();
    }

    /** {zh}
     * @param isFront 是否为前置摄像头
     *                whether it is front camera
     * @brief 设置相机位置
     * set camera position
     */
    /** {en}
     * @param isFront whether it is front camera
     *                whether it is front camera
     * @brief set camera position
     * set camera position
     */

    public void setCameraPosition(boolean isFront){
        if (null == mRenderManager) return;
        mRenderManager.setCameraPostion(isFront);
    }



    /** {zh}
     * @brief 恢复状态
     * recover state
     * @details 当调用 destroy 和 init 之后，SDK 实例会重新创建，之前设置的特效会消失，
     * 此时可用此函数，将之前设置的特效恢复
     * after called destroy and init, SDK instance will recreate, and the effects we set
     * will be removed, we can use it to recover effect
     */
    /** {en}
     * @brief Recovery state
     * recover state
     * @details When destroy and init are called, the SDK instance will be recreated, and the previously set effects will disappear,
     * This function can be used at this time to restore the previously set effects
     * after called destroy and init, SDK instance will recreate, and the effects we set
     * will be removed, we can use it to recover effect
     */

    public void recoverStatus(){
        LogUtils.d("recover status");
        if (!TextUtils.isEmpty(mFilterResource)) {
            mRenderManager.setFilter(mFilterResource);
        }
        if (!TextUtils.isEmpty(mStickerResource)) {
            mRenderManager.setSticker(mStickerResource);
        }
        LogUtils.d("mSavedComposerNodes size ="+mSavedComposerNodes.size()+"  "+mSavedComposerNodes);

        if (mSavedComposerNodes.size() > 0) {
            String[] nodes = getSavedComposerNodes(mSavedComposerNodes);
            String prefix = mResourceProvider.getComposePath();
            String[] path = new String[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                if (!TextUtils.isEmpty(nodes[i]) && !nodes[i].contains(mContext.getPackageName())) {
                    path[i] = prefix + nodes[i];
                } else {
                    path[i] = nodes[i];
                }
            }
            if (mEnableSyncLoadResource){
                for (String item: path){
                    if (!mExistResourcePath.contains(item)){
                        mNeedLoadResource = true;
                        break;
                    }
                }
                mExistResourcePath.clear();
                mExistResourcePath.addAll(Arrays.asList(path));
            }
             mRenderManager.setComposerNodes(path);
            if (mEnableSyncLoadResource && mNeedLoadResource){
                if (EGL14.eglGetCurrentContext() != EGL14.EGL_NO_CONTEXT) {
                    mRenderManager.loadResourceWithTimeout(-1);
                    mNeedLoadResource = false;
                } else {
                    LogUtils.e("loadResourceWithTimeout api must be called with GL context");
                }
            }

            for (SavedComposerItem item : mSavedComposerNodes) {
                if (item instanceof SavedComposerMesssageItem) {
                    mRenderManager.sendMessage(
                            ((SavedComposerMesssageItem) item).msgId,
                            ((SavedComposerMesssageItem) item).arg1,
                            ((SavedComposerMesssageItem) item).arg2,
                            ((SavedComposerMesssageItem) item).config);
                } else {
                    String nodePath = item.node;
                    if (!TextUtils.isEmpty(item.node) && !item.node.contains(mContext.getPackageName())) {
                        nodePath = prefix + item.node;
                    }
                    LogUtils.d("updateComposerNodes node ="+nodePath+" key = "+ item.key + " intensity ="+item.intensity);
                    mRenderManager.updateComposerNodes(nodePath, item.key, item.intensity);
                }
            }
        }
        updateFilterIntensity(mFilterIntensity);
    }


    private String[] getSavedComposerNodes(Set<SavedComposerItem> composerItems){
        if (composerItems == null || composerItems.size() == 0){
            return new String[0];
        }
        Set<String> mComposeNodes = new HashSet<>();
        for (SavedComposerItem item : composerItems) {
            mComposeNodes.add(item.node);
        }
        return mComposeNodes.toArray(new String[mComposeNodes.size()]);
    }

    public interface OnEffectListener {
        void onEffectInitialized();
    }

    private static class SavedComposerItem {
        String node;
        String key;
        float intensity;

        public SavedComposerItem(String node, String key, float value) {
            this.node = node;
            this.key = key;
            this.intensity = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SavedComposerItem that = (SavedComposerItem) o;
            return Objects.equals(node, that.node) &&
                    Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, key);
        }
    }

    private static class SavedComposerMesssageItem extends SavedComposerItem{
        int msgId;
        long arg1;
        long arg2;
        String config;

        public SavedComposerMesssageItem(String node, String key, int msgId, long arg1, long arg2, String config) {
            super(node, key, 0.0f);
            this.msgId = msgId;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.config = config;
        }
    }

    protected boolean checkResult(String msg, int ret) {
        if (ret != 0 && ret != -11 && ret != 1) {
            String log = msg + " error: " + ret;
            LogUtils.e(log);
            String toast = RenderManager.formatErrorCode(ret);
            if (toast == null) {
                toast = log;
            }
            Intent intent = new Intent(Config.CHECK_RESULT_BROADCAST_ACTION);
            intent.putExtra("msg", toast);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return false;
        }
        return true;
    }


    public void addMessageListener(MessageCenter.Listener listener){
        MessageCenter.addListener(listener);
    }

    public void removeMessageListener(MessageCenter.Listener listener){
        MessageCenter.removeListener(listener);
    }

    public void sendCaptureMessage(){
        sendMessage(MSG_ID_CAPTURE_IMAGE, 1, 0, "");
    }

    public boolean setHairColorByPart(int part, float r,float g, float b, float a) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("r",r);
            obj.put("g",g);
            obj.put("b",b);
            obj.put("a",a);
            String config = obj.toString();
            sendMessage(MSG_ID_HAIR_DYE, 0, part, config);
            SavedComposerMesssageItem item = new SavedComposerMesssageItem("hair/ranfa", "hairpart" + part, MSG_ID_HAIR_DYE,0, part, config);
            mSavedComposerNodes.remove(item);
            mSavedComposerNodes.add(item);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void setCanUsePipeline() {
        if (!hasTestUsePipeLine) {
            hasTestUsePipeLine = true;

            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo ci = am.getDeviceConfigurationInfo();
            int renderapi = (ci.reqGlEsVersion >= 0x30000)?1:0;

            FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    boolean support = PipelineProcessUtils.isSupportPipeline(renderapi, EGL14.eglGetCurrentContext());

                    return support ? 1 : 0;
                }
            });

            Thread thread = new Thread(futureTask);
            thread.start();
            try {
                thread.join();
                Integer value = futureTask.get();
                canUsePipeLine = value.intValue() > 0;
            } catch (Exception e) {
                canUsePipeLine = false;
            }
            LogUtils.e("support opengl pipeline processor:" + canUsePipeLine);
        }
    }

    public boolean deviceConfig(boolean accelerator, boolean gyroscope, boolean gravity, boolean orientation){
        return mRenderManager.deviceConfig(accelerator, gyroscope, gravity,orientation) == BEF_RESULT_SUC;
    }

    public  boolean onOrientationChanged(double[] value, int num, double timeStamp){
        return  mRenderManager.onOrientationChanged(value, num, timeStamp) == BEF_RESULT_SUC;

    }

    public boolean onAcceleratorChanged(double x, double y, double z, double timeStamp){
        return mRenderManager.onAcceleratorChanged(x,y,z,timeStamp) == BEF_RESULT_SUC;
    }

    public boolean onGravityChanged(double x, double y, double z, double timeStamp){
        return mRenderManager.onGravityChanged(x,y,z,timeStamp) == BEF_RESULT_SUC;
    }

    public boolean onGyroscopeChanged(double x, double y, double z, double timeStamp){
        return mRenderManager.onGyroscopeChanged(x, y,z,timeStamp) == BEF_RESULT_SUC;
    }

    public boolean setDeviceRotation(float[] quaternion){
        return mRenderManager.setDeviceRotation(quaternion) == BEF_RESULT_SUC;
    }

    public boolean setUseBuiltinSensor(boolean flag){
        LogUtils.d("setUseBuiltinSensor "+flag);
        return mRenderManager.useBuiltinSensor(flag) == BEF_RESULT_SUC;
    }

    /**
     * 开启特效中人脸算法的图片模式，该模式下可以检测更小的人脸
     * 如需开启图片模式，请在init之后调用该接口
     * @param pictureMode
     * @return
     */
    public boolean enableAlgorithmPictureMode(boolean pictureMode) {
        return mRenderManager.enableAlgorithmPictureMode(pictureMode) == BEF_RESULT_SUC;
    }

}
