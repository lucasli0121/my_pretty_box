package com.effectsar.labcv.core.algorithm;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.effectsar.labcv.core.algorithm.base.AlgorithmResourceProvider;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTask;
import com.effectsar.labcv.core.algorithm.base.AlgorithmTaskKey;
import com.effectsar.labcv.core.license.EffectLicenseProvider;
import com.effectsar.labcv.core.util.LogUtils;
import com.effectsar.labcv.core.util.timer_record.LogTimerRecord;
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants;
import com.effectsar.labcv.effectsdk.ObjectTracking;
import com.effectsar.labcv.licenselibrary.EffectsSDKLicenseInterface;

import java.nio.ByteBuffer;

public class ObjectTrackingAlgorithmTask extends AlgorithmTask<ObjectTrackingAlgorithmTask.ObjectTrackingResourceProvider, ObjectTrackingAlgorithmTask.ObjectTrackingRenderInfo>
{
    public static final AlgorithmTaskKey OBJECT_TRACKING  = AlgorithmTaskKey.createKey("object_tracking", true);
    public static final AlgorithmTaskKey OBJECT_TRACKING_START  = AlgorithmTaskKey.createKey("object_tracking_start");
    public static final AlgorithmTaskKey OBJECT_TRACKING_TOUCH_EVENT  = AlgorithmTaskKey.createKey("object_tracking_touch");

    public ObjectTracking mDetector = null;
    private final PointF mFirstPoint;
    private final PointF mLastPoint;
    private boolean mIsTracking = false;
    private boolean mBoxNeedInitialize = false;
    private boolean mBoxInited = false;
    private final ObjectTrackingRenderInfo mObjectTrackingRenderInfo;

    public ObjectTrackingAlgorithmTask(Context context, ObjectTrackingAlgorithmTask.ObjectTrackingResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new ObjectTracking();
        mFirstPoint = new PointF();
        mLastPoint = new PointF();
        mObjectTrackingRenderInfo = new ObjectTrackingRenderInfo();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        int ret = mDetector.init(mContext, mResourceProvider.objectTrackingModel(),
                mLicenseProvider.getLicensePath(EffectsSDKLicenseInterface.LICENSE_FUNCTION_NAME.OBJECT_TRACKING_STR),
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("ObjectTrackingAlgorithmTask init", ret)) return ret;

        return 0;
    }

    @Override
    public ObjectTrackingRenderInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("Object Tracking");
        if (mBoxNeedInitialize) {
            ObjectTracking.ObjectTrackingBoundBox boundBox = new ObjectTracking.ObjectTrackingBoundBox();
            boundBox.centerX = (mFirstPoint.x + mLastPoint.x )/2.f;
            boundBox.centerY = (mFirstPoint.y + mLastPoint.y )/2.f;
            boundBox.width = Math.abs (mFirstPoint.x - mLastPoint.x );
            boundBox.height = Math.abs (mFirstPoint.y - mLastPoint.y);
            boundBox.rotateAngle = 0.0f;

            mObjectTrackingRenderInfo.boundBox = boundBox;
            mObjectTrackingRenderInfo.boxInited = true;
        } else {
            mObjectTrackingRenderInfo.boxInited = false;
            mObjectTrackingRenderInfo.isTracking = false;
        }

        if (mIsTracking && !mBoxInited && mBoxNeedInitialize) {
            mBoxNeedInitialize = false;
            int statues =  mDetector.setInitBox(buffer, width, height, stride, 4, pixlFormat.getValue(), mObjectTrackingRenderInfo.boundBox);
            mBoxInited = statues == 0;
        }

        if (mIsTracking && mBoxInited ) {
            ObjectTracking.ObjectTrackingBoundBox result = new ObjectTracking.ObjectTrackingBoundBox();

            mDetector.trackFrame(buffer, width, height, stride, 4, pixlFormat.getValue(), 0.f, result);
            mObjectTrackingRenderInfo.boundBox = result;
            mObjectTrackingRenderInfo.isTracking = true;
        }

        LogTimerRecord.STOP("Object Tracking");
        return mObjectTrackingRenderInfo;
    }

    @Override
    public ObjectTrackingRenderInfo process(ByteBuffer buffer, int width, int height, int stride, EffectsSDKEffectConstants.PixlFormat pixlFormat, EffectsSDKEffectConstants.Rotation rotation, long timeStamp) {
        return super.process(buffer, width, height, stride, pixlFormat, rotation, timeStamp);
    }

    @Override
    public int destroyTask() {
        if (mDetector != null) {
            mDetector.destroy();
            mBoxInited = false;
            mBoxNeedInitialize = false;

        }
        return 0;
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);

        if (key.getKey() == OBJECT_TRACKING_TOUCH_EVENT.getKey()) {
            if (mIsTracking) return ;

            if (p instanceof MotionEvent) {
                 if (((MotionEvent) p).getAction() == MotionEvent.ACTION_DOWN) {
                     mBoxNeedInitialize = false;
                     mFirstPoint.x = ((MotionEvent) p).getX();
                     mFirstPoint.y = ((MotionEvent) p).getY();

                 } else if (((MotionEvent) p).getAction() == MotionEvent.ACTION_MOVE) {
                     mLastPoint.x = ((MotionEvent) p).getX();
                     mLastPoint.y = ((MotionEvent) p).getY();
                     mBoxNeedInitialize = true;
                 }
            }
        } else if (key.getKey() == ObjectTrackingAlgorithmTask.OBJECT_TRACKING_START.getKey()) {
            mIsTracking = getBoolConfig(OBJECT_TRACKING_START) ;
            if(!mIsTracking) {
                mBoxInited = false;
                mBoxNeedInitialize = false;
                mObjectTrackingRenderInfo.boxInited = false;
            }
        } else if (key.getKey() == OBJECT_TRACKING.getKey()) {
            if(!getBoolConfig(OBJECT_TRACKING)) {
                mBoxInited = false;
                mBoxNeedInitialize = false;
                mObjectTrackingRenderInfo.boxInited = false;
            }
        }
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return OBJECT_TRACKING;
    }

    public interface ObjectTrackingResourceProvider extends AlgorithmResourceProvider{
        String objectTrackingModel();
    }

    public class ObjectTrackingRenderInfo {
        public ObjectTracking.ObjectTrackingBoundBox boundBox;
        public boolean isTracking;
        public boolean boxInited;
    }
}




