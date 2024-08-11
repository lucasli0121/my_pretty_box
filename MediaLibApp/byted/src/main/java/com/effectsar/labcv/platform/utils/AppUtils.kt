package com.effectsar.labcv.platform.utils

import android.content.Context
import android.content.pm.PackageManager

object AppUtils {
    fun getVersionCode(context: Context): Long {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }
}