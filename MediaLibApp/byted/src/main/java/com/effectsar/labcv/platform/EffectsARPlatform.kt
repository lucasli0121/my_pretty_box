package com.effectsar.labcv.platform

import android.text.TextUtils
import android.util.Log
import com.effectsar.labcv.platform.api.MaterialDownloadListener
import com.effectsar.labcv.platform.base.AppData
import com.effectsar.labcv.platform.base.PlatformApp
import com.effectsar.labcv.platform.base.PlatformCoroutineScope
import com.effectsar.labcv.platform.config.EffectsARPlatformConfig
import com.effectsar.labcv.platform.download.DownloadManager
import com.effectsar.labcv.platform.download.OnProgressListener
import com.effectsar.labcv.platform.download.UnzipErrorException
import com.effectsar.labcv.platform.requester.ResourceRequester
import com.effectsar.labcv.platform.struct.CategoryData
import com.effectsar.labcv.platform.struct.DeferredResult
import com.effectsar.labcv.platform.struct.Material
import com.effectsar.labcv.platform.struct.PlatformError
import com.effectsar.labcv.platform.struct.PlatformError.DOWNLOAD_ERROR
import com.effectsar.labcv.platform.struct.PlatformError.NETWORK_NOT_AVAILABLE
import com.effectsar.labcv.platform.struct.PlatformError.UNZIP_ERROR
import com.effectsar.labcv.platform.struct.Requirement
import com.effectsar.labcv.platform.utils.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object EffectsARPlatform {
    private const val TAG = "EffectsARPlatform"
    private lateinit var config: EffectsARPlatformConfig

    private val scope by lazy {
        PlatformCoroutineScope()
    }

    private val coroutineDispatcher by lazy {
        Executors.newFixedThreadPool(3).asCoroutineDispatcher()
    }

    private val requester by lazy {
        ResourceRequester()
    }

    fun init(config: EffectsARPlatformConfig? = null) {
        config?.let {
            this.config = it
        } ?: kotlin.run {
            this.config = EffectsARPlatformConfig.Builder().build()
        }
        val context = PlatformApp.instance
        val savedVersionCode = AppData.getVersion(context)
        val currentVersionCode = AppUtils.getVersionCode(context)
        if (currentVersionCode <= savedVersionCode) {
            return
        }
        scope.launch(coroutineDispatcher) {
//            copyAssetsResToLocal()
        }
    }

    fun closeAll(){
        coroutineDispatcher.cancel()
    }

    fun fetchMaterial(material: Material, listener: MaterialDownloadListener? = null) {
        scope.launch(Dispatchers.Main) {
            getModelRootPath().let {
                if (it.exists().not()) {
                    it.mkdir()
                }
            }
            getResourceRootPath().let {
                if (it.exists().not()) {
                    it.mkdir()
                }
            }
            val materialSavePath = material.getStorageFile()

            // {zh} 判断资源是否存在 {en} Determine if the resource exists
            if (material.exists()) {
                listener?.onSuccess(material, materialSavePath.absolutePath)
                return@launch
            }

            // {zh} 判断网络状态 {en} Determine the network status
            if (NetworkUtils.getNetworkType(PlatformApp.instance)?.isAvailable?.not() == true) {
                listener?.onFailed(material, java.lang.IllegalStateException("Network is not available"), NETWORK_NOT_AVAILABLE)
                return@launch
            }
            val downloadAsyncList = ArrayList<Deferred<DeferredResult>>()

            // {zh} 下载模型文件 {en} Download the model file
            material.requirements.forEach { requirement ->
                addDownloadModel(this, downloadAsyncList, requirement)
            }

            // {zh} 下载依赖的视频 {en} Download dependent videos
            if (material.video.isNotEmpty()) {
                addDownloadVideo(this, downloadAsyncList, material)
            }

            // {zh} 下载特效资源 {en} Download special effects resources
            addDownloadMaterial(this, downloadAsyncList, materialSavePath, listener, material)

            val result = downloadAsyncList.awaitAll().find { !it.result }
            result?.let {
                listener?.onFailed(
                    material,
                    it.exception ?: java.lang.IllegalArgumentException("this should not be the case"),
                    it.platformError ?: DOWNLOAD_ERROR
                )
            } ?: kotlin.run {
                material.progress = 100
                listener?.onProgress(material, 100)
                listener?.onSuccess(material, materialSavePath.absolutePath)
            }
        }
    }

    private suspend fun fetchMaterialSuspend(material: Material): Boolean = suspendCoroutine { continuation ->
        fetchMaterial(material, object : MaterialDownloadListener {
            override fun onSuccess(material: Material, path: String) {
                continuation.resume(true)
            }

            override fun onProgress(material: Material, process: Int) {
            }

            override fun onFailed(material: Material, e: Exception, platformError: PlatformError) {
                continuation.resume(false)
            }
        })
    }

    suspend fun fetchMaterials(categoryData: CategoryData): Boolean {
        return withContext(coroutineDispatcher) {
            val downloadMaterialsAsyncList = ArrayList<Deferred<Boolean>>()
            categoryData.tabs.forEach {
                it.items.forEach {
                    downloadMaterialsAsyncList.add(async {
                        fetchMaterialSuspend(it)
                    })
                }
            }
            val result = downloadMaterialsAsyncList.awaitAll().find { !it }
            return@withContext result == null
        }
    }

    private fun addDownloadModel(
        scope: CoroutineScope,
        downloadAsyncList: ArrayList<Deferred<DeferredResult>>,
        requirement: Requirement
    ) {
        downloadAsyncList.add(scope.async(coroutineDispatcher) {
            Log.i(TAG, "start download model ${requirement.name}")
            try {
                val modelFile = File(getModelRootPath(), requirement.name)
                if (modelFile.exists()) {
                    // {zh} 文件存在的情况检查下MD5 {en} Check if the file exists under MD5.
                    val md5Valid = checkFileMD5(modelFile, requirement.md5)
                    if (md5Valid) {
                        return@async DeferredResult(true)
                    } else {
                        modelFile.delete()
                    }
                }
                val downloadResult = DownloadManager.download(requirement.url, File(getModelRootPath(), requirement.name))

                val result = if (requirement.name.endsWith(".zip")) {
                    // {zh} requirement是zip {en} The requirement is zip.
                    val dir = File(getModelRootPath(), StringUtil.getFileNameFromZip(requirement.name))
                    var materialUnzipDir = dir.parentFile
                    downloadResult.file?.let {
                        val zipList = ZipUtils.unzipFile(it, materialUnzipDir)
                        if (zipList.isNullOrEmpty()) {
                            return@let DeferredResult(false, UnzipErrorException("unzip ${dir.name} failed"), UNZIP_ERROR)
                        }
                        it.delete()
                        DeferredResult(true)
                    } ?: kotlin.run {
                        DeferredResult(false, downloadResult.e, DOWNLOAD_ERROR)
                    }
                } else {
                    // {zh} requirement是model {en} Requirement is model
                    downloadResult.file?.let {
                        DeferredResult(true)
                    } ?: kotlin.run {
                        DeferredResult(false, downloadResult.e, DOWNLOAD_ERROR)
                    }
                }
                result
            } catch (e: IOException) {
                e.printStackTrace()
                DeferredResult(false, e, DOWNLOAD_ERROR)
            }
        })
    }

    private fun addDownloadMaterial(
        scope: CoroutineScope,
        downloadAsyncList: ArrayList<Deferred<DeferredResult>>,
        materialSaveDir: File,
        listener: MaterialDownloadListener?,
        material: Material
    ) {
        val downloadMaterial = scope.async(coroutineDispatcher) {
            Log.i(TAG, "start download material ${material.fileName}")
            try {
                if (materialSaveDir.exists()) {
                    return@async DeferredResult(true)
                }
                val downloadFileResult = DownloadManager.download(
                    material.url,
                    material.getDownloadFile(),
                    object : OnProgressListener {
                        override fun onProgress(process: Int) {
                            runOnUiThread {
                                material.progress = process
                                listener?.onProgress(material, process)
                            }
                        }
                    }
                )

                downloadFileResult.file?.let {
                    if (downloadFileResult.file.toString().endsWith(".zip")) {
                        var materialUnzipDir = materialSaveDir
                        if (material.extra.is_model_resource) {
                            materialUnzipDir = materialSaveDir.parentFile
                        }
                        val zipList = ZipUtils.unzipFile(it, materialUnzipDir)
                        if (zipList.isNullOrEmpty()) {
                            return@let DeferredResult(false, UnzipErrorException("unzip ${material.fileName} failed"), UNZIP_ERROR)
                        }
                        it.delete()
                        DeferredResult(true)
                    } else {
                        DeferredResult(true)
                    }
                } ?: kotlin.run {
                    DeferredResult(false, downloadFileResult.e, DOWNLOAD_ERROR)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                DeferredResult(false, e, DOWNLOAD_ERROR)
            }
        }
        downloadAsyncList.add(downloadMaterial)
    }

    private fun addDownloadVideo(
        scope: CoroutineScope,
        downloadAsyncList: ArrayList<Deferred<DeferredResult>>,
        material: Material
    ) {
        val downloadVideo = scope.async(coroutineDispatcher) {
            val videoSaveFile = material.getVideoFile()
            Log.i(TAG, "start download video ${videoSaveFile.name}")
            try {
                if (videoSaveFile.exists()) {
                    return@async DeferredResult(true)
                }
                val downloadResult = DownloadManager.download(material.video, videoSaveFile)
                val result = downloadResult.file?.let {
                    DeferredResult(true)
                } ?: kotlin.run {
                    DeferredResult(false, downloadResult.e, DOWNLOAD_ERROR)
                }
                result
            } catch (e: IOException) {
                e.printStackTrace()
                DeferredResult(false, e, DOWNLOAD_ERROR)
            }
        }
        downloadAsyncList.add(downloadVideo)
    }

    suspend fun fetchCategoryDataSuspend(
        accessKey: String,
        categoryKey: String,
        panelKey: String = "",
    ): CategoryData? {
        return requester.fetchCategoryData(accessKey, categoryKey, panelKey)
    }

    fun fetchCategoryData(
        accessKey: String,
        categoryKey: String,
        panelKey: String = "",
        callback: (CategoryData?) -> Unit
    ) {
        scope.launch {
            try {
                val data = requester.fetchCategoryData(accessKey, categoryKey, panelKey)
                callback.invoke(data)
            } catch (e: Exception) {
                callback.invoke(null)
                e.printStackTrace()
            }
        }
    }

    fun clear(endAction: (() -> Unit)? = null) {
        scope.launch {
            withContext(coroutineDispatcher) {
                config.resourcePath.deleteRecursively()
//                copyAssetsResToLocal()
            }
            endAction?.invoke()
        }
    }

    fun getResourceRootPath() = File(config.resourcePath, "material")

    fun getVideoRootPath() = File(config.resourcePath, "video")

    fun getModelRootPath() = File(config.resourcePath, "model")

    fun getConfig() = config

    private fun checkFileMD5(file: File, md5: String): Boolean {
        val realMD5 = FileUtils.getFileMD5ToString(file)
        return realMD5 == md5
    }

    private fun copyAssetsResToLocal() {
        try {
            Log.i(TAG, "copyAssetsResToLocal")
            val destDirPath = PlatformApp.instance.getExternalFilesDir("")!!.absolutePath
            FileUtils.copyAssets(PlatformApp.instance.assets, "resource", destDirPath)
            AppData.setVersion(PlatformApp.instance, AppUtils.getVersionCode(PlatformApp.instance))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}