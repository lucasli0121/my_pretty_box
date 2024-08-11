package com.effectsar.labcv.common.abe;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;


public class TESTActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
/*
        RenderConfig renderConfig = new RenderConfig(glSurfaceView)
                .configUse3Buffer(false)
                .configUsePipeline(true)
                .configUse3Buffer(false)
                .configEGLContextClientVersion(3);

        BECameraSourceImpl beCameraSource = BECameraSourceImpl.build(this, glSurfaceView)
                .configPreviewSize(1280, 720)
                .configCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);


        BERender beRender = BERender.build(this)
                .configRenderConfig(renderConfig)
                .configSourceProvider(beCameraSource)
                .configRenderCallBack(new IRenderCallBack() {
                    @Override
                    public void onSurfaceCreated() {
                        beCameraSource.open();
                    }

                    @Override
                    public void onDrawFrame(RenderBean renderBean) {
                    }
                });

        BERenderMgr beRenderMgr = BERenderMgr.build(beCameraSource)
                .configBERenderImpl(beRender)
                .configBESourceImpl(beCameraSource)
                ;*/

    }
}
