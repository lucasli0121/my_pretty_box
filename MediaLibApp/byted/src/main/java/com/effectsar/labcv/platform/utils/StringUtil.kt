package com.effectsar.labcv.platform.utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object StringUtil {

    fun isEmpty(str: CharSequence): Boolean {
        return isNull(str) || str.isEmpty()
    }

    fun isNull(o: Any?): Boolean {
        return o == null
    }

    /**
     * Return whether string1 is equals to string2.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return `true`: yes<br></br>`false`: no
     */
    fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
        if (s1 === s2) return true
        var length = 0
        return if (s1 != null && s2 != null && s1.length.also { length = it } == s2.length) {
            if (s1 is String && s2 is String) {
                s1 == s2
            } else {
                for (i in 0 until length) {
                    if (s1[i] != s2[i]) return false
                }
                true
            }
        } else false
    }

    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return `true`: yes<br></br> `false`: no
     */
    fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }


    fun getSuffix(path: String?): String {
        if (path == null) {
            return ""
        }
        var fe = ""
        val i = path.lastIndexOf('.')
        if (i > 0) {
            fe = path.substring(i + 1)
        }
        return fe
    }

    fun getFileNameFromZip(fileName: String): String {
        return if (fileName.endsWith(".zip")) {
            fileName.replace(".zip", "")
        } else {
            fileName
        }
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}