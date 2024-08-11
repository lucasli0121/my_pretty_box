package com.effectsar.labcv.platform.struct

data class DeferredResult(
    val result: Boolean,
    val exception: Exception? = null,
    val platformError: PlatformError? = null
)