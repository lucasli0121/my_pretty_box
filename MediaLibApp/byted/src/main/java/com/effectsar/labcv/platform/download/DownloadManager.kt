package com.effectsar.labcv.platform.download

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

object DownloadManager {
    private const val TAG = "DownloadManager"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /** {zh} 
     * 同步请求
     */
    /** {en} 
     * Synchronization request
     */
    fun download(url: String, saveFile: File, listener: OnProgressListener? = null): DownloadResult {
        val request: Request = Request.Builder()
            .url(url)
            .build()

        val parentFile = File(saveFile.parent ?: "")
        if (parentFile.exists().not()) {
            parentFile.mkdirs()
        } else {
            saveFile.createNewFile()
        }
        try {
            // {zh} 同步请求 {en} Synchronization request
            val originalResponse = okHttpClient.newCall(request).execute()
            val body = originalResponse.body ?: return DownloadResult(null, FileNotAvailableException("response body is null"))
            val contentLength = body.contentLength()
            val source: BufferedSource = body.source()
            val sink: BufferedSink = saveFile.sink().buffer()
            val sinkBuffer: Buffer = sink.buffer
            var totalBytesRead: Long = 0
            val bufferSize = 8 * 1024
            var bytesRead: Long
            while (source.read(sinkBuffer, bufferSize.toLong()).also { bytesRead = it } != -1L) {
                sink.emit()
                totalBytesRead += bytesRead
                val progress = (totalBytesRead * 99 / contentLength).toInt()
                listener?.onProgress(progress)
            }
            sink.flush()
            sink.close()
            source.close()
            return DownloadResult(saveFile, null)
        } catch (e: IOException) {
            Log.i(TAG, e.toString())
            return DownloadResult(null, e)
        }
    }
}