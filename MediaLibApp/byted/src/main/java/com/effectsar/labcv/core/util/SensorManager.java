package com.effectsar.labcv.core.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorManager  {
    private final OnSensorChangedListener mListener;
    private android.hardware.SensorManager mManager;

    private Sensor mAcceleratorSensor;
    private Sensor mGyroscopeSensor;
    private Sensor mGravitySensor;
    private Sensor mRotationVectorSensor;
    private boolean mHasAccelerator = false;
    private boolean mHasGyroscope = false;
    private boolean mHasGravity = false;
    private boolean mHasOrientation = false;

    public SensorManager(OnSensorChangedListener listener) {
        this.mListener = listener;
    }

    public boolean startSlamSensor(Context context) {
        if (mManager == null) {
            mManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        if (mManager == null) {
            mAcceleratorSensor = null;
            mGravitySensor = null;
            mGyroscopeSensor = null;
            mRotationVectorSensor = null;
            return false;
        }
        mGyroscopeSensor = mManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAcceleratorSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravitySensor = mManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mRotationVectorSensor = mManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        if (mGyroscopeSensor != null) {
            mHasGyroscope = true;
            mManager.registerListener(mGryoscopListener, mGyroscopeSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mAcceleratorSensor != null) {
            mHasAccelerator = true;
            mManager.registerListener(mAcceleratorListener, mAcceleratorSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mGravitySensor != null) {
            mHasGravity = true;
            mManager.registerListener(mGravityListenser, mGravitySensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mRotationVectorSensor != null) {
            mHasOrientation = true;
            mManager.registerListener(mRotationVectorListener, mRotationVectorSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            mRotationVectorSensor = mManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (mRotationVectorSensor != null) {
                mHasOrientation = true;
            }
            mManager.registerListener(mRotationVectorListener, mRotationVectorSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        }

        return true;
    }

    public void stopSlamSensor(Context context) {
        mManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(mManager == null) {
            return;
        }
        if (mAcceleratorSensor != null) {
            mManager.unregisterListener(mAcceleratorListener, mAcceleratorSensor);
        }
        if (mGravitySensor != null) {
            mManager.unregisterListener(mGravityListenser, mGravitySensor);
        }
        if (mGyroscopeSensor != null) {
            mManager.unregisterListener(mGryoscopListener, mGyroscopeSensor);
        }
        if (mRotationVectorSensor != null) {
            mManager.unregisterListener(mRotationVectorListener, mRotationVectorSensor);
        }
    }

    private final SensorEventListener mGryoscopListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double time = sensorEvent.timestamp;
            if (mListener != null)  {
                mListener.onGryoscopChanged(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], time /1.e9);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener mGravityListenser = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double time = sensorEvent.timestamp;
            if (mListener != null)  {
                mListener.onGravityChanged(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], time /1.e9);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener mAcceleratorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double time = sensorEvent.timestamp;
            if (mListener != null)  {
                mListener.onAcceleratorChanged(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], time /1.e9);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener mRotationVectorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double time = sensorEvent.timestamp;
            float[] rb = new float[9];
            android.hardware.SensorManager.getRotationMatrixFromVector(rb, sensorEvent.values);
            double[] data = new double[9];
            for (int i = 0; i < 9; i++) {
                data[i] = rb[i];
            }
            if (mListener != null) {
                mListener.onRotationVectorChanged(data, time/1.e9);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public boolean hasAccelerator() {
        return mHasAccelerator;
    }

    public boolean hasGyroscope() {
        return mHasGyroscope;
    }

    public boolean hasGravity() {
        return mHasGravity;
    }

    public boolean hasOrientation() {
        return mHasOrientation;
    }

    public interface OnSensorChangedListener {

        void onAcceleratorChanged(double dx, double dy, double dz, double timeStamp);
        void onGravityChanged(double dx, double dy, double dz, double timeStamp);
        void onGryoscopChanged(double dx, double dy, double dz, double timeStamp);
        void onRotationVectorChanged(double[] d, double timeStamp);

    }
}
