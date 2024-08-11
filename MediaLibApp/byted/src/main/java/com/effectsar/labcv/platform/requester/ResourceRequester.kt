package com.effectsar.labcv.platform.requester

import com.effectsar.labcv.platform.EffectsARPlatform
import com.effectsar.labcv.platform.download.CommonParamsInterceptor
import com.effectsar.labcv.platform.struct.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author liutianyu.39
 * @since 2022/10/11
 *
 */
class ResourceRequester {

    companion object {
        private const val MATERIAL_CATE_URI = "v1/config"
    }

    private val baseRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(EffectsARPlatform.getConfig().url)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .client(
                OkHttpClient.Builder().addInterceptor(CommonParamsInterceptor()).build()
            )
            .build()
    }

    // material list in [category]
    suspend fun fetchCategoryData(accessKey: String, categoryKey: String, panelKey: String): CategoryData? {
        try {
            val qMap = mutableMapOf("category_key" to categoryKey)
            if (panelKey.isNotEmpty()) {
                qMap["panel_key"] = panelKey
            }
            val service = baseRetrofit.create(ConfigService::class.java)
            val materialResponse = retrofitRequest { service.getMaterialCategory(accessKey, qMap) } ?: return null
            if (materialResponse.code != 0) {
                return null
            }
            return materialResponse.data
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private suspend fun <T> retrofitRequest(getFun: (() -> Call<T>)): T? = suspendCoroutine { continuation ->
        getFun.invoke().enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>, response: retrofit2.Response<T>
            ) {
                continuation.resume(response.body())
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resume(null)
            }
        })
    }

    interface ConfigService {
        @GET(MATERIAL_CATE_URI)
        fun getMaterialCategory(@Header("access-key") ak: String, @QueryMap qMap: Map<String, String>): Call<ConfigResponse>
    }
}