package com.effectsar.labcv.core.license;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.external_lib.ExternalLibraryLoader;
import com.effectsar.labcv.core.external_lib.SdCardLibrarySource;
import com.effectsar.labcv.core.util.LogUtils;
//import com.effectsar.labcv.effectsdk.OnlineLicense;
import com.effectsar.labcv.effectsdk.RenderManager;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseWrapper;
import com.effectsar.labcv.licenselibrary.HttpRequestProvider;

import java.io.File;
import java.util.HashMap;

import com.effectsar.labcv.core.effect.EffectManager;

public class EffectLicenseHelper implements EffectLicenseProvider {
    public static final String RESOURCE = "resource";
    public static final String LICENSE_URL_OFFICIAL = "https://cv-tob.byteintl.com/v1/api/sdk/tob_license/getlicense"; // {zh} 正式 {en} Formal
    public static final String LICENSE_URL_BOE = "http://cvtob.license.byted.org/v1/api/sdk/tob_license/getlicense";    //boe
    public static String LICENSE_URL = LICENSE_URL_OFFICIAL;
    public static boolean overSeasVersion = false;
    private static EffectLicenseHelper instance = null;

    private final Context mContext;
    private static LICENSE_MODE_ENUM _licenseMode = LICENSE_MODE_ENUM.OFFLINE_LICENSE;
    private int _errorCode = 0;
    private String _errorMsg = "";
    private EffectsSDKLicenseWrapper licenseWrapper = null;
    private HttpRequestProvider httpProvider = null;

    private static String KEY = "biz_license_tool_test_keyc37702def92e411cba63f1b78ec18285";
    private static String SECRET = "95ff17dec611dfe361f4c6493fd0bf31";
    public static EffectLicenseHelper getInstance(Context mContext) {
        //  {zh} 先判断实例是否存在，若不存在再对类对象进行加锁处理  {en} First determine whether the instance exists, if it does not exist, then lock the class object
        if (instance == null) {
            synchronized (EffectLicenseHelper.class) {
                if (instance == null) {
                    instance = new EffectLicenseHelper(mContext);
                }
            }
        }
        return instance;
    }

    public static void Online_or_offline_model(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("online_model",0);
        if (sharedPreferences.getString("online_model_key", "").equals("OFFLINE_LICENSE")){
            _licenseMode = LICENSE_MODE_ENUM.OFFLINE_LICENSE;
        }
        else if (sharedPreferences.getString("online_model_key", "").equals("ONLINE_LICENSE")){
            _licenseMode = LICENSE_MODE_ENUM.ONLINE_LICENSE;
        }
    }

    private EffectLicenseHelper(Context mContext) {
        this.mContext = mContext;
        HashMap<String, String> parames = new HashMap<>();
        if (_licenseMode == LICENSE_MODE_ENUM.ONLINE_LICENSE) {
            parames.put("mode", "ONLINE");
            parames.put("url", LICENSE_URL);
            parames.put("key", KEY);
            parames.put("secret", SECRET);
            parames.put("licensePath", mContext.getFilesDir().getPath() + "/license.bag");
        }
        else if (_licenseMode == LICENSE_MODE_ENUM.OFFLINE_LICENSE)
        {
            parames.put("mode", "OFFLINE");
            parames.put("licensePath", new File(new File(getResourcePath(), "LicenseBag.bundle"), Config.LICENSE_NAME).getAbsolutePath());
        }
        this.httpProvider = new EffectHttpRequestProvider();

        if (EffectManager.USE_SO_LIB) {
            ExternalLibraryLoader externalLibraryLoader = new ExternalLibraryLoader(new SdCardLibrarySource(mContext));
            externalLibraryLoader.loadLib();
        }
        this.licenseWrapper = new com.effectsar.labcv.licenselibrary.EffectsSDKLicenseWrapper(parames, this.httpProvider);
    }

