package com.effectsar.labcv.demo.task;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.effectsar.labcv.common.utils.LocaleUtils;
import com.effectsar.labcv.core.license.EffectLicenseHelper;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.effect.EffectResourceHelper;
import com.effectsar.labcv.demo.model.UserData;
import com.effectsar.labcv.effectsdk.RenderManager;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.lang.ref.WeakReference;

public class RequestLicenseTask extends AsyncTask<String, Void, Boolean> {
    public interface ILicenseViewCallback {
        Context getContext();
        void onStartTask();
        void onEndTask(boolean result);
    }

    private final ILicenseViewCallback mCallback;

    public RequestLicenseTask(ILicenseViewCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        ILicenseViewCallback callback = mCallback;
        if (callback == null) return false;

        try {
            EffectResourceHelper resourceHelper = new EffectResourceHelper(callback.getContext());
            EffectLicenseHelper.overSeasVersion = LocaleUtils.isOverseaLicenseCheck(callback.getContext());
            EffectLicenseHelper.Online_or_offline_model(callback.getContext());
            EffectLicenseHelper licenseHelper = EffectLicenseHelper.getInstance(callback.getContext());
            if (licenseHelper.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.OFFLINE_LICENSE)
                return true;

            callback.getContext();
            ActivityManager am = (ActivityManager) callback.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo ci = am.getDeviceConfigurationInfo();
            int renderapi = (ci.reqGlEsVersion >= 0x30000)?1:0;

            SharedPreferences sharedPreferences = callback.getContext().getSharedPreferences("license_info_tag", 0);
            boolean isEditingMode = sharedPreferences.getBoolean("isEditingMode", false);
            String filePath = licenseHelper.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.EFFECT);

            int overseaVersionCode = sharedPreferences.getInt("overSeasVersion", 0);
            boolean overSeasChanged = overseaVersionCode != 0 && overseaVersionCode != (EffectLicenseHelper.overSeasVersion ? 1 : 2);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (EffectLicenseHelper.overSeasVersion) {
                editor.putInt("overSeasVersion", 1);
                editor.apply();
            } else {
                editor.putInt("overSeasVersion", 2);
                editor.apply();
            }

            int savedVersionCode = UserData.getInstance(callback.getContext()).getVersion();
            int currentVersionCode = getVersionCode();
            /*  {zh} app版本升级时，删除本地缓存的license，重新拉取在线的license    {en} When the app version is upgraded, delete the locally cached license and re-pull the online license. */
            if (isEditingMode || savedVersionCode < currentVersionCode || overSeasChanged) {
                licenseHelper.deleteCacheFile();
                filePath = licenseHelper.updateLicensePath();
            }

            return licenseHelper.getLastErrorCode() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        ILicenseViewCallback callback = mCallback;
        if (callback == null) return;
        callback.onStartTask();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        ILicenseViewCallback callback = mCallback;
        if (callback == null) return;
        callback.onEndTask(result);
    }

    private int getVersionCode() {
        Context context = mCallback.getContext();
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
