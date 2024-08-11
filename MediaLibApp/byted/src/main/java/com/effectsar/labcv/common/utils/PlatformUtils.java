package com.effectsar.labcv.common.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.platform.EffectsARPlatform;
import com.effectsar.labcv.platform.api.MaterialDownloadListener;
import com.effectsar.labcv.platform.config.EffectsARPlatformConfig;

import com.effectsar.labcv.core.effect.EffectResourceHelper;
import com.effectsar.labcv.platform.struct.CategoryData;
import com.effectsar.labcv.platform.struct.CategoryTabItem;
import com.effectsar.labcv.platform.struct.Material;
import com.effectsar.labcv.platform.struct.PlatformError;
import com.effectsar.labcv.platform.utils.ExtensionKt;
import com.effectsar.labcv.platform.utils.StringUtil;
import com.effectsar.labcv.core.util.LogUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;


public class PlatformUtils {

    private static Context mAppContext = null;
    private static final ConcurrentSkipListSet<String> mCategoryDownloadingSet = new ConcurrentSkipListSet<>();
    private static final ConcurrentSkipListSet<String> mMaterialDownloadingSet = new ConcurrentSkipListSet<>();

    public static void init(Context context){
            mAppContext = context;
       // ppe
        EffectsARPlatform.INSTANCE.init(
                new EffectsARPlatformConfig.Builder()
                        .appVersion("4.5.2")
                        .language(LocaleUtils.getPlatformLangParam(mAppContext))
                        .url("https://cv.iccvlog.com/")
                        .channel("1")
                        .build()
        );
    }

    public static void fetchCategoryData(String categoryKey, CategoryFetchListener categoryFetchListener){
        if (mAppContext == null) {
            return;
        }
        if (!mCategoryDownloadingSet.contains(categoryKey)) {
            mCategoryDownloadingSet.add(categoryKey);
            EffectsARPlatform.INSTANCE.fetchCategoryData(Config.ASSESS_KEY, categoryKey, Config.PANEL_KEY, categoryData -> {
                if (categoryData == null) {
                    mCategoryDownloadingSet.remove(categoryKey);
                    return null;
                }
                categoryFetchListener.onCategoryFetched(categoryData);
                mCategoryDownloadingSet.remove(categoryKey);
                return null;
            });
        }
    }

    public static void fetchCategoryDataWithCache(String categoryKey, CategoryFetchListener categoryFetchListener){
        if (mAppContext == null) {
            return;
        }
        File configPath = new File(new EffectResourceHelper(mAppContext).getConfigPath());
        if (!configPath.exists()) {
            configPath.mkdir();
        }
        String storedConfigMd5 = getStoredConfigMd5(categoryKey);
        if (!TextUtils.isEmpty(storedConfigMd5)) {
            CategoryData storedCategoryData = getStoredCategoryData(categoryKey);
            categoryFetchListener.onCategoryFetched(storedCategoryData);
        }

        if (!mCategoryDownloadingSet.contains(categoryKey)) {
            mCategoryDownloadingSet.add(categoryKey);
            EffectsARPlatform.INSTANCE.fetchCategoryData(Config.ASSESS_KEY, categoryKey, Config.PANEL_KEY, categoryData -> {
                if (categoryData == null) {
                    return null;
                }
                if (TextUtils.isEmpty(storedConfigMd5)) {
                    categoryFetchListener.onCategoryFetched(categoryData);
                }

                updateStoredConfig(categoryKey, categoryData);

                mCategoryDownloadingSet.remove(categoryKey);
                return null;
            });
        }
    }

    public static void fetchCategoryMaterial(String categoryKey, CategoryMaterialFetchListener listener){

        if (mAppContext == null) {
            return;
        }
        File configPath = new File(new EffectResourceHelper(mAppContext).getConfigPath());
        if (!configPath.exists()) {
            configPath.mkdir();
        }

        LogUtils.e("check "+ categoryKey + " contained");
        if (!mCategoryDownloadingSet.contains(categoryKey)) {
            mCategoryDownloadingSet.add(categoryKey);
            LogUtils.e("add "+ categoryKey + " to set");

            String storedConfigMd5 = getStoredConfigMd5(categoryKey);
            if (!TextUtils.isEmpty(storedConfigMd5)) {
                CategoryData storedCategoryData = getStoredCategoryData(categoryKey);
                traversalDownloading(categoryKey, storedCategoryData, listener);
            }

            EffectsARPlatform.INSTANCE.fetchCategoryData(Config.ASSESS_KEY, categoryKey, Config.PANEL_KEY, categoryData -> {
                if (categoryData == null) {
                    mCategoryDownloadingSet.remove(categoryKey);
                    LogUtils.e("onFail 0 remove"+ categoryKey + " from set");
                    listener.onFailed();
                    return null;
                }

                if (TextUtils.isEmpty(storedConfigMd5)) {
                    traversalDownloading(categoryKey, categoryData, listener);
                }
                updateStoredConfig(categoryKey, categoryData);
                return null;
            });
        }

    }

