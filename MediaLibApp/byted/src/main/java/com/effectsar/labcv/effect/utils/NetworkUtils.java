package com.effectsar.labcv.effect.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetworkUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient mClient;
    private DownloadProgressListener mDownloadProgressListener;

    public NetworkUtils() {
        mClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void setDownloadProgressCallback(DownloadProgressListener listener) {
        mDownloadProgressListener = listener;
    }

    public String postWithJson(String url, String json, Map<String, String> headerMap) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (headerMap != null){
            for (String key : headerMap.keySet()) {
                requestBuilder.addHeader(key, headerMap.get(key));
            }
        }
        requestBuilder.post(RequestBody.create(JSON, json));
        Request request = requestBuilder.build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    public String downloadFileWithJson(String url, String json, String filePath) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding","*/*")
                .post(RequestBody.create(JSON, json))
                .build();

        return downloadFile(request, filePath);
    }

    public String getdownloadFileWithJson(String url, String filePath) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding","*/*")
//                .addHeader("x-use-ppe","1")
//                .addHeader("x-tt-env","ppe_tob_qr_preview")
                .build();

        return downloadFile(request, filePath);
    }

    public String downloadFile(String url, String filePath) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return downloadFile(request, filePath);
    }

    private String downloadFile(Request request, String filePath) throws IOException {
        Response response = mClient.newCall(request).execute();
        assert response.body() != null;
        InputStream is = response.body().byteStream();

        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (response.code() != 200) {
                return "Request failed:"+response.message()+"("+response.code()+")";
            }
            long total = response.body().contentLength();
            long count = 0;
            int consumed;
            byte[] bytes = new byte[1024 * 8];
            while ((consumed = is.read(bytes)) > 0) {
                fos.write(bytes, 0, consumed);
                count += consumed;
                if (mDownloadProgressListener != null) {
                    mDownloadProgressListener.onProgressUpdate(1.f * count / total);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "save file error";
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

        return null;
    }

    public String downloadRemoteResourceConfig(String url,String filePath, String systemLanguage,String appVersion, String resourceType) throws IOException {

        RequestBody body = new FormBody.Builder()
                .add("system_version", "Android")
                .add("system_language", systemLanguage)
                .add("app_version",appVersion)
                .add("resource_type",resourceType)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        assert response.body() != null;
        InputStream is = response.body().byteStream();

        File file = new File(filePath);
        file.createNewFile();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            long total = response.body().contentLength();
            long count = 0;
            int consumed;
            byte[] bytes = new byte[1024 * 8];
            while ((consumed = is.read(bytes)) > 0) {
                fos.write(bytes, 0, consumed);
                count += consumed;
//                if (mDownloadProgressListener != null) {
//                    mDownloadProgressListener.onProgressUpdate(1.f * count / total);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "save file error";
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

        assert response.body() != null;
        return null;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public interface DownloadProgressListener {
        void onProgressUpdate(float progress);
    }
}
