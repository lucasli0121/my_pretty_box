package com.effectsar.labcv.core.algorithm.base;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.RenderManager;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class AlgorithmTask<T extends AlgorithmResourceProvider, R extends Object> {

    protected Context mContext;
    protected T mResourceProvider;
    protected EffectLicenseProvider mLicenseProvider;
    protected Map<AlgorithmTaskKey, Object> mConfig;

    public AlgorithmTask(Context context, T resourceProvider, EffectLicenseProvider licenseProvider) {
        mContext = context;
        mResourceProvider = resourceProvider;
        mLicenseProvider = licenseProvider;
        mConfig = new HashMap<>();
    }

    /** {zh} 
     * @brief 初始化算法
     */
    /** {en} 
     * @brief Initialization algorithm
     */

    public abstract int initTask();

    /** {zh} 
     * @brief SDK 处理
     * @param buffer buffer，必须是 direct 类型
     * @param width width
     * @param height height
     * @param stride stride
     * @param pixlFormat format
     * @param rotation rotation
     */
    /** {en} 
     * @brief SDK  processing
     * @param buffer buffer, must be direct type
     * @param width width
     * @param height height
     * @param stride stride
     * @param pixlFormat format
     * @param rotation rotation
     */

    public abstract R process(ByteBuffer buffer, int width, int height, int stride,
                                EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation);

    public R process(ByteBuffer buffer, int width, int height, int stride,
                     EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation, long timeStamp)
    {
        LogUtils.e("current process api with timestamp is not implemented! please use the other process api");
        return null;
    }

    /** {zh} 
     * @brief 销毁算法
     */
    /** {en} 
     * @brief Destruction algorithm
     */

    public abstract int destroyTask();

    /** {zh} 
     * @brief 设置参数
     * @param key 参数类型
     * @param p 参数
     */
    /** {en} 
     * @brief Set parameter
     * @param key  parameter type
     * @param p  parameter
     */

    public void setConfig(AlgorithmTaskKey key, Object p) {
        mConfig.put(key, p);
    }

    /** {zh} 
     * @brief 获取预期 buffer 大小
     * @return 返回数组，0 号为宽，1 号为高
     */
    /** {en} 
     * @brief Get the expected buffer size
     * @return  return array, 0  is wide, 1 is high
     */

    public abstract int[] preferBufferSize();

    /** {zh} 
     * @brief 获取 key
     */
    /** {en} 
     * @brief Get key
     */

    public abstract AlgorithmTaskKey key();

    protected boolean getBoolConfig(AlgorithmTaskKey key, boolean orDefault) {
        if (mConfig.containsKey(key)) {
            Object obj = mConfig.get(key);
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            }
        }
        return orDefault;
    }

    protected boolean getBoolConfig(AlgorithmTaskKey key) {
        return getBoolConfig(key, false);
    }

    protected float getFloatConfig(AlgorithmTaskKey key, float orDefault) {
        if (mConfig.containsKey(key)) {
            Object obj = mConfig.get(key);
            if (obj instanceof Float) {
                return (float) obj;
            }
        }
        return orDefault;
    }

    protected float getFloatConfig(AlgorithmTaskKey key) {
        return getFloatConfig(key, 0.f);
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

}
