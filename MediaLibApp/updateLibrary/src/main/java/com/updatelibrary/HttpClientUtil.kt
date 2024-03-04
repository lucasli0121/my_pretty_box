package com.updatelibrary

import com.loopj.android.http.*
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

object HttpClientUtil {
    private val client: AsyncHttpClient = AsyncHttpClient()
    private var syncClient: SyncHttpClient? = null
    private val syncHttpClient: SyncHttpClient?
        private get() {
            synchronized(SyncHttpClient::class.java) {
                if (null == syncClient) {
                    syncClient = SyncHttpClient()
                    syncClient!!.setTimeout(120 * 1000)
                    syncClient!!.setMaxConnections(60)
                }
            }
            return syncClient
        }

    init {
        client.setTimeout(120 * 1000)
        client.maxConnections = 60
    }

    fun addHeader(header: String, value: String) {
        client.addHeader(header, value)
    }
    fun removeAllHeader() {
        client.removeAllHeaders()
    }
    /**
     * 访问数据库并返回JSON数据字符�?
     *
     * @param //params 向服务器端传的参�?
     * @param //url
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun toGetJsonInfo(urlPath: String?): String {
        val outStream = ByteArrayOutputStream()
        val data = ByteArray(1024)
        var len = 0
        val url = URL(urlPath)
        val conn = url.openConnection() as HttpURLConnection
        val inStream = conn.inputStream
        while (inStream.read(data).also { len = it } != -1) {
            outStream.write(data, 0, len)
        }
        inStream.close()
        return String(outStream.toByteArray()) //通过out.Stream.toByteArray获取到写的数�? 
    }

    operator fun get(url: String?, params: RequestParams?,
                     responseHandler: AsyncHttpResponseHandler?): RequestHandle {
        return client.get(url, params, responseHandler)
    }

    fun post(url: String?, params: RequestParams?,
             responseHandler: AsyncHttpResponseHandler?): RequestHandle {
        return client.post(url, params, responseHandler)
    }

    fun post(url: String?, params: RequestParams?, contentType: String,
             responseHandler: AsyncHttpResponseHandler?): RequestHandle {
        return client.post(null, url, params?.getEntity(responseHandler), contentType, responseHandler)
    }
    fun syncGet(url: String?, params: RequestParams?,
                responseHandler: AsyncHttpResponseHandler?): RequestHandle? {
        return syncHttpClient?.get(url, params, responseHandler)
    }

    fun syncPost(url: String?, params: RequestParams?,
                 responseHandler: AsyncHttpResponseHandler?): RequestHandle? {
        return syncHttpClient?.post(url, params, responseHandler)
    }


}