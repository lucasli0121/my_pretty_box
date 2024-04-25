package com.media.demo

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.media.demo.obj.BoxConf
import com.media.demo.util.AssetFile
import com.media.demo.util.PermissionsUtils
import com.medialib.jni.MediaJni
import com.meihu.beauty.bean.MeiYanBean
import com.meihu.beauty.bean.MeiYanTypeBean
import com.meihu.beauty.bean.TeXiaoWaterBean
import com.meihu.beauty.utils.MhDataManager
import com.meihu.beautylibrary.MHSDK
import com.meihu.beautylibrary.bean.MHCommonBean
import com.meihu.beautylibrary.bean.MHConfigConstants
import com.meihu.beautylibrary.manager.MHBeautyManager
import com.updatelibrary.UpdateMgr

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val mediaJni = MediaJni()
    private var mhManager: MHBeautyManager? = null
    private var boxCfg = BoxConf()
    private var waterList = ArrayList<TeXiaoWaterBean>()
    private var beans = ArrayList<MHCommonBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.setCurrentActivity(this)

        var upgrade = UpdateMgr(this)
        upgrade.checkUpdate(true)
        //权限申请使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            PermissionsUtils.checkAndRequestMorePermissions(
                this, PERMISSIONS, REQUEST_CODE_PERMISSIONS
            ) {
                initView()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionsUtils.isPermissionRequestSuccess(grantResults)) {
            initView()
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_main)
        if(!boxCfg.initConfig(this)) {
            Log.e("MainActivity", "init json config failed")
            finish()
        }
        mediaJni.setParams(boxCfg.useSdk, boxCfg.enableDecode, boxCfg.rtmpLocalPort, boxCfg.rtmpRemoteUrl, boxCfg.width, boxCfg.height)
        mhManagerInit()
        var glSurface = findViewById<SurfaceView>(R.id.gl_surface)
        glSurface.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                startMediaServer()
                // Example of a call to a native method.
//                mediaJni.testMediaCodec(AssetFile.getInputVideoFile(this@MainActivity), AssetFile.getOutputVideoFile(this@MainActivity))
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopMediaServer()
            }
        })


    }
    private fun mhManagerInit() {
        MhDataManager.getInstance().create(applicationContext)
        mhManager = MhDataManager.getInstance().mhBeautyManager
//        mhManager = MHBeautyManager(this)
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_no,
                R.mipmap.beauty_btn_drawing_default,
                R.mipmap.beauty_btn_originaldrawing,
                MHConfigConstants.MEI_YAN_MEI_XING_YUAN_TU
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_dayan,
                R.mipmap.beauty_btn_eye_default,
                R.mipmap.beauty_btn_eye_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_DA_YAN
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_shoulian,
                R.mipmap.beauty_btn_face_default,
                R.mipmap.beauty_btn_face_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_SHOU_LIAN
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_zuixing,
                R.mipmap.beauty_btn_mouth_default,
                R.mipmap.beauty_btn_mouth_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_ZUI_XING
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_shoubi,
                R.mipmap.beauty_btn_thinnose_default,
                R.mipmap.beauty_btn_thinnose_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_SHOU_BI
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_xiaba,
                R.mipmap.beauty_btn_chin_default,
                R.mipmap.beauty_btn_chin_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_XIA_BA
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_etou,
                R.mipmap.beauty_btn_forehead_default,
                R.mipmap.beauty_btn_forehead_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_E_TOU
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_meimao,
                R.mipmap.beauty_btn_eyebrow_default,
                R.mipmap.beauty_btn_eyebrow_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_MEI_MAO
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_yanjiao,
                R.mipmap.beauty_btn_canth_default,
                R.mipmap.beauty_btn_canth_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_YAN_JIAO
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_yanju,
                R.mipmap.beauty_btn_eyespan_default,
                R.mipmap.beauty_btn_eyespan_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_YAN_JU
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_kaiyanjiao,
                R.mipmap.beauty_btn_openeye_default,
                R.mipmap.beauty_btn_openeye_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_KAI_YAN_JIAO
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_xuelian,
                R.mipmap.beauty_btn_cutface_default,
                R.mipmap.beauty_btn_cutface_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_XUE_LIAN
            )
        )
        beans.add(
            MeiYanBean(
                R.string.beauty_mh_changbi,
                R.mipmap.beauty_btn_longnose_default,
                R.mipmap.beauty_btn_longnose_sele,
                MHConfigConstants.MEI_YAN_MEI_XING_CHANG_BI
            )
        )
        beans = MHSDK.getFunctionItems(
            beans,
            MHConfigConstants.MEI_YAN,
            MHConfigConstants.MEI_YAN_MEI_XING_FUNCION
        ) as ArrayList<MHCommonBean>


