package com.effectsar.labcv.platform.download

import com.effectsar.labcv.platform.EffectsARPlatform
import com.effectsar.labcv.platform.config.EffectsARPlatformConfig
import okhttp3.Interceptor
import okhttp3.Response

class CommonParamsInterceptor : Interceptor {

    private var retryNum = 0
    private var maxRetry = 3

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        val newRequestBuilder = oldRequest.newBuilder()
        val config = EffectsARPlatform.getConfig()

        if (config.envKey.isNotEmpty()) {
            newRequestBuilder.addHeader("x-tt-env", config.envKey)
        }
        if (config.ppeKey.isNotEmpty()) {
            newRequestBuilder.addHeader("x-use-ppe", config.ppeKey)
        }

        val oldUrlBuilder = oldRequest.url.newBuilder().apply {
            scheme(oldRequest.url.scheme)
            host(oldRequest.url.host)
            if (config.channel.isNotEmpty()) {
                addQueryParameter("channel", config.channel)
            }
            addQueryParameter("platform", "android")
            addQueryParameter("app_version", config.appVersion)
            addQueryParameter("system_language", config.language)
        }
        val newRequest = newRequestBuilder.url(oldUrlBuilder.build()).build()

        var response = chain.proceed(newRequest)
        while (!response.isSuccessful && retryNum < maxRetry - 1) {
            retryNum++
            response = chain.proceed(newRequest)
        }
        return response
    }
}