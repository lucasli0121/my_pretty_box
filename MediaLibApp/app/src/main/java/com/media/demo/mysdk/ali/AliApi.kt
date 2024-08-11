package com.media.demo.mysdk.ali

import android.content.Context
import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.GLES30
import android.os.Build
import android.util.Log
import com.aliyun.android.libqueen.QueenConfig
import com.aliyun.android.libqueen.QueenEngine
import com.aliyun.android.libqueen.Texture2D
import com.aliyun.android.libqueen.models.BeautyFilterMode
import com.aliyun.android.libqueen.models.BeautyFilterType
import com.aliyun.android.libqueen.models.BeautyParams
import com.aliyun.android.libqueen.models.FaceShapeType
import com.aliyunsdk.queen.param.QueenParamHolder
import com.media.demo.mysdk.MeiyanApi
import com.media.demo.obj.BoxConf
import com.media.demo.util.AssetFile
import com.media.demo.util.GLTextureHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class AliApi(context: Context): MeiyanApi(context) {
    private var engine: QueenEngine? = null
    private var queenConfig: QueenConfig = QueenConfig()
//    private var outTexture2D: Texture2D? = null

    override fun init(conf: BoxConf?): Boolean {
        super.init(conf)
        try {
            queenConfig.toScreen = false
            queenConfig.enableDebugLog = false
            queenConfig.withContext = false
            queenConfig.withNewGlThread = false

            if (queenConfig.withContext || queenConfig.withNewGlThread) {
                // 如果需要Queen在单独的线程创建gl上下文，且需要共享当前线程的gl上下文，那么配置当前gl上下文
                if (Build.VERSION.SDK_INT >= 21) {
                    queenConfig.shareGlContext = EGL14.eglGetCurrentContext().getNativeHandle();
                } else {
                    queenConfig.shareGlContext = EGL14.eglGetCurrentContext().getHandle().toLong();
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun unInit() {
        Log.d("AliApi", "ali-sdk unInit, current threadId=${Thread.currentThread()}")
        super.unInit()
//        if(outTexture2D != null) {
//            outTexture2D!!.release()
//            outTexture2D = null
//        }
        if (engine != null) {
            engine!!.release()
            engine = null
        }
        QueenParamHolder.relaseQueenParams()
    }

    override fun reinit() :Boolean{
        Log.d("AliApi", "ali-sdk reinit, current threadId=${Thread.currentThread()}")
//        if (engine != null) {
//            engine!!.release()
//            engine = null
//        }
        return true
    }

    override fun reloadConfig() {
        if (engine == null) {
            return
        }
//        engine!!.setPowerSaving(true);     // 设置为powersaving省电模式

        // 美白
        engine!!.enableBeautyType(BeautyFilterType.kSkinWhiting, true, BeautyFilterMode.kBMSkinBuffing_Natural)
        engine!!.setBeautyParam(BeautyParams.kBPSkinWhitening, boxCfg!!.skinWhiting.toFloat() / 10);
        /**
         * 磨皮和锐化开关，
         * 第三个参数为基础美颜的模式，设置为kBMSkinBuffing_Natural，则美颜的效果更自然，细节保留更多；设置为kBMSkinBuffing_Strong，则效果更夸张，细节去除更多。
         */
        engine!!.enableBeautyType(BeautyFilterType.kSkinBuffing, true, BeautyFilterMode.kBMSkinBuffing_Natural)
        //   磨皮 [0,1]
        engine!!.setBeautyParam(BeautyParams.kBPSkinBuffing, boxCfg!!.skinSmooth.toFloat() / 10)
        // 锐化 [0,1]
        engine!!.setBeautyParam(BeautyParams.kBPSkinSharpen, boxCfg!!.skinSharpen.toFloat() / 10)
        // 高级美颜开关
        engine!!.enableBeautyType(BeautyFilterType.kFaceBuffing, true)
        // 去除法令纹[0,1]
        engine!!.setBeautyParam(BeautyParams.kBPNasolabialFolds, boxCfg!!.noNasolabialFolds.toFloat() / 10)
        //祛眼袋[0,1]
        engine!!.setBeautyParam(BeautyParams.kBPPouch, boxCfg!!.noPouch.toFloat() / 10)
        // 祛皱[0,1]
        engine!!.setBeautyParam(BeautyParams.kBPWrinkles, boxCfg!!.noWrinkles.toFloat() / 10)
        // 滤镜美妆：红润[0,1]
        engine!!.setBeautyParam(BeautyParams.kBPBlush, boxCfg!!.ruddy.toFloat() / 10)

//        // 画质
//        engine!!.enableBeautyType(BeautyFilterType.kHSV,true, BeautyFilterMode.kBMSkinBuffing_Strong)
//        //饱和度[-1,1]
//        engine!!.setBeautyParam(BeautyParams.kBPHSV_SATURATION,boxCfg!!.lightSaturation.toFloat() / 10)
//        //对比度[-1,1]
//        engine!!.setBeautyParam(BeautyParams.kBPHSV_CONTRAST,boxCfg!!.contrast.toFloat() / 10)

        /**
         * 美型开关，其中第二个参数是功能开关，第三个参数为调试开关
         * 第四个参数为美型的模式，可以设置为kBMFaceShape_Baseline、kBMFaceShape_Main、kBMFaceShape_High、kBMFaceShape_Max四种模式，形变的幅度会依次变强
         */
        engine!!.enableBeautyType(BeautyFilterType.kFaceShape, true, false, BeautyFilterMode.kBMSkinBuffing_Natural)
        /**
         * 美型参数：削脸
         * 参数范围：[0,1]
         */
        engine!!.updateFaceShape(FaceShapeType.typeCutFace, boxCfg!!.smallFace.toFloat() / 10);
        /**
         * 美型参数：瘦脸
         * 参数范围：[0,1]
         */
        engine!!.updateFaceShape(FaceShapeType.typeThinFace, boxCfg!!.faceLift.toFloat() / 10)
//        /**
//         * 美型参数：脸长
//         * 参数范围：[0,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeLongFace, boxCfg!!.narrowFace.toFloat() / 10)
//        /**
//         * 美型参数：瘦下巴
//         * 参数范围：[0,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeThinJaw, boxCfg!!.chinLift.toFloat() /  10)
//        /**
//         * 美型参数：瘦下颌
//         * 参数范围：[0,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeThinMandible, boxCfg!!.mandibleThin.toFloat() / 10)
        /**
         * 美型参数：大眼
         * 参数范围：[0,1]
         */
        engine!!.updateFaceShape(FaceShapeType.typeBigEye, boxCfg!!.bigEye.toFloat() / 10)
//        /**
//         * 美型参数：眼角1
//         * 参数范围：[0,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeEyeAngle1, boxCfg!!.eyeCorner.toFloat() / 10)
//        /**
//         * 美型参数：眼距
//         * 参数范围：[-1,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeCanthus, boxCfg!!.eyeLength.toFloat() / 10)
        /**
         * 美型参数：瘦鼻
         * 参数范围：[0,1]
         */
        engine!!.updateFaceShape(FaceShapeType.typeThinNose, boxCfg!!.noseLift.toFloat() / 10)
//        /**
//         * 美型参数：嘴唇大小
//         * 参数范围：[-1,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeMouthSize, boxCfg!!.mouthLift.toFloat() / 10)
//        /**
//         * 美型参数：嘴角上扬（微笑）
//         * 参数范围：[-1,1]
//         */
//        engine!!.updateFaceShape(FaceShapeType.typeSmile, boxCfg!!.smile.toFloat() / 10)
    }
    /*
     根据textureId处理数据
     */
    override fun renderTextureId(textureId: Int, len: Int, w: Int, h: Int): Int {
        if (engine == null) {
            engine = QueenEngine(context, queenConfig)
            // 开启log日志打印调试模式，建议只在Debug包打开日志调试，避免影响性能
//            engine!!.enableDebugLog()
            Log.d("AliApi", "ali-sdk renderTextureId, current threadId=${Thread.currentThread()}")
            reloadConfig()
        }
//        return textureId
        return processInputTextureId(textureId, len,false, w, h)
    }
    /*
     根据buffer处理数据
     */
    override fun renderBuffer(data: ByteArray, len: Int, w: Int, h: Int): Int {
        if (engine == null) {
            engine = QueenEngine(context, queenConfig)
            // 开启log日志打印调试模式，建议只在Debug包打开日志调试，避免影响性能
//            engine!!.enableDebugLog()
            reloadConfig()
        }
        val textureId = GLTextureHelper.loadRgbaBuf2Texture(data, w, h, -1)
        return processInputTextureId(textureId, len,false, w, h)
    }

    private fun processInputTextureId(textureId: Int, len: Int, isOes: Boolean, width: Int, height: Int) : Int {
        if (engine == null) {
            return -1
        }
        val oldFboId: IntArray = IntArray(1)
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, IntBuffer.wrap(oldFboId))
        val oldViewPort = IntArray(4)
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, oldViewPort, 0)
        val w = if (isOes) height else width
        val h = if (isOes) width else height

        engine!!.setInputTexture(textureId, w, h, isOes)
        val outTexture2D = engine!!.autoGenOutTexture(false)
        engine!!.updateInputTextureBufferAndRunAlg(0, 0, 0, false)
        val result = engine!!.render()
        val newId = if (result != 0) {
            textureId
        } else {
            outTexture2D!!.textureId
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, oldFboId[0])
        GLES20.glViewport(oldViewPort[0], oldViewPort[1], oldViewPort[2], oldViewPort[3])
        return newId
    }
}