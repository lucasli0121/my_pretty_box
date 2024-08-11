package com.effectsar.labcv.core.opengl;


import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;

public class ProgramManager {


    public ProgramManager() {
    }

    private ProgramTexture2d mProgramTexture2D;
    private ProgramTextureOES mProgramTextureOES;
    private ProgramCompareTexture2d mProgramCompareTexture2d;

    public  Program getProgram(EffectsSDKEffectConstants.TextureFormat srcTetxureFormat){
        switch (srcTetxureFormat){
            case Texure2D:
                if (null == mProgramTexture2D){
                    mProgramTexture2D = new ProgramTexture2d();
                }
                return mProgramTexture2D;
            case Texture_Oes:
                if (null == mProgramTextureOES) {
                    mProgramTextureOES = new ProgramTextureOES();
                }
                return mProgramTextureOES;
        }
        return null;

    }

    public Program getCompareProgram() {
        if (mProgramCompareTexture2d == null) {
            mProgramCompareTexture2d = new ProgramCompareTexture2d();
        }

        return mProgramCompareTexture2d;
    }

    public void release(){
        if (null != mProgramTexture2D){
            mProgramTexture2D.release();
            mProgramTexture2D = null;

        }
        if (null != mProgramTextureOES){
            mProgramTextureOES.release();
            mProgramTextureOES = null;

        }

        if (null != mProgramCompareTexture2d){
            mProgramCompareTexture2d.release();
            mProgramCompareTexture2d = null;
        }
    }
}
