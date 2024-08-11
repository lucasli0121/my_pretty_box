package com.effectsar.labcv.platform.utils

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef
import com.effectsar.labcv.platform.utils.ConvertUtils.MemoryConstants.Unit
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.nio.charset.Charset

object ConvertUtils {
    object MemoryConstants {
        const val BYTE = 1
        const val KB = 1024
        const val MB = 1048576
        const val GB = 1073741824

        @IntDef(BYTE, KB, MB, GB)
        @Retention(RetentionPolicy.SOURCE)
        annotation class Unit
    }

    private const val BUFFER_SIZE = 8192
    private val HEX_DIGITS_UPPER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_DIGITS_LOWER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    /**
     * Int to hex string.
     *
     * @param num The int number.
     * @return the hex string
     */

    fun int2HexString(num: Int): String {
        return Integer.toHexString(num)
    }

    /**
     * Hex string to int.
     *
     * @param hexString The hex string.
     * @return the int
     */

    fun hexString2Int(hexString: String): Int {
        return hexString.toInt(16)
    }

    /**
     * Bytes to chars.
     *
     * @param bytes The bytes.
     * @return chars
     */

    fun bytes2Chars(bytes: ByteArray?): CharArray? {
        if (bytes == null) return null
        val len = bytes.size
        if (len <= 0) return null
        val chars = CharArray(len)
        for (i in 0 until len) {
            chars[i] = (bytes[i].toInt().and(0xff)) as Char
        }
        return chars
    }

    /**
     * Chars to bytes.
     *
     * @param chars The chars.
     * @return bytes
     */

    fun chars2Bytes(chars: CharArray?): ByteArray? {
        if (chars == null || chars.size <= 0) return null
        val len = chars.size
        val bytes = ByteArray(len)
        for (i in 0 until len) {
            bytes[i] = chars[i].toByte()
        }
        return bytes
    }
    /**
     * Bytes to hex string.
     *
     * e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }, true) returns "00A8"
     *
     * @param bytes       The bytes.
     * @param isUpperCase True to use upper case, false otherwise.
     * @return hex string
     */
    /**
     * Bytes to hex string.
     *
     * e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns "00A8"
     *
     * @param bytes The bytes.
     * @return hex string
     */

