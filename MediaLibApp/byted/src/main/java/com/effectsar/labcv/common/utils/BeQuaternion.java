package com.effectsar.labcv.common.utils;

import android.renderscript.Matrix4f;

public class BeQuaternion {
    public float x, y, z, w;

    public BeQuaternion() {
        x = 0; y = 0; z = 0; w = 1.0f;
    }

    public BeQuaternion(BeQuaternion other) {
        x = other.x; y = other.y; z = other.z; w = other.w;
    }


    public BeQuaternion(float w, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static BeQuaternion loadMultiply(BeQuaternion p, BeQuaternion q){
        BeQuaternion r = new BeQuaternion();

        r.w = p.w * q.w - p.x * q.x - p.y * q.y - p.z * q.z;
        r.x = p.w * q.x + p.x * q.w + p.y * q.z - p.z * q.y;
        r.y = p.w * q.y + p.y * q.w + p.z * q.x - p.x * q.z;
        r.z = p.w * q.z + p.z * q.w + p.x * q.y - p.y * q.x;

        return r;
    }

    public void multiply(BeQuaternion q){
        BeQuaternion r = new BeQuaternion();

        r.w = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
        r.x = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
        r.y = this.w * q.y + this.y * q.w + this.z * q.x - this.x * q.z;
        r.z = this.w * q.z + this.z * q.w + this.x * q.y - this.y * q.x;

        this.x = r.x; this.y = r.y; this.z = r.z; this.w = r.w;
    }

    public static boolean isValid(BeQuaternion qua) {
        float value = qua.x * qua.x + qua.y * qua.y + qua.z * qua.z + qua.w * qua.w;
        return Math.abs(value - 1.0) < 0.2;
    }


    /*
            Result[0][0] = T(1) - T(2) * (qyy +  qzz);
            Result[0][1] = T(2) * (qxy + qwz);
            Result[0][2] = T(2) * (qxz - qwy);

            tmp[1][0] = 2.f * (qxy - qwz);
            tmp[1][1] = 1.f - 2.f * (qxx +  qzz);
            tmp[1][2] = 2.f * (qyz + qwx);

            tmp[2][0] = 2.f * (qxz + qwy);
            tmp[2][1] = 2.f * (qyz - qwx);
            tmp[2][2] = 1.f - 2.f * (qxx +  qyy);
     */
    public static BeMatrix4f
    toMatrix(BeQuaternion q){
        BeMatrix4f tmp = new BeMatrix4f();

        float qxx = q.x * q.x;
        float qyy = q.y * q.y;
        float qzz = q.z * q.z;
        float qxz = q.x * q.z;
        float qxy = q.x * q.y;
        float qyz = q.y * q.z;
        float qwx = q.w * q.x;
        float qwy = q.w * q.y;
        float qwz = q.w * q.z;


        tmp.set(0, 0,  1.f - 2.f * (qyy +  qzz));
        tmp.set(0, 1,  2.f * (qxy + qwz));
        tmp.set(0, 2,  2.f * (qxz - qwy));

        tmp.set(1, 0,  2.f * (qxy - qwz));
        tmp.set(1, 1,  1.f - 2.f * (qxx +  qzz));
        tmp.set(1, 2,  2.f * (qyz + qwx));

        tmp.set(2, 0,  2.f * (qxz + qwy));
        tmp.set(2, 1,  2.f * (qyz - qwx));
        tmp.set(2, 2,  1.f - 2.f * (qxx +  qyy));

        return tmp;
    }

    public static void
    toMatrix(BeQuaternion q, BeMatrix4f tmp){

        float x = q.x * 2.0F;
        float y = q.y * 2.0F;
        float z = q.z * 2.0F;
        float xx = q.x * x;
        float yy = q.y * y;
        float zz = q.z * z;
        float xy = q.x * y;
        float xz = q.x * z;
        float yz = q.y * z;
        float wx = q.w * x;
        float wy = q.w * y;
        float wz = q.w * z;

        // Calculate 3x3 matrix from orthonormal basis
        tmp.set(0, new BeVec4f( 1.0f - (yy + zz), xy + wz, xz - wy, 0.0F));

        tmp.set(1, new BeVec4f( xy - wz, 1.0f - (xx + zz), yz + wx, 0.0F));

        tmp.set(2, new BeVec4f( xz + wy, yz - wx, 1.0f - (xx + yy), 0.0F));

        tmp.set(3, new BeVec4f( 0, 0, 0, 1));

    }


    public void normalize(){
        float length = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        x /= length;
        y /= length;
        z /= length;
        w /= length;
    }
    @Override
    public String toString() {
        return "BeQuaternion{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}
