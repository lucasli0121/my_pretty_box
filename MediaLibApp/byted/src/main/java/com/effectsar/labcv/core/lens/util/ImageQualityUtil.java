package com.effectsar.labcv.core.lens.util;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

public class ImageQualityUtil {
    //   {zh} 高通处理器列表       {en} List of qualcomm processors  
    // https://zh.wikipedia.org/wiki/%E9%AB%98%E9%80%9A%E9%A9%8D%E9%BE%8D%E5%85%83%E4%BB%B6%E5%88%97%E8%A1%A8
    //   {zh} 视频超分目前只支持高通660以上设备       {en} Video super score currently only supports Qualcomm 660 + devices  
    public  static boolean isSupportVideoSR(Context context){
        String renderer = GLES20.glGetString(GL10.GL_RENDERER); // Adreno (TM) 540
        Log.e("tmp", "isSupportVideoSR:"+ renderer);

        int len = renderer.length();
        try {
            if (renderer.startsWith("Adreno")){
                if (renderer.endsWith("G") || renderer.endsWith("L")) {
                    renderer = renderer.substring(0, len - 1);
                }
                String versionStr = renderer.substring(len - 3);
                int versionInt = Integer.parseInt(versionStr);

                return versionInt >= 505;
            } else if (renderer.startsWith("Mali-G")) {
                int prefix_length = "Mali-G".length();
                String versionStr = renderer.substring(prefix_length, prefix_length + 2);
                Log.e("tmp",  versionStr);
                int versionInt = Integer.parseInt(versionStr);

                return versionInt >= 51;
            }
        } catch (Exception e){
            return false;
        }

        return false;
    }


    public static  boolean isMailG(){
        String renderer = GLES20.glGetString(GL10.GL_RENDERER); // Adreno (TM) 540
        Log.e("tmp", "isSupportVideoSR:"+ renderer);
        if (renderer.startsWith("Mali-G")) {
            int prefix_length = "Mali-G".length();
            String versionStr = renderer.substring(prefix_length, prefix_length + 2);
            Log.e("tmp",  versionStr);
            int versionInt = Integer.parseInt(versionStr);

            return versionInt >= 51;
        }
        return false;
    }

    public static  boolean isOsVersionHigherThan(int version) {
        String  versionStr = Build.VERSION.RELEASE;
        int index  = versionStr.indexOf('.');
        if (index != -1) {
            versionStr = versionStr.substring(0, index);
        }
        int main =  Integer.parseInt(versionStr);
        return main  >= version;
    }

    public static boolean isPixelSeriesDevices(){
        String deviceModel = android.os.Build.MODEL;
        return deviceModel.contains("Pixel") || deviceModel.contains("AOSP");
    }

    public static boolean isPhotoImageQualityNotSupport() {
        String deviceModel = android.os.Build.MODEL;
        return deviceModel.contains("Y67");
    }

    public static boolean isOpenCLSupport() {
        String deviceModel = android.os.Build.MODEL;
        return !deviceModel.contains("Y67") && !deviceModel.contains("V1901A");
    }
}
