package com.media.demo

import android.Manifest
import android.content.Intent
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.media.demo.util.AssetFile
import com.media.demo.util.PermissionsUtils
import com.medialib.jni.MediaJni
import com.meihu.beauty.utils.MhDataManager
import com.meihu.beautylibrary.manager.MHBeautyManager
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val mediaJni = MediaJni()
    private var mhManager: MHBeautyManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.setCurrentActivity(this)
        //设置底部虚拟状态栏为透明，并且可以充满，4.4以上才有
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
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
        mhManager = MHBeautyManager(this)
        mhManager?.setSkinWhiting(8)
        mhManager?.setSkinSmooth(9)
        mhManager?.setBrightness(50)
        mhManager?.setBigEye(100)
        mhManager?.setFaceLift(100)
        mhManager?.setMouseLift(100)
        mhManager?.setNoseLift(100)
        mhManager?.setChinLift(100)
        mhManager?.setForeheadLift(100)
        mhManager?.setEyeBrow(100)
        mhManager?.setEyeCorner(100)
        mhManager?.setEyeLength(100)
        mhManager?.setEyeAlat(100)
        mhManager?.setFaceShave(100)
        mhManager?.setLengthenNoseLift(100)
        var userfaces = mhManager?.useFaces
        if (userfaces != null && userfaces.isNotEmpty()) {
            userfaces[0] = 1
            if(userfaces.size > 1) {
                userfaces[1] = 1
            }
            mhManager?.setUseFace(true)
            mhManager?.useFaces = userfaces
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
