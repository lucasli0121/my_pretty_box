package com.media.demo.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.rastermill.FrameSequenceDrawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.media.demo.R
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

object AssetFile {
    private val Tag = "AssetFile"
    private const val ver = 2
    private const val BUFFER_SIZE = 1024 * 400
    private const val pkgName = "com.media.demo"
    fun assetSdPath(context: Context?): String {
        return context?.getExternalFilesDir(null)?.absolutePath + "/";
    }

    fun getMediaLogFile(context: Context): String {
        return assetSdPath(context) + "medialib.log"
    }
    fun getInputVideoFile(context: Context): String {
        return assetSdPath(context) + "test.264"
    }

    fun getOutputVideoFile(context: Context): String {
        return assetSdPath(context) + "output.yuv"
    }

    fun readVersion(context: Context?): String {
        var ver = "pad_std"
        try {
            val fIn = FileInputStream(assetSdPath(context) + "/version")
            val bRd = BufferedReader(InputStreamReader(fIn))
            val line = bRd.readLine()
            val ary = line.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (ary[0].equals("version", ignoreCase = true)) {
                ver = ary[1]
            }
            bRd.close()
            fIn.close()
        } catch (e: Exception) {
        }
        return ver
    }
    fun getPemStream(context: Context) : InputStream {
        val assetMgr: AssetManager = context.resources.assets
        return assetMgr.open("cert/cert.pem")
    }
    fun getFonts(context: Context): Typeface {
        val assetMgr: AssetManager = context.resources.assets
        return Typeface.createFromAsset(assetMgr, "fonts/SourceHanSansCN-Heavy.otf")
    }

    fun loadWebp(context: Context, fileName: String, img: ImageView, finishedLisenter: FrameSequenceDrawable.OnFinishedListener?) : Boolean {
        try {
            val assetMgr: AssetManager = context.resources.assets
            var inputSteam = assetMgr.open(fileName)
            var draw = FrameSequenceDrawable(inputSteam)
            draw.setLoopCount(-1)
            draw.setLoopBehavior(FrameSequenceDrawable.LOOP_FINITE)
            draw.setOnFinishedListener(finishedLisenter)
            img.setImageDrawable(draw)
        }catch (e: Exception) {
            Log.e(Tag, e.toString())
            return false
        } catch (e: java.lang.UnsatisfiedLinkError) {
            Log.e(Tag, e.toString())
            return false
        }
        return true
    }
    fun cleanAssetFileInSd(context: Context?) {
        if (ver == 2) {
            return
        } else {
            val sdFilePath = File(assetSdPath(context) + "/")
            removeAllFiles(sdFilePath)
        }
    }

    fun removeAllFiles(file: File) {
        if (ver == 2) {
            return
        }
        if (file.exists() && file.isDirectory) {
            val files = file.listFiles()
            for (i in files.indices) {
                if (files[i].isFile) {
                    try {
                        files[i].delete()
                    } catch (e: Exception) {
                    }
                } else if (files[i].isDirectory) {
                    removeAllFiles(files[i])
                }
            }
        }
        try {
            file.delete()
        } catch (e: Exception) {
        }
    }

    fun slowLoadImageWithFile(context: Context, image: ImageView?, imgFile: String?) {
        if (imgFile == null) {
            return
        }
        try {
            var file = File(imgFile)
            val fileUri: Uri = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Uri.fromFile(file)
            } else {
                FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )
            }
            if (image != null) {
                Glide.with(context)
                    .load(fileUri)
                    .placeholder(R.mipmap.loading_gif)
                    .thumbnail(0.5f)
                    .into(image)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
