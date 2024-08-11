package com.effectsar.labcv.demo.task;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.effectsar.labcv.common.utils.FileUtils;
import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.effect.EffectManager;
import com.effectsar.labcv.core.effect.EffectResourceHelper;

import java.io.File;
import java.lang.ref.WeakReference;

public class UnzipTask extends AsyncTask<String, Void, Boolean> {
    public static final String DIR = "resource";

    public interface IUnzipViewCallback {
        Context getContext();
        void onStartTask();
        void onEndTask(boolean result);
    }

    private final WeakReference<IUnzipViewCallback> mCallback;

    public UnzipTask(IUnzipViewCallback callback) {
        mCallback = new WeakReference<>(callback);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        IUnzipViewCallback callback = mCallback.get();
        if (callback == null) return false;
        String path = strings[0];
        File dstFile = callback.getContext().getExternalFilesDir("assets");
        FileUtils.clearDir(new File(dstFile, path));

        if (Config.ENABLE_ASSETS_SYNC) {
            try {
                FileUtils.copyAssets(callback.getContext().getAssets(), path, dstFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            AssetManager assetManager = callback.getContext().getAssets();
            EffectResourceHelper effectResourceHelper = new EffectResourceHelper(callback.getContext());
            String[] filePathList = assetManager.list(path);
            for (String filePath : filePathList) {
                switch (filePath){
                    case "3DModelResource.bundle":
                        break;
                    case "ModelResource.bundle":
                        if (!EffectManager.USE_MODEL_FROM_ASSET) {
                            FileUtils.copyAssetsContent(callback.getContext().getAssets(), path, filePath, effectResourceHelper.getModelPath());
                        }
                        break;
                    case "ComposeMakeup.bundle":
                    case "FilterResource.bundle":
                    case "StickerResource.bundle":
                        FileUtils.copyAssetsContent(callback.getContext().getAssets(), path, filePath, effectResourceHelper.getMaterialPath());
                        break;
                    default:
                        FileUtils.copyAssets(callback.getContext().getAssets(), path, filePath, effectResourceHelper.getResourcePath());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

//        return FileUtils.unzipAssetFile(mCallback.get().getContext(), zipPath, dstFile);
    }

    @Override
    protected void onPreExecute() {
        IUnzipViewCallback callback = mCallback.get();
        if (callback == null) return;
        callback.onStartTask();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        IUnzipViewCallback callback = mCallback.get();
        if (callback == null) return;
        callback.onEndTask(result);
    }
}
