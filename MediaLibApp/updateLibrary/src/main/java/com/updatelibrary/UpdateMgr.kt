package com.updatelibrary

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import com.loopj.android.http.TextHttpResponseHandler
import android.widget.TextView
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.FileProvider
import com.updatalibrary.R
import cz.msebera.android.httpclient.Header
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap

class UpdateMgr(private val mContext: Context) : TextHttpResponseHandler() {
    private val appId = "com.media.demo"
    private var mHashMap: HashMap<String, String>? = null
    private var progress = 0
    private var mProgress: ProgressBar? = null
    private var _labelTxt: TextView? = null
    private var mDownloadDialog: Dialog? = null
    private var _cancel = false
    private var _savePath: String? = null
    private var _autoDown: Boolean = false
//    var serverUri = "http://192.168.1.102:8089/public/file/"
    var serverUri = "http://120.79.139.90:8089/public/file/"
    companion object {
        private const val DOWNLOAD = 1
        private const val DOWNLOAD_FINISH = 2
        private const val DOWNLOAD_CLOSE = 3
    }
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                DOWNLOAD -> {
                    if (mProgress != null) {
                        mProgress!!.progress = progress
                    }
                    if (_labelTxt != null) {
                        val txt = String.format("%d%%", progress)
                        _labelTxt!!.text = txt
                    }
                }
                DOWNLOAD_FINISH ->
                    installApkInRoot()
                DOWNLOAD_CLOSE -> if (mDownloadDialog != null) {
                    mDownloadDialog!!.dismiss()
                }
                else -> {
                }
            }
        }
    }

    fun checkUpdate(autoDown: Boolean) {
        _autoDown = autoDown
        HttpClientUtil[serverUri + "version.xml", null, this]
    }

    private fun getVersionCode(context: Context): Long {
        var versionCode = 0L
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode =
                    context.packageManager.getPackageInfo(appId, 0).longVersionCode.toInt().toLong()
            } else {
                versionCode = context.packageManager.getPackageInfo(appId, 0).versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    interface UpdateCheckListener {
        fun getId(): String
    }
    private fun isUpdate(xmlResponse: String): Boolean {
        val versionCode = getVersionCode(mContext)
        try {
            val inStream: InputStream = ByteArrayInputStream(xmlResponse.toByteArray())
            mHashMap = XmlParser.parseXml2(inStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (null != mHashMap) {
            val ver = mHashMap!!["version"]
            if (!ver.isNullOrEmpty()) {
                val serviceCode = Integer.valueOf(ver!!)
                if (serviceCode > versionCode) {
                    return true
                }
            }
        }
        return false
    }

    private fun showNoticeDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(R.string.app_update_title)
        builder.setMessage(R.string.app_update_info)
        builder.setPositiveButton(
            R.string.app_update_ok,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                showDownloadDialog()
            })
        // �Ժ����
        builder.setNegativeButton(
            R.string.app_update_cancel,
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        val noticeDialog: Dialog = builder.create()
        noticeDialog.show()
    }

    private fun showDownloadDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(R.string.app_updating)
        val inflater = LayoutInflater.from(mContext)
        val v: View = inflater.inflate(R.layout.app_update_progress, null)
        mProgress = v.findViewById<View>(R.id.update_progress) as ProgressBar
        mProgress!!.max = 100
        _labelTxt = v.findViewById<View>(R.id.label) as TextView
        builder.setView(v)
        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
            _cancel = true
        }
        mDownloadDialog = builder.create()
        (mDownloadDialog as AlertDialog)?.show()
        downloadApk()
    }

    private fun downloadApk() {
        downloadApkThread().start()
    }

    private inner class downloadApkThread : Thread() {
        override fun run() {
            try {
                val url = mHashMap!!["url"] ?: return
                _savePath = mContext?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
//                    val sdpath = Environment.getExternalStorageDirectory().toString() + "/"
//                    _savePath = sdpath + "download"
                var apkName = mHashMap!!["name"]
                if(apkName != null && url != null) {
                    val url = URL(url)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connect()
                    val length = conn.contentLength
                    val `is` = conn.inputStream
                    val file = File(_savePath)
                    if (!file.exists()) {
                        file.mkdir()
                    }

                    val apkFile = File(_savePath, apkName)
                    val fos = FileOutputStream(apkFile)
                    var count = 0
                    val buf = ByteArray(2*1024*1024)
                    do {
                        var numread = 0
                        try {
                            numread = `is`.read(buf)
                        } catch (e: Exception) {
                        }
                        if (numread < 0) {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH)
                            break
                        }
                        count += numread
                        progress = (count.toFloat() / length * 100).toInt()
                        mHandler.sendEmptyMessage(DOWNLOAD)
                        fos.write(buf, 0, numread)
                    } while (!_cancel)
                    fos.close()
                    `is`.close()
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mHandler.sendEmptyMessage(DOWNLOAD_CLOSE)
        }
    }

    /**
     */
    private fun installApk() {
        var apkName = mHashMap!!["name"]
        if(apkName != null) {
            val apkfile = File(_savePath, apkName)
            if (!apkfile.exists()) {
                return
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val fileUri: Uri = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                Uri.fromFile(apkfile)
            } else {
                FileProvider.getUriForFile(
                    mContext,
                    mContext.applicationContext.packageName + ".provider",
                    apkfile
                )
            }
            i.setDataAndType(fileUri, "application/vnd.android.package-archive")
            mContext.startActivity(i)
        }
    }
    private fun installApkInRoot() {
        var printWriter : PrintWriter? = null
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su")
            printWriter = PrintWriter(process.outputStream)
            val apkName = mHashMap!!["name"]
            if(apkName != null) {
                val apkFile = "${_savePath}/${apkName}"
                printWriter.println("pm install -r $apkFile")
                printWriter.flush()
                printWriter.close()
                delayRebootApp()
                process.waitFor()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun delayRebootApp() {
        var cmd = "sleep 1; am start -n $appId/.MainActivity"
        try {
            var process = Runtime.getRuntime().exec("su")
            var stream = DataOutputStream(process.outputStream)
            stream.writeBytes(cmd)
            stream.flush()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(arg0: Int, arg1: Array<Header?>?, arg2: String?, arg3: Throwable?) {
    }

    override fun onSuccess(statusCode: Int, headers: Array<Header?>?, responseString: String) {
        if (isUpdate(responseString)) {
            if(_autoDown) {
                downloadApk()
            } else {
                showNoticeDialog()
            }
        } else {
//            ShowToast.displayToast(mContext, R.string.no_new_version)
        }
    }
}