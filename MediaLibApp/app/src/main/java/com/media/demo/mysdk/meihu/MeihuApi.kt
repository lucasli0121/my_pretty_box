package com.media.demo.mysdk.meihu

import android.content.Context
import android.util.Log
import com.media.demo.R
import com.media.demo.mysdk.MeiyanApi
import com.media.demo.obj.BoxConf

class MeihuApi(context: Context): MeiyanApi(context) {
//    private var mhManager: MHBeautyManager? = null
//
//    private var waterList = ArrayList<TeXiaoWaterBean>()
//    private var beans = ArrayList<MHCommonBean>()

    override fun init(conf: BoxConf?) : Boolean {
        super.init(conf)
        return mhManagerInit()
    }

    override fun reinit(): Boolean {
//        MhDataManager.getInstance().release()
        return mhManagerInit()
    }


    private fun mhManagerInit() : Boolean {
        return boxCfg != null
//        MhDataManager.getInstance().create(context.applicationContext)
//        mhManager = MhDataManager.getInstance().mhBeautyManager
////        mhManager = MHBeautyManager(this)
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_no,
//                R.mipmap.beauty_btn_drawing_default,
//                R.mipmap.beauty_btn_originaldrawing,
//                MHConfigConstants.MEI_YAN_MEI_XING_YUAN_TU
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_dayan,
//                R.mipmap.beauty_btn_eye_default,
//                R.mipmap.beauty_btn_eye_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_DA_YAN
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_shoulian,
//                R.mipmap.beauty_btn_face_default,
//                R.mipmap.beauty_btn_face_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_SHOU_LIAN
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_zuixing,
//                R.mipmap.beauty_btn_mouth_default,
//                R.mipmap.beauty_btn_mouth_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_ZUI_XING
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_shoubi,
//                R.mipmap.beauty_btn_thinnose_default,
//                R.mipmap.beauty_btn_thinnose_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_SHOU_BI
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_xiaba,
//                R.mipmap.beauty_btn_chin_default,
//                R.mipmap.beauty_btn_chin_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_XIA_BA
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_etou,
//                R.mipmap.beauty_btn_forehead_default,
//                R.mipmap.beauty_btn_forehead_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_E_TOU
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_meimao,
//                R.mipmap.beauty_btn_eyebrow_default,
//                R.mipmap.beauty_btn_eyebrow_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_MEI_MAO
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_yanjiao,
//                R.mipmap.beauty_btn_canth_default,
//                R.mipmap.beauty_btn_canth_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_YAN_JIAO
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_yanju,
//                R.mipmap.beauty_btn_eyespan_default,
//                R.mipmap.beauty_btn_eyespan_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_YAN_JU
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_kaiyanjiao,
//                R.mipmap.beauty_btn_openeye_default,
//                R.mipmap.beauty_btn_openeye_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_KAI_YAN_JIAO
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_xuelian,
//                R.mipmap.beauty_btn_cutface_default,
//                R.mipmap.beauty_btn_cutface_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_XUE_LIAN
//            )
//        )
//        beans.add(
//            MeiYanBean(
//                R.string.beauty_mh_changbi,
//                R.mipmap.beauty_btn_longnose_default,
//                R.mipmap.beauty_btn_longnose_sele,
//                MHConfigConstants.MEI_YAN_MEI_XING_CHANG_BI
//            )
//        )
//        beans = MHSDK.getFunctionItems(
//            beans,
//            MHConfigConstants.MEI_YAN,
//            MHConfigConstants.MEI_YAN_MEI_XING_FUNCION
//        ) as ArrayList<MHCommonBean>


//        mhManager?.setMinFaceSize(boxCfg!!.minFaceSize)
//        mhManager?.setMaxFace(boxCfg!!.maxFaceSize)
//        mhManager?.setSkinWhiting(boxCfg!!.skinWhiting)
//        mhManager?.setSkinSmooth(boxCfg!!.skinSmooth)
//        mhManager?.setBrightness(boxCfg!!.brightness)
//        mhManager?.setBigEye(boxCfg!!.bigEye)
//        mhManager?.setFaceLift(boxCfg!!.faceLift)
//        mhManager?.setMouseLift(boxCfg!!.mouthLift)
//        mhManager?.setNoseLift(boxCfg!!.noseLift)
//        mhManager?.setChinLift(boxCfg!!.chinLift)
//        mhManager?.setForeheadLift(boxCfg!!.foreHeadLift)
//        mhManager?.setEyeBrow(boxCfg!!.eyeBrow)
//        mhManager?.setEyeCorner(boxCfg!!.eyeCorner)
//        mhManager?.setEyeLength(boxCfg!!.eyeLength)
//        mhManager?.setEyeAlat(50)
//        mhManager?.setFaceShave(50)
//        mhManager?.setLengthenNoseLift(50)
//
//        val userfaces = mhManager?.useFaces
//        if (userfaces != null && userfaces.isNotEmpty()) {
//            userfaces[0] = 1
//            if(userfaces.size > 1) {
//                userfaces[1] = 1
//            }
//            mhManager?.setUseFace(true)
//            mhManager?.useFaces = userfaces
//        }
//        waterList.add(TeXiaoWaterBean(com.meihu.beauty.R.mipmap.ic_mh_none, 0, MHSDK.WATER_NONE, true))
//        waterList.add(
//            TeXiaoWaterBean(
//                com.meihu.beauty.R.mipmap.ic_water_thumb_0,
//                com.meihu.beauty.R.mipmap.ic_water_res_0,
//                MHSDK.WATER_TOP_LEFT
//            )
//        )
//        waterList.add(
//            TeXiaoWaterBean(
//                com.meihu.beauty.R.mipmap.ic_water_thumb_1,
//                com.meihu.beauty.R.mipmap.ic_water_res_1,
//                MHSDK.WATER_TOP_RIGHT
//            )
//        )
//        waterList.add(
//            TeXiaoWaterBean(
//                com.meihu.beauty.R.mipmap.ic_water_thumb_2,
//                com.meihu.beauty.R.mipmap.ic_water_res_2,
//                MHSDK.WATER_BOTTOM_LEFT
//            )
//        )
//        waterList.add(
//            TeXiaoWaterBean(
//                com.meihu.beauty.R.mipmap.ic_water_thumb_3,
//                com.meihu.beauty.R.mipmap.ic_water_res_3,
//                MHSDK.WATER_BOTTOM_RIGHT
//            )
//        )
//        when(boxCfg!!.waterPos) {
//            MHSDK.WATER_TOP_LEFT -> {
//                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_0, MHSDK.WATER_TOP_LEFT)
//            }
//            MHSDK.WATER_TOP_RIGHT -> {
//                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_1, MHSDK.WATER_TOP_RIGHT)
//            }
//            MHSDK.WATER_BOTTOM_LEFT -> {
//                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_2, MHSDK.WATER_BOTTOM_LEFT)
//            }
//            MHSDK.WATER_BOTTOM_RIGHT -> {
//                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_3, MHSDK.WATER_BOTTOM_RIGHT)
//            }
//            else -> {
//
//            }
//        }
    }

    override fun renderTextureId(textureId: Int, len: Int, w: Int, h: Int): Int {
//        val newId = MhDataManager.getInstance().render(textureId, w, h)
//        return newId
        return 0
    }
}