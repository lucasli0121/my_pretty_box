/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.effectsar.labcv.core.opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static android.opengl.GLES20.GL_FRAMEBUFFER;


public class ProgramTextureOES extends Program {

    // Simple vertex shader, used for all programs.
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uUVMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    vec4 TexCoord = uUVMatrix * vec4(aTextureCoord, 0.0, 1.0);\n" +
                    "    vTextureCoord = TexCoord.xy;\n" +
                    "}\n";

    // Simple fragment shader for use with external 2D textures (e.g. what we get from
    // SurfaceTexture).
    private static final String FRAGMENT_SHADER_EXT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private int muMVPMatrixLoc;
    private int muUVMatrixLoc;
    private int maPositionLoc;
    private int maTextureCoordLoc;
    private final float[] identityMat = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Prepares the program in the current EGL context.
     */
    public ProgramTextureOES() {
        super(VERTEX_SHADER, FRAGMENT_SHADER_EXT);
    }

    @Override
    protected Drawable2d getDrawable2d() {
        return new Drawable2d(Drawable2d.Prefab.FULL_RECTANGLE);
    }

    @Override
    protected void getLocations() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        muUVMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uUVMatrix");
        GlUtil.checkLocation(muUVMatrixLoc, "uUVMatrix");
    }

    @Override
    public void drawFrameOnScreen(int textureId,int width, int height,  float[] mvpMatrix) {
        GlUtil.checkGlError("draw start");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glUniformMatrix4fv(muUVMatrixLoc, 1, false, identityMat, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");


        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, Drawable2d.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, Drawable2d.VERTEXTURE_STRIDE, mDrawable2d.getVertexArray());
        GlUtil.checkGlError("glVertexAttribPointer");

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, Drawable2d.TEXTURE_COORD_STRIDE, mDrawable2d.getTexCoordArray());
        GlUtil.checkGlError("glVertexAttribPointer");
//        GLES20.glViewport(0, 0, width, height);

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mDrawable2d.getVertexCount());
        GlUtil.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }


    @Override
    public int drawFrameOffScreen(int textureId, int width, int height, float[] mvpMatrix) {
        GlUtil.checkGlError("draw start");
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        initFrameBufferIfNeed(width, height);
        GlUtil.checkGlError("initFrameBufferIfNeed");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GlUtil.checkGlError("glBindTexture");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GlUtil.checkGlError("glBindFramebuffer");

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glUniformMatrix4fv(muUVMatrixLoc, 1, false, identityMat, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glViewport(0, 0, width, height);
        offScreenDraw();
        return mFrameBufferTextures[0];
    }

    public int drawFrameOffScreen(int textureId, int width, int height, float[] mvpMatrix, float[] uvMatrix) {
        if (Arrays.equals(mvpMatrix, identityMat) && Arrays.equals(uvMatrix, identityMat)) {
            // invalid uv input
            return 0;
        }
        GlUtil.checkGlError("draw start");
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        initFrameBufferIfNeed(width, height);
        GlUtil.checkGlError("initFrameBufferIfNeed");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GlUtil.checkGlError("glBindTexture");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GlUtil.checkGlError("glBindFramebuffer");

        // Copy the model / view / projection matrix over.
        mvpMatrix[5] *= -1.0;
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glUniformMatrix4fv(muUVMatrixLoc, 1, false, uvMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glViewport(0, 0, width, height);
        offScreenDraw();
        return mFrameBufferTextures[0];
    }

    private void offScreenDraw() {
        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, Drawable2d.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, Drawable2d.VERTEXTURE_STRIDE, mDrawable2d.getVertexArray());
        GlUtil.checkGlError("glVertexAttribPointer");

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, Drawable2d.TEXTURE_COORD_STRIDE, mDrawable2d.getTexCoorArrayFB());
        GlUtil.checkGlError("glVertexAttribPointer");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mDrawable2d.getVertexCount());
        GlUtil.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(0);
    }

    @Override
    public int drawFrameOffScreenForCompare(int textureId, int src_textureId, float progress, int width, int height, float[] mvpMatrix){
        return 0;
    }

    /** {zh} 
     * 读取渲染结果的buffer
     * @param width 目标宽度
     * @param height 目标高度
     * @return 渲染结果的像素Buffer 格式RGBA
     */
    /** {en} 
     * Read the buffer
     * @param width target width
     * @param height target height
     * @return pixel Buffer  format of the rendered result RGBA
     */

    @Override
    public ByteBuffer readBuffer(int textureId, int width, int height) {
        if ( textureId == GlUtil.NO_TEXTURE) {
            return null;
        }
        if (width* height == 0){
            return  null;
        }

        ByteBuffer mCaptureBuffer = ByteBuffer.allocateDirect(width* height*4);

        mCaptureBuffer.position(0);
        int[] frameBuffer = new int[1];
        GLES20.glGenFramebuffers(1,frameBuffer,0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);
        GLES20.glReadPixels(0, 0, width, height,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mCaptureBuffer);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if (null != frameBuffer) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
        }
        return mCaptureBuffer;
    }
}
