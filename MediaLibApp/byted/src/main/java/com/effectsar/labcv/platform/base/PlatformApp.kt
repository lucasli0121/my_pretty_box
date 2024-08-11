package com.effectsar.labcv.platform.base

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object PlatformApp {
    @Volatile
    lateinit var instance: Context

    fun bindInstance(context: Context) {
        instance = context
    }
}