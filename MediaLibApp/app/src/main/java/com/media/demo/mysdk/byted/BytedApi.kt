package com.media.demo.mysdk.byted

import android.content.Context
import com.effectsar.labcv.core.effect.EffectManager
import com.effectsar.labcv.core.effect.EffectResourceHelper
import com.effectsar.labcv.core.effect.EffectResourceProvider
import com.effectsar.labcv.core.license.EffectLicenseHelper
import com.effectsar.labcv.core.license.EffectLicenseProvider
import android.util.Log
import com.effectsar.labcv.common.imgsrc.ImageSourceProvider
import com.effectsar.labcv.common.model.ProcessInput
import com.effectsar.labcv.core.util.ImageUtil
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants
import com.media.demo.mysdk.MeiyanApi
import com.media.demo.obj.BoxConf

class BytedApi(context: Context): MeiyanApi(context) {
    private val Tag = "ByteApi"
    private var effectMgr : EffectManager? = null
    private val imageUtil: ImageUtil = ImageUtil()
    override fun init(conf: BoxConf?): Boolean {
        super.init(conf)
        return true
    }

    override fun unInit() {
        effectMgr?.destroy()
        effectMgr = null
    }

    private fun initEffectMgr() : Boolean {
        if(effectMgr != null) {
            effectMgr!!.destroy()
            effectMgr = null
        }
        effectMgr = EffectManager(context, EffectResourceHelper(context), EffectLicenseHelper.getInstance(context))
        effectMgr?.setOnEffectListener {
            Log.i(Tag, "EffectManager init")
        }
        val result = effectMgr?.init()
        Log.i(Tag, "effectMgr.init result=${result}")
        return result == 0
    }

    override fun reloadConfig() {
        if(effectMgr == null) {
            return
        }
        // 美白、磨皮、锐化、清晰
        val nodes = ArrayList<String>()
        nodes.add("beauty_Android_lite")
        nodes.add("reshape_lite")
        nodes.add("reshape_nature")
        nodes.add("reshape_lite_eye_size")
        nodes.add("palette/color")
        nodes.add("palette/contrast")
        nodes.add("palette/light")
        effectMgr?.setComposeNodes(nodes.toTypedArray())

        effectMgr!!.updateComposerNodeIntensity("beauty_Android_lite", "whiten", boxCfg!!.skinWhiting.toFloat() / 10)
        effectMgr!!.updateComposerNodeIntensity("beauty_Android_lite", "sharp", (boxCfg!!.skinSharpen.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("beauty_Android_lite", "smooth", (boxCfg!!.skinSmooth.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("beauty_Android_lite", "clear", (boxCfg!!.clear.toFloat() / 10))

        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_Overall", (boxCfg!!.faceLift.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_Face", (boxCfg!!.smallFace.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_CutFace", (boxCfg!!.narrowFace.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_Zoom_Jawbone", (boxCfg!!.mandibleThin.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_Chin", (boxCfg!!.chinLift.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_Eye", (boxCfg!!.bigEye.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Eye_Spacing", (boxCfg!!.eyeLength.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_RotateEye", (boxCfg!!.eyeCorner.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_NoseSize", (boxCfg!!.noseLift.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_BrowSize", (boxCfg!!.browSize.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_ZoomMouth", (boxCfg!!.mouthLift.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("reshape_lite", "Internal_Deform_MouthCorner", (boxCfg!!.smile.toFloat() / 10))

        effectMgr!!.updateComposerNodeIntensity("palette/light", "Intensity_Light", (boxCfg!!.brightness.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("palette/contrast", "Intensity_Contrast", (boxCfg!!.contrast.toFloat() / 10))
        effectMgr!!.updateComposerNodeIntensity("palette/color", "Intensity_Saturation", (boxCfg!!.lightSaturation.toFloat() / 10))

    }

    override fun renderTextureId(textureId: Int, len: Int, w: Int, h: Int): Int {
        if(effectMgr == null) {
            if(!initEffectMgr() ) {
                Log.e(Tag, "init effectmgr failed")
            }
            reloadConfig()
        }
        if(effectMgr == null) {
            return 0
        }
        val destId = imageUtil.prepareTexture(w, h)
        val timeStemp = System.nanoTime()
        val result = effectMgr!!.process(textureId, destId, w, h, EffectsSDKEffectConstants.Rotation.CLOCKWISE_ROTATE_0, timeStemp)
        if(!result) {
            Log.w(Tag, "effectMgr.process return false")
        }
        return destId
    }
}