package com.effectsar.labcv.platform.struct

enum class PlatformError(msg: String) {
    DOWNLOAD_ERROR("download_error"),
    NETWORK_NOT_AVAILABLE("network not available"),
    FILE_NOT_AVAILABLE("file not available"),
    UNZIP_ERROR("unzip error")
}