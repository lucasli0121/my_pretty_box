package com.effectsar.labcv.common.utils;

import android.renderscript.Matrix4f;

import java.util.Arrays;

public class BeMatrix4f{
    public BeVec4f[] vals = null;
    public BeMatrix4f() {
        vals = new BeVec4f[4];

        for (int i = 0; i < vals.length; i ++) {
            vals[i] = new BeVec4f();
        }
        loadIdentity();
    }

    public BeMatrix4f(BeVec4f[] vec4s) {
        vals = new BeVec4f[4];
        System.arraycopy(vec4s, 0, vals, 0, vec4s.length);
    }

    public void loadIdentity(){
        for (int i = 0; i < 16; i++) {
            vals[i / 4].val[i % 4] = 0.f;
        }
        vals[0].setValue(0, 1);
        vals[1].setValue(1, 1);
        vals[2].setValue(2, 1);
        vals[3].setValue(3, 1);

    }
    public BeMatrix4f(BeMatrix4f other) {
        vals = new BeVec4f[4];
        for (int i = 0; i < 4; i ++) {
            vals[i] = new BeVec4f(other.vals[i]);
        }
    }

    public void CopyFrom(BeMatrix4f other) {
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j ++) {
                this.set(i, j, other.get(i, j));
            }
        }
    }

    public void transpose() {
        for (int i = 0; i < 4; i ++) {
            for (int j = i + 1; j < 4; j++) {
                float tmp = vals[i].val[j];
                vals[i].val[j] = vals[j].val[i];
                vals[j].val[i] = tmp;
            }
        }
    }

    public BeMatrix4f(float[] other){
        assert (other != null);
        vals = new BeVec4f[4];
        for (int i = 0; i < vals.length; i ++) {
            vals[i] = new BeVec4f();
        }

        for (int i = 0; i < 16; i++) {
            vals[i / 4].setValue(i % 4, other[i]);
        }
    }

    public void scale(float x, float y, float z) {
        vals[0].scale(x);
        vals[1].scale(y);
        vals[2].scale(z);
    }

    public static BeMatrix4f
    loadTranslate(BeMatrix4f lfh, BeVec4f trans) {
        BeMatrix4f ret= new BeMatrix4f(lfh);

        BeVec4f r0 = BeVec4f.loadAdd(lfh.vals[0], trans.getValue(0));
        BeVec4f r1 = BeVec4f.loadAdd(lfh.vals[1], trans.getValue(1));
        BeVec4f r2 = BeVec4f.loadAdd(lfh.vals[2], trans.getValue(2));

        r0.add(r1);
        r0.add(r2);
        r0.add(lfh.vals[3]);

        ret.vals[3] = r0;
        return ret;
    }

    public  BeMatrix4f
    translate(BeVec4f trans) {
        BeVec4f r0 = BeVec4f.loadScale(this.vals[0], trans.getValue(0));
        BeVec4f r1 = BeVec4f.loadScale(this.vals[1], trans.getValue(1));
        BeVec4f r2 = BeVec4f.loadScale(this.vals[2], trans.getValue(2));

        r0.add(r1);
        r0.add(r2);
        r0.add(this.vals[3]);

        this.vals[3] = r0;
        return this;
    }

    public static BeMatrix4f
    loadScale(BeMatrix4f lfh, BeVec4f scales){
        BeMatrix4f ret= new BeMatrix4f();

        ret.vals[0] = BeVec4f.loadScale(lfh.vals[0], scales.getValue(0));
        ret.vals[1] = BeVec4f.loadScale(lfh.vals[1], scales.getValue(1));
        ret.vals[2] = BeVec4f.loadScale(lfh.vals[2], scales.getValue(2));
        ret.vals[3] = new BeVec4f(lfh.vals[3]);

        return ret;
    }

    public static BeMatrix4f
    loadMultiply(BeMatrix4f lhs, BeMatrix4f rhs) {
        BeMatrix4f ret = new BeMatrix4f();

        BeVec4f srcA0 = lhs.vals[0];
        BeVec4f srcA1 = lhs.vals[1];
        BeVec4f srcA2 = lhs.vals[2];
        BeVec4f srcA3 = lhs.vals[3];

        BeVec4f srcB0 = rhs.vals[0];
        BeVec4f srcB1 = rhs.vals[1];
        BeVec4f srcB2 = rhs.vals[2];
        BeVec4f srcB3 = rhs.vals[3];

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB0.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB0.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB0.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB0.val[3]);
            v0.add(v1).add(v2).add(v3);
            ret.vals[0] = new BeVec4f(v0);
        }

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB1.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB1.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB1.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB1.val[3]);
            v0.add(v1).add(v2).add(v3);
            ret.vals[1] = new BeVec4f(v0);
        }

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB2.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB2.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB2.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB2.val[3]);
            v0.add(v1).add(v2).add(v3);
            ret.vals[2] = new BeVec4f(v0);
        }

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB3.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB3.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB3.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB3.val[3]);
            v0.add(v1).add(v2).add(v3);
            ret.vals[3] = new BeVec4f(v0);
        }
        return ret;
    }

    public BeMatrix4f
    multiply(BeMatrix4f rhs) {
        BeVec4f srcA0 = this.vals[0];
        BeVec4f srcA1 = this.vals[1];
        BeVec4f srcA2 = this.vals[2];
        BeVec4f srcA3 = this.vals[3];

        BeVec4f srcB0 = rhs.vals[0];
        BeVec4f srcB1 = rhs.vals[1];
        BeVec4f srcB2 = rhs.vals[2];
        BeVec4f srcB3 = rhs.vals[3];

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB0.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB0.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB0.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB0.val[3]);
            v0.add(v1).add(v2).add(v3);
            this.vals[0] = v0;
        }

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB1.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB1.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB1.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB1.val[3]);
            v0.add(v1).add(v2).add(v3);
            this.vals[1] = v0;
        }

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB2.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB2.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB2.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB2.val[3]);
            v0.add(v1).add(v2).add(v3);
            this.vals[2] = v0;
        }

        {
            BeVec4f v0 = BeVec4f.loadScale(srcA0, srcB3.val[0]);
            BeVec4f v1 = BeVec4f.loadScale(srcA1, srcB3.val[1]);
            BeVec4f v2 = BeVec4f.loadScale(srcA2, srcB3.val[2]);
            BeVec4f v3 = BeVec4f.loadScale(srcA3, srcB3.val[3]);
            v0.add(v1).add(v2).add(v3);
            this.vals[3] = v0;
        }
        return this;
    }

    public void decompose(BeVec3f pos, BeVec3f scale, BeQuaternion quat, BeVec3f skew) {
        if (pos != null) {
            pos.setValue(0, vals[3].getValue(0));
            pos.setValue(1, vals[3].getValue(1));
            pos.setValue(2, vals[3].getValue(2));
        }

        BeVec3f vx = new BeVec3f(vals[0]);
        BeVec3f vy = new BeVec3f(vals[1]);
        BeVec3f vz = new BeVec3f(vals[2]);

        BeVec3f c = BeVec3f.cross(vx, vy);
        boolean flip = BeVec3f.loadDot(c, vz) < 0;
        if (flip) {
            vx.scale(-1);
        }

        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        float yx = 0.0f;
        float xz = 0.0f;
        float yz = 0.0f;

        if (scale != null || quat != null || skew != null) {
            y = vy.magnitude();

            if (y > BeVec3f.epsilon())
            {
                vy.scale(1.f / y);
//                vy = vy / y;
            }

            yx = BeVec3f.loadDot(vy, vx);
            BeVec3f t = BeVec3f.loadScale(vy, yx);
            t.scale(-1);
            vx.add(t);

            x = vx.magnitude();

            if (x > BeVec3f.epsilon())
            {
                vx.scale(1.0f / x);
                yx = yx / x;
            }

            yz = BeVec3f.loadDot(vy, vz);
            t = BeVec3f.loadScale(vy, yz);
            t.scale(-1);
            vz.add(t);

            xz = BeVec3f.loadDot(vx, vz);
            t = BeVec3f.loadScale(vy, xz);
            t.scale(-1);
            vz.add(t);

            z = vz.magnitude();

            if (z > BeVec3f.epsilon())
            {
                vz.scale(1.0f / z);
//                vz = vz / z;
                yz = yz / z;
                xz = xz / z;
            }
        }

        if (scale != null)
        {
            scale.load(x, y, z);
            if (flip)
            {
                scale.val[0] *= -1;
            }
        }
        if (skew != null)
        {
            skew.load(yx, yz, xz);
        }

        if (quat != null)
        {
            BeMatrix4f tmp = new BeMatrix4f();

            tmp.set(0, vx);
            tmp.set(1, vy);
            tmp.set(2, vz);

            BeQuaternion q = BeMatrix4f.toQuat(tmp);
            quat.x = q.x;
            quat.y = q.y;
            quat.z = q.z;
            quat.w = q.w;
        }
    }

    public void decompose(BeVec3f pos, BeVec3f scale, BeQuaternion quat) {
        if (pos != null) {
            pos.setValue(0, vals[3].getValue(0));
            pos.setValue(1, vals[3].getValue(1));
            pos.setValue(2, vals[3].getValue(2));
        }

        BeVec3f vx = new BeVec3f(vals[0]);
        BeVec3f vy = new BeVec3f(vals[1]);
        BeVec3f vz = new BeVec3f(vals[2]);

        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        if (scale != null || quat != null) {
            x = vx.magnitude();
            y = vy.magnitude();
            z = vz.magnitude();
        }

        if (scale != null) {
            scale.val[0] = x;
            scale.val[1] = y;
            scale.val[2] = z;
        }
        if (quat != null) {
            float[] m = new float[]{x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1};
            BeMatrix4f scaleMat4 = new BeMatrix4f(m);
            BeMatrix4f scaleMat4Inv = new BeMatrix4f(m);
            BeMatrix4f.invertFull(scaleMat4, scaleMat4Inv);

            BeMatrix4f matrs = new BeMatrix4f(this);
            matrs.set(3, 0, 0);
            matrs.set(3, 1, 0);
            matrs.set(3, 2, 0);

            BeMatrix4f rotate = BeMatrix4f.loadMultiply(matrs, scaleMat4Inv);
            BeQuaternion r = toQuat(rotate);
            quat.x = r.x; quat.y = r.y; quat.z = r.z; quat.w = r.w;
        }
    }

    public void  setTRSS(BeVec3f pos, BeQuaternion q, BeVec3f s, BeVec3f skew) {
        BeQuaternion.toMatrix(q, this);

        this.vals[2].add(BeVec4f.loadScale(this.vals[0], skew.val[2]));
        this.vals[2].add(BeVec4f.loadScale(this.vals[1], skew.val[1]));
        this.vals[0].add(BeVec4f.loadScale(this.vals[2], skew.val[0]));

        this.vals[0].scale(new BeVec3f(s.val[0]));
        this.vals[1].scale(new BeVec3f(s.val[1]));
        this.vals[2].scale(new BeVec3f(s.val[2]));

        this.set(3, 0, pos.val[0]);
        this.set(3, 1, pos.val[1]);
        this.set(3, 2, pos.val[2]);
    }

    public void  setTRS(BeVec3f pos, BeQuaternion q, BeVec3f s) {
        BeQuaternion.toMatrix(q, this);

        this.vals[0].scale(new BeVec3f(s.val[0]));
        this.vals[1].scale(new BeVec3f(s.val[1]));
        this.vals[2].scale(new BeVec3f(s.val[2]));

        this.set(3, 0, pos.val[0]);
        this.set(3, 1, pos.val[1]);
        this.set(3, 2, pos.val[2]);
    }

    public BeVec4f get(int index) {
        assert (index >= 0 && index < 4);
        return vals[index];
    }

    public float get(int row, int col) {
        return vals[row].getValue(col);
    }

    public float invget(int row, int col) {
        return vals[col].getValue(row);
    }

    public void set(int index, BeVec4f val) {
        assert (index >= 0 && index < 4);
        vals[index] = new BeVec4f(val);
    }

    public void set(int index, BeVec3f val) {
        assert (index >= 0 && index < 4);
        vals[index] = new BeVec4f(val);
    }

    public void set(int r, int c, float val) {
        vals[r].val[c] = val;
    }

    public void invset(int r, int c, float val) {
        vals[c].val[r] = val;
    }


    public float[] toFloatArray() {
        float[] ret = new float[16];
        for (int i = 0; i < 16; i ++) {
            ret[i] = vals[i / 4].getValue(i % 4);
        }
        return ret;
    }

