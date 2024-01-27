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

        // Example of a call to a native method.
//        if(mediaJni.open(AssetFile.getMediaLogFile(this)) != -1) {
//            mediaJni.testMediaCodec("mediacodec", AssetFile.getInputVideoFile(this), AssetFile.getOutputVideoFile(this))
//        }
    }
    private fun mhManagerInit() {
        MhDataManager.getInstance().create(applicationContext)
        mhManager = MHBeautyManager(this)
        mhManager?.setSkinWhiting(5)
        mhManager?.setSkinSmooth(5)
        mhManager?.setBrightness(80)
        mhManager?.setBigEye(80)
        mhManager?.setFaceLift(80)
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
