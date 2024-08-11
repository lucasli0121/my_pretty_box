package com.effectsar.labcv.platform.utils

import android.text.TextUtils
import com.effectsar.labcv.platform.EffectsARPlatform
import com.effectsar.labcv.platform.struct.Material
import java.io.File

fun Material.exists(): Boolean {
    requirements.forEach {
        if (it.name.endsWith(".zip")) {
            val modelFile = File(EffectsARPlatform.getModelRootPath(), StringUtil.getFileNameFromZip(it.name))
            if (modelFile.exists().not()) {
                return false
            }
        } else {
            val modelFile = File(EffectsARPlatform.getModelRootPath(), it.name)
            if (modelFile.exists().not()) {
                return false
            }
            val realMD5 = FileUtils.getFileMD5ToString(modelFile)
            if (realMD5 != it.md5) {
                return false
            }
        }
    }
    if (video.isNotEmpty() && getVideoFile().exists().not()) {
        return false
    }
    val materialFile = getStorageFile()
    if (materialFile.exists().not()) {
        return false
    }
    return true
}

fun Material.getVideoFile(): File {
    return File(EffectsARPlatform.getVideoRootPath(), "${StringUtil.md5(video)}.mp4")
}

fun Material.getStorageFile(): File {

    var downloadRootPath = EffectsARPlatform.getResourceRootPath()
    var downloadFileName = fileName
    if (!TextUtils.isEmpty(extra.relative_path)) {
        downloadFileName = extra.relative_path
    }

    if (extra.is_model_resource){
        downloadRootPath = EffectsARPlatform.getModelRootPath()
        downloadFileName = extra.relative_path
    }
    return File(downloadRootPath, StringUtil.getFileNameFromZip(downloadFileName))

}

fun Material.getDownloadFile(): File {

    var downloadRootPath = EffectsARPlatform.getResourceRootPath()
    var downloadFileName = fileName
    if (!TextUtils.isEmpty(extra.relative_path)) {
        downloadFileName = extra.relative_path
    }

    if (extra.is_model_resource){
        downloadRootPath = EffectsARPlatform.getModelRootPath()
        downloadFileName = extra.relative_path
    }
    return File(downloadRootPath, downloadFileName)

}