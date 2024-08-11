package com.effectsar.labcv.core.opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_FRAMEBUFFER;

public class ProgramTextureNV21 extends Program {
    public ProgramTextureNV21() {super(VERTEX_SHADER, FRAGMENT_SHADER_EXT);}

    // Simple vertex shader, used for all programs.
    private static final String VERTEX_SHADER = "precision highp float;\n" +
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "    gl_Position = uMVPMatrix * aPosition;\n" +
            "    vTextureCoord = aTextureCoord;\n" +
            "    //vTextureCoord.y = 1.0 - aTextureCoord.y;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER_EXT =
            "precision highp float;\n" +

            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D y_texture;\n" +
            "uniform sampler2D uv_texture;\n" +

            "void main (void){\n" +
            "   float r, g, b, y, u, v;\n" +

            //We had put the Y values of each pixel to the R,G,B components by
            //GL_LUMINANCE, that's why we're pulling it from the R component,
            //we could also use G or B
            "y = texture2D(y_texture, vTextureCoord).r;\n" +

            //We had put the U and V values of each pixel to the A and R,G,B
            //components of the texture respectively using GL_LUMINANCE_ALPHA.
            //Since U,V bytes are interspread in the texture, this is probably
            //the fastest way to use them in the shader
            "   u = texture2D(uv_texture, vTextureCoord).a - 0.5;\n" +
            "   v = texture2D(uv_texture, vTextureCoord).r - 0.5;\n" +

            //The numbers are just YUV to RGB conversion constants
            "   r = y + 1.13983*v;\n" +
            "   g = y - 0.39465*u - 0.58060*v;\n" +
            "   b = y + 2.03211*u;\n" +

            //We finally set the RGB color of our pixel
            "   gl_FragColor = vec4(r, g, b, 1.0);\n" +
            "}\n";

    private int uMVPMatrixLoc;
    private int aPositionLoc;
    private int aTextureCoordLoc;
    private int uYTextureLoc;
    private int uUVTextureLoc;
    private int yTexture = -1;
    private int uvTexture = -1;

    @Override
    protected Drawable2d getDrawable2d() {
        return new Drawable2d(Drawable2d.Prefab.FULL_RECTANGLE);
    }

    @Override
    protected void getLocations() {
        aPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        aTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        uMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        uYTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "y_texture");
        uUVTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uv_texture");
        GlUtil.checkLocation(aPositionLoc, "uMVPMatrix");
    }

    @Override
    public void drawFrameOnScreen(int textureId, int width, int height, float[] mvpMatrix) {
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        GlUtil.checkGlError("glBindFramebuffer");

        GLES20.glUseProgram(mProgramHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture);
        GLES20.glUniform1i(uYTextureLoc, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture);
        GlUtil.checkGlError("glBindTexture");

        GLES20.glUniform1i(uUVTextureLoc, 1);
        GlUtil.checkGlError("glUniform1i");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GlUtil.checkGlError("glBindFramebuffer");

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");
        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(aPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(aPositionLoc, Drawable2d.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, Drawable2d.VERTEXTURE_STRIDE, mDrawable2d.getVertexArray());
        GlUtil.checkGlError("glVertexAttribPointer");

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(aTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(aTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, Drawable2d.TEXTURE_COORD_STRIDE, mDrawable2d.getTexCoordArray());
        GlUtil.checkGlError("glVertexAttribPointer");

        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mDrawable2d.getVertexCount());
        GlUtil.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(aPositionLoc);
        GLES20.glDisableVertexAttribArray(aTextureCoordLoc);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(0);
    }

    @Override
    public int drawFrameOffScreen(int textureId, int width, int height, float[] mvpMatrix) {
        return 0;
    }

    @Override
    public int drawFrameOffScreenForCompare(int textureId, int src_textureId, float progress, int width, int height, float[] mvpMatrix){
        return 0;
    }

    @Override
    public ByteBuffer readBuffer(int textureId, int width, int height) {
        return null;
    }

    public void updateTexture(byte[] bytes, int width, int height) {
        if (bytes == null || width <= 0 || height <= 0) return ;

        int[] tmp = new int[1];

        GlUtil.checkGlError("updateTexture");

        if (uvTexture == -1) {
            GLES20.glGenTextures(1, tmp, 0);
            uvTexture = tmp[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        if (yTexture == -1) {
            GLES20.glGenTextures(1, tmp, 0);
            yTexture = tmp[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        ByteBuffer yBuffer = ByteBuffer.allocate(width * height), uvBuffer = ByteBuffer.allocate(width * height);
        yBuffer.put(bytes, 0, width*height);
        yBuffer.position(0);

        uvBuffer.put(bytes, width*height, width*height/2);
        uvBuffer.position(0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture);
        GlUtil.checkGlError("glBindTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height,
                0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);
        GlUtil.checkGlError("glTexImage2D");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, width / 2, height/ 2,
                0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, uvBuffer);
        GlUtil.checkGlError("glTexImage2D");
    }

    @Override
    public void release() {
        if (GLES20.glIsTexture(uvTexture)) {
            GLES20.glDeleteTextures(1, new int[]{uvTexture}, 0);
            uvTexture = -1;
        }
        if (GLES20.glIsTexture(yTexture)) {
            GLES20.glDeleteTextures(1, new int[]{yTexture}, 0);
            yTexture = -1;
        }

        super.release();
    }
}
