package com.effectsar.labcv.platform.download

import java.io.File

data class DownloadResult(
    val file: File?,
    val e: Exception?
)