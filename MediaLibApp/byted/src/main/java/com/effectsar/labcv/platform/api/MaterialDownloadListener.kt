package com.effectsar.labcv.platform.api

import com.effectsar.labcv.platform.struct.Material
import com.effectsar.labcv.platform.struct.PlatformError

interface MaterialDownloadListener {
    fun onSuccess(material: Material, path: String)
    fun onProgress(material: Material, process: Int)
    fun onFailed(material: Material, e: Exception, platformError: PlatformError)
}