//    public static BeMatrix4f makePerspective(float fovy, float aspect, float zNear, float zFar)
//    {
//        BeMatrix4f result = new BeMatrix4f();
//        result.set(0,0,0);
//        result.set(1,1,0);
//        result.set(2,2,0);
//        result.set(3,3,0);
//
//        float tanHalfFovy = (float) Math.tan(fovy / 2.f);
//        result.set(0, 0, 1.f/(aspect * tanHalfFovy));
//        result.set(1, 1, 1.f/(tanHalfFovy));
//        result.set(2, 2, - (zFar + zNear) / (zFar - zNear));
//        result.set(2, 3, -1.0f);
//        result.set(3, 2, -(2.f * zFar * zNear) / (zFar - zNear));
//
//        return result;
//    }

    static float Deg2Rad(float deg)
    {
        // TODO : should be deg * kDeg2Rad, but can't be changed,
        // because it changes the order of operations and that affects a replay in some RegressionTests
        return deg / 360.0F * 2.0F * 3.14159265358979323846264338327950288419716939937510F;
    }
    public static BeMatrix4f makePerspective(float fovy, float aspect, float zNear, float zFar) {
        BeMatrix4f result = new BeMatrix4f();

        float cotangent, deltaZ;
        float radians = Deg2Rad(fovy / 2.0f);
        cotangent = (float) (Math.cos(radians) / Math.sin(radians));
        deltaZ = zNear - zFar;

        result.set(0, 0, cotangent / aspect);
        result.set(1, 0, 0.0f);
        result.set(2, 0,0.0f);
        result.set(3, 0,0.0f);
        result.set(0, 1,0.0f);
        result.set(1, 1,cotangent);
        result.set(2, 1,0.0f);
        result.set(3, 1,0.0f);
        result.set(0, 2,0.0f);
        result.set(1, 2,0.0f);
        result.set(2, 2,(zFar + zNear) / deltaZ);
        result.set(3, 2,2.0F * zNear * zFar / deltaZ);
        result.set(0, 3,0.0f);
        result.set(1, 3,0.0f);
        result.set(2, 3,-1.0f);
        result.set(3, 3, 0.0f);

        return result;
    }

    private static void swapArray(float[] a, float[]b, int size){
        for (int i = 0; i < size; i ++) {
            float t = a[i];
            a[i] = b[i];
            b[i] = t;
        }
    }
    public static boolean invertFull(BeMatrix4f m, BeMatrix4f out)
    {
        float[][] wtmp =new float[4][];
        for (int i = 0; i < 4; i++){
            wtmp[i] = new float[8];
        }
        float m0, m1, m2, m3, s;
        float[] r0, r1, r2, r3;

        r0 = wtmp[0];
        r1 = wtmp[1];
        r2 = wtmp[2];
        r3 = wtmp[3];

        r0[0] = m.invget( 0, 0);
        r0[1] = m.invget( 0, 1);
        r0[2] = m.invget( 0, 2);
        r0[3] = m.invget( 0, 3);
        r0[4] = 1.0f;
        r0[5] = r0[6] = r0[7] = 0.0f;

        r1[0] = m.invget( 1, 0);
        r1[1] = m.invget( 1, 1);
        r1[2] = m.invget( 1, 2);
        r1[3] = m.invget( 1, 3);
        r1[5] = 1.0f;
        r1[4] = r1[6] = r1[7] = 0.0f;

        r2[0] = m.invget( 2, 0);
        r2[1] = m.invget( 2, 1);
        r2[2] = m.invget( 2, 2);
        r2[3] = m.invget( 2, 3);
        r2[6] = 1.0f;
        r2[4] = r2[5] = r2[7] = 0.0f;

        r3[0] = m.invget( 3, 0);
        r3[1] = m.invget( 3, 1);
        r3[2] = m.invget( 3, 2);
        r3[3] = m.invget( 3, 3);
        r3[7] = 1.0f;
        r3[4] = r3[5] = r3[6] = 0.0f;

        /* choose pivot - or die */
        if (Math.abs(r3[0]) > Math.abs(r2[0])){
            swapArray(r3, r2, 8);
        }

        if (Math.abs(r2[0]) > Math.abs(r1[0]))
            swapArray(r2, r1, 8);
        if (Math.abs(r1[0]) > Math.abs(r0[0]))
            swapArray(r1, r0, 8);
        if (0.0F == r0[0]){
            out.loadIdentity();
            out.set(0, 0, 0);
            out.set(1, 1, 0);
            out.set(2, 2, 0);
            out.set(3, 3, 0);
            return  false;

        }


        /* eliminate first variable     */
        m1 = r1[0] / r0[0];
        m2 = r2[0] / r0[0];
        m3 = r3[0] / r0[0];
        s = r0[1];
        r1[1] -= m1 * s;
        r2[1] -= m2 * s;
        r3[1] -= m3 * s;
        s = r0[2];
        r1[2] -= m1 * s;
        r2[2] -= m2 * s;
        r3[2] -= m3 * s;
        s = r0[3];
        r1[3] -= m1 * s;
        r2[3] -= m2 * s;
        r3[3] -= m3 * s;
        s = r0[4];
        if (s != 0.0F)
        {
            r1[4] -= m1 * s;
            r2[4] -= m2 * s;
            r3[4] -= m3 * s;
        }
        s = r0[5];
        if (s != 0.0F)
        {
            r1[5] -= m1 * s;
            r2[5] -= m2 * s;
            r3[5] -= m3 * s;
        }
        s = r0[6];
        if (s != 0.0F)
        {
            r1[6] -= m1 * s;
            r2[6] -= m2 * s;
            r3[6] -= m3 * s;
        }
        s = r0[7];
        if (s != 0.0F)
        {
            r1[7] -= m1 * s;
            r2[7] -= m2 * s;
            r3[7] -= m3 * s;
        }

        /* choose pivot - or die */
        if (Math.abs(r3[1]) > Math.abs(r2[1]))
            swapArray(r3, r2, 8);
        if (Math.abs(r2[1]) > Math.abs(r1[1]))
            swapArray(r2, r1, 8);
        if (0.0F == r1[1]) {
            out.loadIdentity();
            out.set(0, 0, 0);
            out.set(1, 1, 0);
            out.set(2, 2, 0);
            out.set(3, 3, 0);
            return  false;
        }

        /* eliminate second variable */
        m2 = r2[1] / r1[1];
        m3 = r3[1] / r1[1];
        r2[2] -= m2 * r1[2];
        r3[2] -= m3 * r1[2];
        r2[3] -= m2 * r1[3];
        r3[3] -= m3 * r1[3];
        s = r1[4];
        if (0.0F != s)
        {
            r2[4] -= m2 * s;
            r3[4] -= m3 * s;
        }
        s = r1[5];
        if (0.0F != s)
        {
            r2[5] -= m2 * s;
            r3[5] -= m3 * s;
        }
        s = r1[6];
        if (0.0F != s)
        {
            r2[6] -= m2 * s;
            r3[6] -= m3 * s;
        }
        s = r1[7];
        if (0.0F != s)
        {
            r2[7] -= m2 * s;
            r3[7] -= m3 * s;
        }

        /* choose pivot - or die */
        if (Math.abs(r3[2]) > Math.abs(r2[2]))
            swapArray(r3, r2, 8);
        if (0.0F == r2[2]){
            out.loadIdentity();
            out.set(0, 0, 0);
            out.set(1, 1, 0);
            out.set(2, 2, 0);
            out.set(3, 3, 0);
            return  false;
        }

        /* eliminate third variable */
        m3 = r3[2] / r2[2];
        r3[3] -= m3 * r2[3];
        r3[4] -= m3 * r2[4];
        r3[5] -= m3 * r2[5];
        r3[6] -= m3 * r2[6];
        r3[7] -= m3 * r2[7];

        /* last check */
        if (0.0F == r3[3]) {
            out.loadIdentity();
            out.set(0, 0, 0);
            out.set(1, 1, 0);
            out.set(2, 2, 0);
            out.set(3, 3, 0);
            return  false;
        }

        s = 1.0F / r3[3]; /* now back substitute row 3 */
        r3[4] *= s;
        r3[5] *= s;
        r3[6] *= s;
        r3[7] *= s;

        m2 = r2[3]; /* now back substitute row 2 */
        s = 1.0F / r2[2];
        r2[4] = s * (r2[4] - r3[4] * m2);
        r2[5] = s * (r2[5] - r3[5] * m2);
        r2[6] = s * (r2[6] - r3[6] * m2);
        r2[7] = s * (r2[7] - r3[7] * m2);
        m1 = r1[3];
        r1[4] -= r3[4] * m1;
        r1[5] -= r3[5] * m1;
        r1[6] -= r3[6] * m1;
        r1[7] -= r3[7] * m1;
        m0 = r0[3];
        r0[4] -= r3[4] * m0;
        r0[5] -= r3[5] * m0;
        r0[6] -= r3[6] * m0;
        r0[7] -= r3[7] * m0;

        m1 = r1[2]; /* now back substitute row 1 */
        s = 1.0F / r1[1];
        r1[4] = s * (r1[4] - r2[4] * m1);
        r1[5] = s * (r1[5] - r2[5] * m1);
        r1[6] = s * (r1[6] - r2[6] * m1);
        r1[7] = s * (r1[7] - r2[7] * m1);
        m0 = r0[2];
        r0[4] -= r2[4] * m0;
        r0[5] -= r2[5] * m0;
        r0[6] -= r2[6] * m0;
        r0[7] -= r2[7] * m0;

        m0 = r0[1]; /* now back substitute row 0 */
        s = 1.0F / r0[0];
        r0[4] = s * (r0[4] - r1[4] * m0);
        r0[5] = s * (r0[5] - r1[5] * m0);
        r0[6] = s * (r0[6] - r1[6] * m0);
        r0[7] = s * (r0[7] - r1[7] * m0);

        out.invset(0, 0, r0[4]);
        out.invset(0, 1, r0[5]);
        out.invset(0, 2, r0[6]);
        out.invset(0, 3, r0[7]);

        out.invset(1, 0, r1[4]);
        out.invset(1, 1, r1[5]);
        out.invset(1, 2, r1[6]);
        out.invset(1, 3, r1[7]);

        out.invset(2, 0, r2[4]);
        out.invset(2, 1, r2[5]);
        out.invset(2, 2, r2[6]);
        out.invset(2, 3, r2[7]);

        out.invset(3, 0, r3[4]);
        out.invset(3, 1, r3[5]);
        out.invset(3, 2, r3[6]);
        out.invset(3, 3, r3[7]);

        return true;
    }

    public static BeQuaternion toQuat(BeMatrix4f kRot) {
        BeQuaternion q = new BeQuaternion();

        float fTrace = kRot.get(0, 0) + kRot.get(1, 1) + kRot.get(2, 2);
        float fRoot;

        if (fTrace > 0.0f)
        {
            // |w| > 1/2, may as well choose w > 1/2
            fRoot = (float) Math.sqrt(fTrace + 1.0f); // 2w
            q.w = 0.5f * fRoot;
            fRoot = 0.5f / fRoot; // 1/(4w)
            // {zh} TODO: 这里和effect是反的，所以需要交换 {en} TODO: Here and effect are inverse, so they need to be swapped
            q.x = (kRot.get(1, 2) - kRot.get(2, 1)) * fRoot;
            q.y = (kRot.get(2, 0) - kRot.get(0, 2)) * fRoot;
            q.z = (kRot.get(0, 1) - kRot.get(1, 0)) * fRoot;
        }
        else
        {
            // |w| <= 1/2
            int[] s_iNext = new int[] {1, 2, 0};
            int i = 0;
            if (kRot.get(1, 1) > kRot.get(0, 0))
                i = 1;
            if (kRot.get(2, 2) > kRot.get(i, i))
                i = 2;
            int j = s_iNext[i];
            int k = s_iNext[j];

            fRoot = (float) Math.sqrt(kRot.get(i, i) - kRot.get(j, j) - kRot.get(k, k) + 1.0f);
//            float* apkQuat[3] = {&q.x, &q.y, &q.z};
            float[] apkQuat = new float[3];
            assert (!(fRoot < BeVec3f.epsilon()));
            apkQuat[i] = 0.5f * fRoot;
            fRoot = 0.5f / fRoot;
            // {zh} TODO: 这里和effect是反的，所以需要交换 {en} TODO: Here and effect are inverse, so they need to be swapped
            q.w = (kRot.get(j, k) - kRot.get(k, j)) * fRoot;
            apkQuat[j] = (kRot.get(i, j) + kRot.get(j, i)) * fRoot;
            apkQuat[k] = (kRot.get(i, k) + kRot.get(k, i)) * fRoot;
            q.x = apkQuat[0];
            q.y = apkQuat[1];
            q.z = apkQuat[2];
        }
        q.normalize();
        return q;
    }
    @Override
    public String toString() {
        return "BeMatrix4f{" +
                "vals=" + Arrays.toString(vals) +
                '}';
    }
}