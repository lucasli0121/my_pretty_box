package com.effectsar.labcv.platform.base

import android.content.Context

object AppData {
    private const val NAME = "thrall_platform"
    private const val VERSION = "versionCode"

    fun getVersion(context: Context): Long {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getLong(VERSION, 0L)
    }

    fun setVersion(context: Context, version: Long) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putLong(VERSION, version).apply()
    }
}