    private static void traversalDownloading(String categoryKey, CategoryData categoryData, CategoryMaterialFetchListener listener){
        if (categoryData == null || categoryData.getTabs().isEmpty()) {
            EffectsARPlatform.INSTANCE.closeAll();
            mCategoryDownloadingSet.remove(categoryKey);
            LogUtils.e("onFail 1 remove"+ categoryKey + " from set");
            listener.onFailed();
            return;
        }
        int materialsCount = 0;
        Set<String> dirSet = new HashSet<>();
        List<Material> materialSet = new LinkedList<>();
        for (CategoryTabItem categoryTabItem : categoryData.getTabs()) {
            for (Material material : categoryTabItem.getItems()) {
                String dir = ExtensionKt.getStorageFile(material).getAbsolutePath();
                if ( TextUtils.equals(material.getFileName(), "placeholder.zip") ||
                        !dirSet.contains(dir) ) {
                    dirSet.add(dir);
                    materialSet.add(material);
                }
            }
        }
        materialsCount = materialSet.size();
        LogUtils.d("EffectsARPlatform materialsCount: " + materialsCount);
        if (materialsCount == 0) {
            return;
        }

        final int[] successCount = {0};
        final int[] failCount = {0};
        boolean uncachedMaterialAppeared = false;
        for (Material material : materialSet) {
            int finalMaterialsCount = materialsCount;

            if (!uncachedMaterialAppeared && !ExtensionKt.exists(material)) {
                listener.onStart();
                uncachedMaterialAppeared = true;
            }

            EffectsARPlatform.INSTANCE.fetchMaterial(material, new MaterialDownloadListener() {

                @Override
                public void onSuccess(@NonNull Material material, @NonNull String path) {
                    listener.onMaterialFetchSuccess(material, path);
                    ++successCount[0];
                    if (successCount[0] == finalMaterialsCount && failCount[0] == 0) {
                        mCategoryDownloadingSet.remove(categoryKey);
                        LogUtils.e("onSuccess remove"+ categoryKey + " from set");
                        listener.onSuccess(categoryData);
                    } else if (successCount[0] < finalMaterialsCount) {
                        if (successCount[0] + failCount[0] < finalMaterialsCount) {
                            int percent = (successCount[0] * 100) / finalMaterialsCount;
                            listener.onProgress(percent);
                        } else {
                            EffectsARPlatform.INSTANCE.closeAll();
                            mCategoryDownloadingSet.remove(categoryKey);
                            LogUtils.e("onFail 2 remove"+ categoryKey + " from set");
                            listener.onFailed();
                        }
                    }
                }

                @Override
                public void onProgress(@NonNull Material material, int progress) {
                }

                @Override
                public void onFailed(@NonNull Material material, @NonNull Exception e, @NonNull PlatformError platformError) {
                    ++failCount[0];
                    EffectsARPlatform.INSTANCE.closeAll();
                    mCategoryDownloadingSet.remove(categoryKey);
                    LogUtils.e("onFail 3 remove"+ categoryKey + " from set");
                    listener.onFailed();
                    LogUtils.e("EffectsARPlatform " + material.getFileName() + " failed: " +  platformError + " " + e);
                }
            });
        }
    }

    public static void fetchMaterial(Material material, MaterialFetchListener materialFetchListener){
        if (mAppContext == null) {
            return;
        }
        if (!mMaterialDownloadingSet.contains(material.getId())) {
            mMaterialDownloadingSet.add(material.getId());

            if (!ExtensionKt.exists(material)) {
                material.setDownloading(true);
            }
            materialFetchListener.onStart(material);
            EffectsARPlatform.INSTANCE.fetchMaterial(material, new MaterialDownloadListener() {
                @Override
                public void onSuccess(@NonNull Material material, @NonNull String path) {
                    material.setDownloading(false);
                    materialFetchListener.onSuccess(material, path);
                    mMaterialDownloadingSet.remove(material.getId());
                }

                @Override
                public void onProgress(@NonNull Material material, int i) {
                    materialFetchListener.onProgress(material, i);
                }

                @Override
                public void onFailed(@NonNull Material material, @NonNull Exception e, @NonNull PlatformError platformError) {
                    material.setDownloading(false);
                    materialFetchListener.onFailed(material, e, platformError);
                    mMaterialDownloadingSet.remove(material.getId());
                }
            });
        }
    }

