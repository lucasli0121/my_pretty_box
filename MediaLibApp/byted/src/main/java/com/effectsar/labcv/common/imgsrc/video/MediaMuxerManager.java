package com.effectsar.labcv.common.imgsrc.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import androidx.annotation.NonNull;


import com.effectsar.labcv.core.util.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.effectsar.labcv.common.utils.FileUtils.generateVideoFile;


public class MediaMuxerManager {

    private MediaMuxer mMediaMuxer;
    private String mOutputPath;
    private int mAudioTrack;
    private int mVideoTrack;
    private boolean mVideoTrackReady = false;
    private boolean mAudioTrackReady = false;
    private volatile boolean mStart = false;


    public MediaMuxerManager() {
        try {
            mOutputPath = generateVideoFile();
            mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public void addAudioTrack(MediaFormat format) {
        if (null == format) {
            mAudioTrackReady = true;
            ready();
            return;
        }
        try {
            mAudioTrack = mMediaMuxer.addTrack(format);
        } catch (Exception e) {
            throw new IllegalStateException("unsupport format: " + format);
        } finally {
            mAudioTrackReady = true;
            ready();
        }
    }


    public void addVideoTrack(MediaFormat format) {
        try {
            mVideoTrack = mMediaMuxer.addTrack(format);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mVideoTrackReady = true;
        ready();


    }

    public void ready() {
        if (mAudioTrackReady && mVideoTrackReady) {
            synchronized (this) {
                mMediaMuxer.start();
                mStart = true;
            }
        }

//        if (mVideoTrackReady) {
//            mMediaMuxer.start();
//            mStart = true;
//        }

    }

    public void addVideoData(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec.BufferInfo bufferInfo) {
        synchronized (this) {
            if (mStart) {
                LogUtils.e("addVideoData");
                mMediaMuxer.writeSampleData(mVideoTrack, byteBuffer, bufferInfo);

            }
        }
    }

    public void addAudioData(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec.BufferInfo bufferInfo) {
        synchronized (this) {
            if (mStart) {
                LogUtils.e("addAudioData");
                mMediaMuxer.writeSampleData(mAudioTrack, byteBuffer, bufferInfo);
            }
        }
    }


    /**
     * release muxer, will auto stop first
     */
    public void release() {
        synchronized (this) {
            if (mMediaMuxer != null) {
                mStart = false;
                try {
                    mMediaMuxer.release();
                } catch (IllegalStateException e) {
                    LogUtils.e(e.getMessage());
                }
                mMediaMuxer = null;
                mVideoTrackReady = false;
                mAudioTrackReady = false;
            }
        }
    }


}
