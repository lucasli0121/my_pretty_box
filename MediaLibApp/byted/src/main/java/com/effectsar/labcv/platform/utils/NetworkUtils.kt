package com.effectsar.labcv.platform.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

object NetworkUtils {

    @SuppressLint("MissingPermission")
    fun getNetworkType(context: Context): NetworkType? {
        return try {
            val manager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val info = manager.activeNetworkInfo
            if (info == null || !info.isAvailable) {
                return NetworkType.NONE
            }
            val type = info.type
            if (ConnectivityManager.TYPE_WIFI == type) {
                NetworkType.WIFI
            } else if (ConnectivityManager.TYPE_MOBILE == type) {
                val mgr = context.getSystemService(
                    Context.TELEPHONY_SERVICE
                ) as TelephonyManager
                when (mgr.networkType) {
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.MOBILE_3G
                    TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.MOBILE_4G
                    TelephonyManager.NETWORK_TYPE_NR -> NetworkType.MOBILE_5G
                    else -> NetworkType.MOBILE
                }
            } else {
                NetworkType.MOBILE
            }
        } catch (e: Throwable) {
            NetworkType.MOBILE
        }
    }
}

enum class NetworkType(val value: Int) {
    //  {zh} 初始状态  {en} Initial state
    UNKNOWN(-1), NONE(0), MOBILE(1), MOBILE_2G(2), MOBILE_3G(3), WIFI(4), MOBILE_4G(5), MOBILE_5G(6), WIFI_24GHZ(
        7
    ),
    WIFI_5GHZ(8), MOBILE_3G_H(9), MOBILE_3G_HP(10);

    fun is2G(): Boolean {
        return this == MOBILE || this == MOBILE_2G
    }

    val isWifi: Boolean
        get() = this == WIFI

    /** {zh} 
     * 判断是否是4G或者高于4G
     * @return
     */
    /** {en} 
     * Determine whether it is 4G or higher than 4G
     * @return
     */
    fun is4GOrHigher(): Boolean {
        return this == MOBILE_4G || this == MOBILE_5G
    }

    /** {zh} 
     * 判断网络类型是否高于3G
     * @return
     */
    /** {en} 
     * Determine whether the network type is higher than 3G
     * @return
     */
    fun is3GOrHigher(): Boolean {
        return this == MOBILE_3G || this == MOBILE_3G_H || this == MOBILE_3G_HP || this == MOBILE_4G || this == MOBILE_5G
    }

    val isAvailable: Boolean
        get() = this != UNKNOWN && this != NONE

}