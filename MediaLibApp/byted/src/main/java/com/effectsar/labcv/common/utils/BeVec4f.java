package com.effectsar.labcv.common.utils;

import java.util.Arrays;

public class BeVec4f {
    public float[] val;

    public BeVec4f() {
        val = new float[4];
    }

    public BeVec4f(float value) {
        val = new float[4];
        val[0] = val[1] = val[2] = val[3] = value;
    }

    public BeVec4f(float x, float y, float z, float w) {
        val = new float[4];
        val[0] = x;
        val[1] = y;
        val[2] = z;
        val[3] = w;
    }



    public BeVec4f(BeVec4f other) {
        val = new float[4];
        val[0] = other.val[0];
        val[1] = other.val[1];
        val[2] = other.val[2];
        val[3] = other.val[3];
    }

    public BeVec4f(BeVec3f other) {
        val = new float[4];
        val[0] = other.val[0];
        val[1] = other.val[1];
        val[2] = other.val[2];
        val[3] = 0.f;
    }

    public void scale(float value) {
        val[0] *= value;
        val[1] *= value;
        val[2] *= value;
        val[3] *= value;
    }

    public void scale(BeVec3f value) {
        val[0] *= value.val[0];
        val[1] *= value.val[1];
        val[2] *= value.val[2];
    }

    public static BeVec4f loadScale(BeVec4f lhs, float value) {
        BeVec4f ret = new BeVec4f();

        ret.val[0] = lhs.val[0] * value;
        ret.val[1] = lhs.val[1] * value;
        ret.val[2] = lhs.val[2] * value;
        ret.val[3] = lhs.val[3] * value;
        return ret;
    }

    public static BeVec4f loadMultiply(BeVec4f lhs, BeVec4f rhs) {
        BeVec4f ret = new BeVec4f();

        ret.val[0] = lhs.val[0] * rhs.val[0];
        ret.val[1] = lhs.val[1] * rhs.val[1];
        ret.val[2] = lhs.val[2] * rhs.val[2];
        ret.val[3] = lhs.val[3] * rhs.val[3];
        return ret;
    }

    public void add(float value) {
        val[0] += value;
        val[1] += value;
        val[2] += value;
        val[3] += value;
    }

    public static BeVec4f loadAdd(BeVec4f lfh, float value) {
        BeVec4f ret = new BeVec4f();

        ret.val[0] = lfh.val[0] + value;
        ret.val[1] = lfh.val[1] + value;
        ret.val[2] = lfh.val[2] + value;
        ret.val[3] = lfh.val[3] + value;

        return ret;
    }

    public static BeVec4f loadAdd(BeVec4f lhs, BeVec4f rhs) {
        BeVec4f ret = new BeVec4f();

        ret.val[0] = lhs.val[0] + rhs.val[0];
        ret.val[1] = lhs.val[1] + rhs.val[1];
        ret.val[2] = lhs.val[2] + rhs.val[2];
        ret.val[3] = lhs.val[3] + rhs.val[3];

        return ret;
    }

    public BeVec4f add(BeVec4f value) {
        val[0] += value.val[0];
        val[1] += value.val[1];
        val[2] += value.val[2];
        val[3] += value.val[3];
        return this;
    }

    public BeVec4f add(BeVec3f value) {
        val[0] += value.val[0];
        val[1] += value.val[1];
        val[2] += value.val[2];
        return this;
    }

    public float  getValue(int index) {
        assert (index >=0 && index <4);
        return val[index];
    }

    public void setValue(int index, float value) {
        val[index] = value;
    }

    @Override
    public String toString() {
        return "BeVec4f{" +
                "val=" + Arrays.toString(val) +
                '}';
    }
}