    @JvmOverloads
    fun bytes2HexString(bytes: ByteArray?, isUpperCase: Boolean = true): String {
        if (bytes == null) return ""
        val hexDigits = if (isUpperCase) HEX_DIGITS_UPPER else HEX_DIGITS_LOWER
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[bytes[i].toInt().shr(4).and(0x0f)]
            ret[j++] = hexDigits[bytes[i].toInt().and(0x0f)]
            i++
        }
        return String(ret)
    }

    /**
     * Hex string to bytes.
     *
     * e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString The hex string.
     * @return the bytes
     */

    fun hexString2Bytes(hexString: String): ByteArray {
        var hexString = hexString
        var len = hexString.length
        if (len % 2 != 0) {
            hexString = "0$hexString"
            len = len + 1
        }
        val hexBytes = hexString.toUpperCase().toCharArray()
        val ret = ByteArray(len shr 1)
        var i = 0
        while (i < len) {
            ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
            i += 2
        }
        return ret
    }

    private fun hex2Dec(hexChar: Char): Int {
        return if (hexChar >= '0' && hexChar <= '9') {
            hexChar - '0'
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            hexChar - 'A' + 10
        } else {
            throw IllegalArgumentException()
        }
    }
    /**
     * Bytes to string.
     */
    /**
     * Bytes to string.
     */

    @JvmOverloads
    fun bytes2String(bytes: ByteArray?, charsetName: String = ""): String? {
        return if (bytes == null) null else try {
            String(bytes, charset(getSafeCharset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            String(bytes)
        }
    }
    /**
     * String to bytes.
     */
    /**
     * String to bytes.
     */

    @JvmOverloads
    fun string2Bytes(string: String?, charsetName: String = ""): ByteArray? {
        return if (string == null) null else try {
            string.toByteArray(charset(getSafeCharset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            string.toByteArray()
        }
    }

    /**
     * Bytes to JSONObject.
     */

    fun bytes2JSONObject(bytes: ByteArray?): JSONObject? {
        return if (bytes == null) null else try {
            JSONObject(String(bytes))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * JSONObject to bytes.
     */

    fun jsonObject2Bytes(jsonObject: JSONObject?): ByteArray? {
        return jsonObject?.toString()?.toByteArray()
    }

    /**
     * Bytes to JSONArray.
     */

    fun bytes2JSONArray(bytes: ByteArray?): JSONArray? {
        return if (bytes == null) null else try {
            JSONArray(String(bytes))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * JSONArray to bytes.
     */

    fun jsonArray2Bytes(jsonArray: JSONArray?): ByteArray? {
        return jsonArray?.toString()?.toByteArray()
    }

    /**
     * Bytes to Parcelable
     */

    fun <T> bytes2Parcelable(bytes: ByteArray?,
                             creator: Parcelable.Creator<T>): T? {
        if (bytes == null) return null
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        val result = creator.createFromParcel(parcel)
        parcel.recycle()
        return result
    }

    /**
     * Parcelable to bytes.
     */

    fun parcelable2Bytes(parcelable: Parcelable?): ByteArray? {
        if (parcelable == null) return null
        val parcel = Parcel.obtain()
        parcelable.writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        parcel.recycle()
        return bytes
    }

    /**
     * Bytes to Serializable.
     */

    fun bytes2Object(bytes: ByteArray?): Any? {
        if (bytes == null) return null
        var ois: ObjectInputStream? = null
        return try {
            ois = ObjectInputStream(ByteArrayInputStream(bytes))
            ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                ois?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Serializable to bytes.
     */

    fun serializable2Bytes(serializable: Serializable?): ByteArray? {
        if (serializable == null) return null
        var baos: ByteArrayOutputStream
        var oos: ObjectOutputStream? = null
        return try {
            oos = ObjectOutputStream(ByteArrayOutputStream().also { baos = it })
            oos.writeObject(serializable)
            baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Size of memory in unit to size of byte.
     *
     * @param memorySize Size of memory.
     * @param unit       The unit of memory size.
     *
     *  * [MemoryConstants.BYTE]
     *  * [MemoryConstants.KB]
     *  * [MemoryConstants.MB]
     *  * [MemoryConstants.GB]
     *
     * @return size of byte
     */

    fun memorySize2Byte(memorySize: Long,
                        @Unit unit: Int): Long {
        return if (memorySize < 0) -1 else memorySize * unit
    }

    /**
     * Size of byte to size of memory in unit.
     *
     * @param byteSize Size of byte.
     * @param unit     The unit of memory size.
     *
     *  * [MemoryConstants.BYTE]
     *  * [MemoryConstants.KB]
     *  * [MemoryConstants.MB]
     *  * [MemoryConstants.GB]
     *
     * @return size of memory in unit
     */

    fun byte2MemorySize(byteSize: Long,
                        @Unit unit: Int): Double {
        return if (byteSize < 0) (-1).toDouble() else byteSize.toDouble() / unit
    }

    /**
     * Size of byte to fit size of memory.
     *
     * to three decimal places
     *
     * @param byteSize Size of byte.
     * @return fit size of memory
     */

    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteSize: Long): String {
        return byte2FitMemorySize(byteSize, 3)
    }

    /**
     * Size of byte to fit size of memory.
     *
     * to three decimal places
     *
     * @param byteSize  Size of byte.
     * @param precision The precision
     * @return fit size of memory
     */

    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteSize: Long, precision: Int): String {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return when {
            byteSize < 0 -> {
                throw IllegalArgumentException("byteSize shouldn't be less than zero!")
            }
            byteSize < MemoryConstants.KB -> {
                String.format("%." + precision + "fB", byteSize.toDouble())
            }
            byteSize < MemoryConstants.MB -> {
                String.format("%." + precision + "fKB", byteSize.toDouble() / MemoryConstants.KB)
            }
            byteSize < MemoryConstants.GB -> {
                String.format("%." + precision + "fMB", byteSize.toDouble() / MemoryConstants.MB)
            }
            else -> {
                String.format("%." + precision + "fGB", byteSize.toDouble() / MemoryConstants.GB)
            }
        }
    }

    private fun getSafeCharset(charsetName: String): String {
        var cn = charsetName
        if (!Charset.isSupported(charsetName)) {
            cn = "UTF-8"
        }
        return cn
    }

    fun jsonArray2List(jsonArray: JSONArray?): MutableList<String> {
        val result: ArrayList<String> = arrayListOf()
        try {
            val size = jsonArray?.length()
            for (idx in 0..size!!) {
                result.add(jsonArray.getString(idx))
            }
        } catch (ex: Exception) {
            // ignore
        }
        return result
    }

    fun list2JsonArray(list: ArrayList<Any>): JSONArray {
        return JSONArray(list)
    }
}