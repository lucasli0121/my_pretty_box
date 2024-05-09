package com.media.demo.obj

import android.content.Context
import com.media.demo.util.AssetFile
import org.json.JSONObject
import java.io.Serializable
import kotlin.math.max

class BoxConf : Serializable {
    var rtmpLocalPort: Int = 1935
    var rtmpRemoteUrl: String = "rtmp://192.168.1.103:1935/live/test"
    var useSdk: Int = 1
    var enableDecode: Int = 1
    var width: Int = 0
    var height: Int = 0
    var chunkSize: Int = 128
    var minFaceSize: Int = 30
    var maxFaceSize: Int = 60
    var skinWhiting: Int = 8
    var skinSmooth: Int = 8
    var brightness: Int = 50
    var bigEye: Int =80
    var faceLift: Int = 80
    var mouseLift: Int =80
    var noseLift: Int = 80
    var chinLift: Int = 80
    var foreHeadLift: Int = 80
    var eyeBrow: Int = 80
    var eyeCorner: Int = 80
    var eyeLength: Int = 80
    var waterPos: Int = 2000

    fun initConfig(context: Context): Boolean {
        var result: Boolean = false
        try {
            val jsonStr = AssetFile.readJsonConfig(context)
            if (jsonStr.isNotEmpty()) {
                val jsObj = JSONObject(jsonStr)
                rtmpLocalPort = jsObj.getInt("rtmp_local_port")
                rtmpRemoteUrl = jsObj.getString("rtmp_remote_url")
                useSdk = jsObj.getInt("use_sdk")
                enableDecode = jsObj.getInt("enable_decode")
                chunkSize = jsObj.getInt("chunk_size")
                width = jsObj.getInt("width")
                height = jsObj.getInt("height")
                minFaceSize = jsObj.getInt("min_face_size")
                maxFaceSize = jsObj.getInt("max_face_size")
                skinWhiting = jsObj.getInt("skin_whiting")
                skinSmooth = jsObj.getInt("skin_smooth")
                brightness = jsObj.getInt("brightness")
                bigEye = jsObj.getInt("big_eye")
                faceLift = jsObj.getInt("face_lift")
                mouseLift = jsObj.getInt("mouse_lift")
                noseLift = jsObj.getInt("nose_lift")
                chinLift = jsObj.getInt("chin_lift")
                foreHeadLift = jsObj.getInt("fore_head_lift")
                eyeBrow = jsObj.getInt("eye_brow")
                eyeCorner = jsObj.getInt("eye_corner")
                eyeLength = jsObj.getInt("eye_length")
                waterPos = jsObj.getInt("water_pos")
                result = true
            }
        }catch (e: Exception) {
            e.printStackTrace()
            result = false
        }
        return result

    }
}