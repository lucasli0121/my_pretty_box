package com.media.demo.obj

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.bind.DateTypeAdapter
import com.media.demo.util.AssetFile
import com.media.demo.util.JsonUtils
import java.io.Serializable
import java.sql.Date

class BoxConf : Serializable {
    @SerializedName("rtmp_local_port")
    var rtmpLocalPort: Int = 1935
    @SerializedName("rtmp_remote_url")
    var rtmpRemoteUrl: String = "rtmp://192.168.1.103:1935/live/test"
    @SerializedName("use_sdk")
    var useSdk: Int = 1
    @SerializedName("sdk_type")
    var sdkType: String = AliSdk  // "meihu_sdk"
    @SerializedName("enable_decode")
    var enableDecode: Int = 1
    @SerializedName("width")
    var width: Int = 0
    @SerializedName("height")
    var height: Int = 0
    @SerializedName("chunk_size")
    var chunkSize: Int = 128
    @SerializedName("min_face_size")
    var minFaceSize: Int = 30
    @SerializedName("max_face_size")
    var maxFaceSize: Int = 60
    @SerializedName("skin_whiting")
    var skinWhiting: Int = 8
    @SerializedName("skin_smooth")
    var skinSmooth: Int = 8
    @SerializedName("skin_sharpen")
    var skinSharpen: Int = 3
    @SerializedName("ruddy")
    var ruddy: Int = 3
    @SerializedName("no_pouch")
    var noPouch: Int = 3
    @SerializedName("no_nasolabial_folds")
    var noNasolabialFolds: Int = 3
    @SerializedName("no_wrinkles")
    var noWrinkles: Int = 3
    @SerializedName("clear")
    var clear: Int = 5
    @SerializedName("brightness")
    var brightness: Int = 6
    @SerializedName("contrast")
    var contrast: Int = 6
    @SerializedName("light_saturation")
    var lightSaturation: Int = 6
    @SerializedName("big_eye")
    var bigEye: Int =5
    @SerializedName("face_lift")
    var faceLift: Int = 6
    @SerializedName("mouth_lift")
    var mouthLift: Int =80
    @SerializedName("small_face")
    var smallFace: Int= 6
    @SerializedName("narrow_face")
    var narrowFace: Int = 5
    @SerializedName("nose_lift")
    var noseLift: Int = 5
    @SerializedName("chin_lift")
    var chinLift: Int = 5
    @SerializedName("mandible_thin")
    var mandibleThin = 6
    @SerializedName("fore_head_lift")
    var foreHeadLift: Int = 5
    @SerializedName("smile")
    var smile: Int = 5
    @SerializedName("brow_size")
    var browSize: Int = 6
    @SerializedName("eye_corner")
    var eyeCorner: Int = 5
    @SerializedName("eye_length")
    var eyeLength: Int = 6
    @SerializedName("water_pos")
    var waterPos: Int = 2000

    companion object {
        val AliSdk = "ali_sdk"
        val MeihuSdk = "meihu_sdk"
        val BytedSdk = "byted_sdk"
        fun initConfig(context: Context) : BoxConf? {
            var obj: BoxConf? = null
            try {
                var jsonStr = AssetFile.readJsonConfig(context)
                if (jsonStr.isNotEmpty()) {
                    obj = JsonUtils.parse(jsonStr, BoxConf().javaClass)
                } else {
                    jsonStr = AssetFile.getDefaultConfig(context).toString()
                    if (jsonStr.isNotEmpty()) {
                        obj = JsonUtils.parse(jsonStr, BoxConf().javaClass)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
            return obj
        }
    }
    override fun toString() : String {
        val gson = GsonBuilder()
            .registerTypeAdapter(java.util.Date::class.java, DateTypeAdapter())
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        return gson.toJson(this)
    }
}