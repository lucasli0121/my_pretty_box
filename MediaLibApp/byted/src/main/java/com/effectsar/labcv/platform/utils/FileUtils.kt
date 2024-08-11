package com.effectsar.labcv.platform.utils

import android.content.res.AssetManager
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object FileUtils {

    fun getFileMD5ToString(file: File?): String {
        return ConvertUtils.bytes2HexString(getFileMD5(file)).toUpperCase(Locale.getDefault())
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */

    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                dis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */

    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }


    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */

    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */

    fun getFileByPath(filePath: String?): File {
        return File(filePath)
    }

    /** {zh} 
     * 递归拷贝Asset目录中的文件到rootDir中
     * Recursively copy the files in the Asset directory to rootDir
     * @param assets
     * @param path  assets 下的path  eg: resource/duet.bundle/duet.json
     * @param dstRootDir
     * @throws IOException
     */
    /** {en} 
     * Recursively copy the files in the Asset directory to rootDir
     * Recursively copy the files in the Asset directory to rootDir
     * @param assets
     * @param path  assets path eg: resource/duet.bundle/duet.json
     * @param dstRootDir
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyAssets(assets: AssetManager, path: String, dstRootDir: String) {
        if (isAssetsDir(assets, path)) {
            val dir = File(dstRootDir + File.separator + path)
            check(!(!dir.exists() && !dir.mkdirs())) { "mkdir failed" }
            for (s in assets.list(path)!!) {
                copyAssets(assets, "$path/$s", dstRootDir)
            }
        } else {
            val input = assets.open(path)
            val dest = File(dstRootDir, path)
            copyToFileOrThrow(input, dest)
        }
    }

    private fun isAssetsDir(assets: AssetManager, path: String): Boolean {
        try {
            val files = assets.list(path)
            return files != null && files.isNotEmpty()
        } catch (e: IOException) {
            Log.e("FileUtils", "isAssetsDir:", e)
        }
        return false
    }

    @Throws(IOException::class)
    private fun copyToFileOrThrow(inputStream: InputStream, destFile: File) {
        if (destFile.exists()) {
            return
        }
        val file = destFile.parentFile
        if (file != null && !file.exists()) {
            file.mkdirs()
        }
        inputStream.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}