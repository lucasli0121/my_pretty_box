package com.media.demo

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.iceteck.silicompressorr.FileUtils
import com.media.demo.mysdk.MeiyanApi
import com.media.demo.mysdk.ali.AliApi
import com.media.demo.mysdk.byted.BytedApi
import com.media.demo.mysdk.meihu.MeihuApi
import com.media.demo.obj.BoxConf
import com.media.demo.util.AssetFile
import com.media.demo.util.PermissionsUtils
import com.medialib.jni.MediaJni
import com.updatelibrary.UpdateMgr

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val mediaJni = MediaJni()
    private var boxCfg: BoxConf? = null
    private var myApi: MeiyanApi? = null

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
        val beginPlay = findViewById<Button>(R.id.begin_play)
        beginPlay.setOnClickListener { beginPlayTest() }
        val stopPlay = findViewById<Button>(R.id.stop_play)
        stopPlay.setOnClickListener { stopPlay() }
    }

    fun beginPlayTest() {
        mediaJni.putTestMediaFile(AssetFile.getSampleVideoFile(this@MainActivity))
    }
    fun stopPlay() {
        mediaJni.stopTestMedia()
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
        boxCfg = BoxConf.initConfig(this)
        if (boxCfg == null) {
            Log.e("MainActivity", "init json config failed")
            finish()
        }
        val maxMem = Runtime.getRuntime().maxMemory()
        Log.d("MainActivity", "com.media.demo max memory=${maxMem}")

        myApi = when(boxCfg!!.sdkType) {
            BoxConf.AliSdk ->
                AliApi(this)

            BoxConf.MeihuSdk ->
                MeihuApi(this)

            BoxConf.BytedSdk ->
                BytedApi(this)

            else ->
                null
        }
        if(myApi == null) {
            Log.e("MainActivity", "SDK Type is wrong")
            finish()
        }
        if (!myApi!!.init(boxCfg)) {
            Log.e("MainActivity", "SDK init failed,finished")
            finish()
        }

        mediaJni.setParams(boxCfg!!.useSdk,
            boxCfg!!.enableDecode,
            boxCfg!!.rtmpLocalPort,
            boxCfg!!.rtmpRemoteUrl,
            boxCfg!!.width,
            boxCfg!!.height,
            boxCfg!!.chunkSize)

        val glSurface = findViewById<SurfaceView>(R.id.gl_surface)
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

    private fun createAddressInAli(url: String): Boolean {

        return true
    }
    private fun reInitMeiyanApi() {
        if (!myApi!!.reinit()) {
            Log.e("MainActivity", "reinit meiyan api failed")
        }
        System.gc()
    }
    private fun startMediaServer() : Int {
        return mediaJni.openMediaServer(AssetFile.assetSdPath(this), object: MediaJni.IDecodeListener {
            override fun onStartServerCallback(result: Int) {
//                mediaJni.putTestMediaFile(AssetFile.getSampleVideoFile(this@MainActivity))
            }

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
                textureId: Int,
                len: Int,
                w: Int,
                h: Int
            ): Int {
                val newId = myApi!!.renderTextureId(textureId, len, w, h)
                Log.d("mediaJni", "onRenderTextureId, id1=${textureId}, newId=${newId}")
                return newId
            }

            override fun onRenderBuffer(data: ByteArray?, len: Int, w: Int, h: Int): Int {
                if (data == null) {
                    return 0
                }
                return myApi!!.renderBuffer(data, len, w, h)
            }

            override fun onRenderInit() {
                reInitMeiyanApi()
            }

            override fun onRenderStop() {
                myApi!!.unInit()
                System.gc()
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
        myApi?.unInit()
        super.onDestroy()
    }
}