//        var beansTx: MutableList<MHCommonBean> = java.util.ArrayList()
//        beansTx.add(MeiYanTypeBean(R.string.beauty_mh_003, MHConfigConstants.TE_XIAO_FUNCTION))
//        beansTx.add(
//            MeiYanTypeBean(
//                R.string.beauty_mh_014,
//                MHConfigConstants.TE_XIAO_SHUI_YIN_FUNCTION
//            )
//        )
//        beansTx.add(
//            MeiYanTypeBean(
//                R.string.beauty_mh_015,
//                MHConfigConstants.TE_XIAO_DONG_ZUO_FUNCTION
//            )
//        )
//        beansTx.add(
//            MeiYanTypeBean(
//                R.string.beauty_mh_004,
//                MHConfigConstants.TE_XIAO_HA_HA_JING_FUNCTION
//            )
//        )
//        val meiYanTypeBean = beansTx[0] as MeiYanTypeBean
//        meiYanTypeBean.isChecked = true
//
//        beansTx = MHSDK.getFunctions(beansTx, MHConfigConstants.TE_XIAO)

        mhManager?.setMinFaceSize(boxCfg.minFaceSize)
        mhManager?.setMaxFace(boxCfg.maxFaceSize)
        mhManager?.setSkinWhiting(boxCfg.skinWhiting)
        mhManager?.setSkinSmooth(boxCfg.skinSmooth)
        mhManager?.setBrightness(boxCfg.brightness)
        mhManager?.setBigEye(boxCfg.bigEye)
        mhManager?.setFaceLift(boxCfg.faceLift)
        mhManager?.setMouseLift(boxCfg.mouseLift)
        mhManager?.setNoseLift(boxCfg.noseLift)
        mhManager?.setChinLift(boxCfg.chinLift)
        mhManager?.setForeheadLift(boxCfg.foreHeadLift)
        mhManager?.setEyeBrow(boxCfg.eyeBrow)
        mhManager?.setEyeCorner(boxCfg.eyeCorner)
        mhManager?.setEyeLength(boxCfg.eyeLength)
        mhManager?.setEyeAlat(50)
        mhManager?.setFaceShave(50)
        mhManager?.setLengthenNoseLift(50)

        var userfaces = mhManager?.useFaces
        if (userfaces != null && userfaces.isNotEmpty()) {
            userfaces[0] = 1
            if(userfaces.size > 1) {
                userfaces[1] = 1
            }
            mhManager?.setUseFace(true)
            mhManager?.useFaces = userfaces
        }
        waterList.add(TeXiaoWaterBean(com.meihu.beauty.R.mipmap.ic_mh_none, 0, MHSDK.WATER_NONE, true))
        waterList.add(
            TeXiaoWaterBean(
                com.meihu.beauty.R.mipmap.ic_water_thumb_0,
                com.meihu.beauty.R.mipmap.ic_water_res_0,
                MHSDK.WATER_TOP_LEFT
            )
        )
        waterList.add(
            TeXiaoWaterBean(
                com.meihu.beauty.R.mipmap.ic_water_thumb_1,
                com.meihu.beauty.R.mipmap.ic_water_res_1,
                MHSDK.WATER_TOP_RIGHT
            )
        )
        waterList.add(
            TeXiaoWaterBean(
                com.meihu.beauty.R.mipmap.ic_water_thumb_2,
                com.meihu.beauty.R.mipmap.ic_water_res_2,
                MHSDK.WATER_BOTTOM_LEFT
            )
        )
        waterList.add(
            TeXiaoWaterBean(
                com.meihu.beauty.R.mipmap.ic_water_thumb_3,
                com.meihu.beauty.R.mipmap.ic_water_res_3,
                MHSDK.WATER_BOTTOM_RIGHT
            )
        )
        when(boxCfg.waterPos) {
            MHSDK.WATER_TOP_LEFT -> {
                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_0, MHSDK.WATER_TOP_LEFT)
            }
            MHSDK.WATER_TOP_RIGHT -> {
                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_1, MHSDK.WATER_TOP_RIGHT)
            }
            MHSDK.WATER_BOTTOM_LEFT -> {
                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_2, MHSDK.WATER_BOTTOM_LEFT)
            }
            MHSDK.WATER_BOTTOM_RIGHT -> {
                MhDataManager.getInstance().setWater(R.mipmap.ic_water_res_3, MHSDK.WATER_BOTTOM_RIGHT)
            }
            else -> {

            }
        }

    }
    private fun reInitMhManager() {
        MhDataManager.getInstance().release()
        mhManagerInit()
    }
    private fun startMediaServer() : Int {
        return mediaJni.openMediaServer(AssetFile.assetSdPath(this), object: MediaJni.IDecodeListener {
            override fun onDecodeCallback(
                data: ByteArray?,
                len: Int,
                w: Int,
                h: Int,
                keyFrame: Int
            ) {
                Log.d("mediaJni", "onDecodeCallback, len=${len},w=${w}, h=${h}, key=${keyFrame}")
            }

            override fun onRenderTextureId(
                textureId1: Int,
                textureId2: Int,
                textureId3: Int,
                data: ByteArray?,
                w: Int,
                h: Int
            ): Int {
                var newId = MhDataManager.getInstance().render(textureId1, w, h)
                Log.d("mediaJni", "onRenderTextureId, id1=${textureId1}, newId=${newId}")
                return newId
            }

            override fun onRenderInit() {
                reInitMhManager()
            }
        })
    }

    private fun stopMediaServer() {
        mediaJni.closeMediaServer()
    }
    private fun onRecordTestClick() {
        val intent = Intent()
        intent.setClass(this, RecordTestActivity::class.java)
        this.startActivity(intent)
    }

    override fun onDestroy() {
        MhDataManager.getInstance().release()
        super.onDestroy()
    }
}