    @Override
    public String getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME function) {

        String filePath = licenseWrapper.getParam("licensePath");
        if (licenseFileExists()){ // {zh} 文件存在的话，做一次检测，成功的话就返回 {en} If the file exists, do a test and return if successful
            int ret = EffectsSDKLicenseWrapper.checkLicense(filePath, function, _licenseMode == LICENSE_MODE_ENUM.ONLINE_LICENSE);
            if (ret == 0){
                return filePath;
            }
        }
        // {zh} OFFLINE_LICENSE 模式不需要更新 {en} OFFLINE_LICENSE does not need to be updated
        if (_licenseMode == LICENSE_MODE_ENUM.OFFLINE_LICENSE)
           return filePath;

        // {zh} 文件不存在，或者文件授权不成功 {en} The file does not exist, or the file authorization is unsuccessful
        if (updateLicensePath().isEmpty()){
            // {zh} 拉取license失败 {en} Failed to pull the license
            return "";
        }
        checkLicenseResult(function);
        return filePath;
    }

    private String getLicensePath() {

        if (_licenseMode == LICENSE_MODE_ENUM.ONLINE_LICENSE) {
            _errorCode = 0;
            _errorMsg = "";
            // {zh} 同步请求 {en} Sync request
            int retCode = licenseWrapper.getLicenseWithParams(new HashMap<String, String>(), false, new com.effectsar.labcv.licenselibrary.LicenseCallback() {
                @Override
                public void execute(String retmsg, int retSize, int errorCode, String errorMsg) {
                    _errorCode = errorCode;
                    _errorMsg = errorMsg;
                }
            });
            if (retCode != 0) {
                _errorCode = retCode;
                _errorMsg = "{zh} jni注册失败，检查是否注入网络请求 {en} Jni registration failed, check whether the network request is injected";
            }

            if (!checkLicenseResult("getLicensePath"))
                return "";
        }

        return licenseWrapper.getParam("licensePath");
    }

    String getHalLicensePath() {
        try {
            String fileDir = "/sdcard/licenseExternal/";
            File file = new File(fileDir);

            if (file.listFiles().length == 1 && file.listFiles()[0].isFile()) {
                String externalLicense = file.listFiles()[0].toString();
                if (externalLicense.endsWith(".licbag")) {
                    return externalLicense;
                }
            }
        } catch( Exception e) {
            LogUtils.e("use package default license file to authentication");
        }
        return "";
    }

    @Override
    public String updateLicensePath() {
        _errorCode = 0;
        // {zh} 同步请求 {en} Sync request
        int retCode = licenseWrapper.updateLicenseWithParams(new HashMap<String, String>(), false, new com.effectsar.labcv.licenselibrary.LicenseCallback() {
            @Override
            public void execute(String retmsg, int retSize, int errorCode, String errorMsg) {
                _errorCode = errorCode;
                _errorMsg = errorMsg;
            }
        });

        if (retCode != 0) {
            _errorCode = retCode;
            _errorMsg = "{zh} jni注册失败，检查是否注入网络请求 {en} Jni registration failed, check whether the network request is injected";
        }

        if (!checkLicenseResult("updateLicensePath"))
            return "";

        return licenseWrapper.getParam("licensePath");
    }

    @Override
    public LICENSE_MODE_ENUM getLicenseMode() {
        return _licenseMode;
    }

    @Override
    public int getLastErrorCode() {
        return _errorCode;
    }

    private boolean checkLicenseResult(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME function_name) {
        String filename = licenseWrapper.getParam("licensePath");
        int ret = EffectsSDKLicenseWrapper.checkLicense(filename, function_name, _licenseMode == LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (ret != 0) {
            String log = "checkLicenseResult error: " + ret;
            LogUtils.e(log);
            String toast = RenderManager.formatErrorCode(ret);
            if (toast == null) {
                toast = log;
            }
            Intent intent = new Intent(Config.CHECK_RESULT_BROADCAST_ACTION);
            intent.putExtra("msg", toast);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
        return ret == 0;
    }
    public boolean checkLicenseResult(String msg) {
        if (_errorCode != 0) {
            String log = msg + " error: " + _errorCode;
            LogUtils.e(log);
            String toast = _errorMsg;
            if (toast == "") {
                toast = log;
            }
            Intent intent = new Intent(Config.CHECK_RESULT_BROADCAST_ACTION);
            intent.putExtra("msg", toast);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return false;
        }
        return true;
    }

    public boolean deleteCacheFile() {
        String filename = licenseWrapper.getParam("licensePath");
        if (filename.isEmpty())
            return true;

        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }

        return true;
    }

    private boolean licenseFileExists(){
        String filename = licenseWrapper.getParam("licensePath");
        if (filename.isEmpty())
            return false;

        File file = new File(filename);
        return file.exists() && file.isFile();
    }

    private String getResourcePath() {
        return mContext.getExternalFilesDir("assets").getAbsolutePath() + File.separator + RESOURCE;
    }

    public static void setKey(String key) {
        KEY = key;
    }

    public static void setSecret(String secret) {
        SECRET = secret;
    }
}