    private static String getStoredConfigMd5(String categoryKey){
        String configStart = categoryKey + "-" + LocaleUtils.getPlatformLangParam(mAppContext);
        File configPath = new File(new EffectResourceHelper(mAppContext).getConfigPath());
        File[] files = configPath.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && !pathname.isHidden() && pathname.getName().startsWith(configStart);
            }
        });
        String storedMd5 = "";
        for (File file : files) {
            String fileName = file.getName();
            if ( (fileName != null) && (fileName.length() > 0)) {
                int dot = fileName.lastIndexOf('.');
                int hyphen = fileName.lastIndexOf('-');
                if ( (dot > -1) && (dot < fileName.length()) &&
                        (hyphen > -1) && (hyphen < fileName.length()) &&
                        dot >= hyphen
                ) {
                    storedMd5 = fileName.substring(hyphen+1, dot);
                }
            }
        }
        return storedMd5;
    }

    private static File getStoredConfig(String categoryKey){
        String configStart = categoryKey + "-" + LocaleUtils.getPlatformLangParam(mAppContext);
        File configPath = new File(new EffectResourceHelper(mAppContext).getConfigPath());
        File[] files = configPath.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && !pathname.isHidden() && pathname.getName().startsWith(configStart);
            }
        });
        if (files.length > 0) {
            return files[0];
        }
        return null;
    }

    private static CategoryData getStoredCategoryData(String categoryKey){
        File storedConfigFile = getStoredConfig(categoryKey);
        if (storedConfigFile == null) {
            return null;
        }
        FileInputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            inputStream = new FileInputStream(storedConfigFile);
            int BUFFER_SIZE = 8192;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,StandardCharsets.UTF_8), BUFFER_SIZE);
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String storedConfig = sb.toString();
        return new Gson().fromJson(storedConfig, CategoryData.class);
    }

    private static boolean updateStoredConfig(String categoryKey, CategoryData categoryData){
        String categoryDataJson = new Gson().toJson(categoryData, CategoryData.class);
        String categoryDataJsonMd5 =  StringUtil.INSTANCE.md5(categoryDataJson);
        File configPath = new File(new EffectResourceHelper(mAppContext).getConfigPath());
        String storedConfigMd5 = getStoredConfigMd5(categoryKey);
        boolean configChanged = false;
        if (!TextUtils.equals(storedConfigMd5, categoryDataJsonMd5)) {
            configChanged = true;
            LogUtils.e("config changed!");
            LogUtils.e("old md5: " + storedConfigMd5);
            LogUtils.e("new md5: " + categoryDataJsonMd5);
            String configStart = categoryKey + "-" + LocaleUtils.getPlatformLangParam(mAppContext);
            File[] files = configPath.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && !pathname.isHidden() && pathname.getName().startsWith(configStart);
                }
            });
            for (File file : files) {
                file.delete();
            }

            FileOutputStream fos = null;
            try {
                File updateConfigFile = new File(configPath, configStart + "-" + categoryDataJsonMd5 + ".json");
                fos = new FileOutputStream(updateConfigFile,false);
                fos.write(categoryDataJson.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configChanged;
    }

    public static boolean isDownloading(String categoryKey){
        return mCategoryDownloadingSet.contains(categoryKey);
    }

    public static boolean isCategoryCached(String categoryKey){
        File configPath = new File(new EffectResourceHelper(mAppContext).getConfigPath());
        if (!configPath.exists()) {
            configPath.mkdir();
        }
        String md5 = getStoredConfigMd5(categoryKey);
        return !TextUtils.isEmpty(md5);
    }

    public static File getModelRootPath(){
        return EffectsARPlatform.INSTANCE.getModelRootPath();
    }

    public interface CategoryFetchListener{
        void onCategoryFetched(@NonNull CategoryData categoryData);
    }

    public interface CategoryMaterialFetchListener{
        void onStart();
        void onSuccess(CategoryData categoryData);
        void onMaterialFetchSuccess(@NonNull Material material, @NonNull String path);
        void onProgress(int i);
        void onFailed();
    }

    public interface MaterialFetchListener{
        void onStart(@NonNull Material material);
        void onSuccess(@NonNull Material material, @NonNull String path);
        void onProgress(@NonNull Material material, int i);
        void onFailed(@NonNull Material material, @NonNull Exception e, @NonNull PlatformError platformError);
    }

}
