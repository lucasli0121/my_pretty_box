package com.effectsar.labcv.common.utils;

import java.util.Arrays;

public class BeVec3f {
    public float[] val;

    public BeVec3f() {
        val = new float[3];
    }

    public BeVec3f(float value) {
        val = new float[3];
        val[0] = val[1] = val[2] = value;
    }

    public BeVec3f(float x, float y, float z) {
        val = new float[3];
        val[0] = x;
        val[1] = y;
        val[2] = z;
    }


    public BeVec3f(BeVec4f other) {
        val = new float[3];
        val[0] = other.val[0];
        val[1] = other.val[1];
        val[2] = other.val[2];
    }

    public BeVec3f(BeVec3f other) {
        val = new float[3];
        val[0] = other.val[0];
        val[1] = other.val[1];
        val[2] = other.val[2];
    }

    public void scale(float value) {
        val[0] *= value;
        val[1] *= value;
        val[2] *= value;
    }

    public void load(float x, float y, float z) {
        val[0] = x;
        val[1] = y;
        val[2] = z;
    }

    public static BeVec3f loadScale(BeVec3f lhs, float value) {
        BeVec3f ret = new BeVec3f();

        ret.val[0] = lhs.val[0] * value;
        ret.val[1] = lhs.val[1] * value;
        ret.val[2] = lhs.val[2] * value;
        return ret;
    }

    public static BeVec3f loadMultiply(BeVec3f lhs, BeVec3f rhs) {
        BeVec3f ret = new BeVec3f();

        ret.val[0] = lhs.val[0] * rhs.val[0];
        ret.val[1] = lhs.val[1] * rhs.val[1];
        ret.val[2] = lhs.val[2] * rhs.val[2];
        return ret;
    }

    public void add(float value) {
        val[0] += value;
        val[1] += value;
        val[2] += value;
    }

    public void sub(BeVec3f value) {
        val[0] -= value.val[0];
        val[1] -= value.val[1];
        val[2] -= value.val[2];
    }

    public static BeVec3f loadAdd(BeVec3f lfh, float value) {
        BeVec3f ret = new BeVec3f();

        ret.val[0] = lfh.val[0] + value;
        ret.val[1] = lfh.val[1] + value;
        ret.val[2] = lfh.val[2] + value;

        return ret;
    }

    public static BeVec3f loadAdd(BeVec3f lhs, BeVec3f rhs) {
        BeVec3f ret = new BeVec3f();

        ret.val[0] = lhs.val[0] + rhs.val[0];
        ret.val[1] = lhs.val[1] + rhs.val[1];
        ret.val[2] = lhs.val[2] + rhs.val[2];

        return ret;
    }

    public BeVec3f add(BeVec3f value) {
        val[0] += value.val[0];
        val[1] += value.val[1];
        val[2] += value.val[2];
        return this;
    }

    public float  getValue(int index) {
        assert (index >=0 && index <3);
        return val[index];
    }

    public float magnitude() {
        return (float) Math.sqrt(dot(this));
    }
    public void setValue(int index, float value) {
        val[index] = value;
    }

    public float dot(BeVec3f other) {
        return this.val[0] * other.val[0] + this.val[1] * other.val[1] + this.val[2] * other.val[2];
    }

    public static float loadDot(BeVec3f left,  BeVec3f right) {
        return left.val[0] * right.val[0] + left.val[1] * right.val[1] + left.val[2] * right.val[2];
    }

    public static BeVec3f cross(BeVec3f left,  BeVec3f right) {
        return new BeVec3f(left.val[1] * right.val[2] - left.val[2] * right.val[1],
                left.val[2] * right.val[0] - left.val[0] * right.val[2],
                left.val[0] * right.val[1] - left.val[1] * right.val[0]);
    }

    public static float epsilon(){
        return 0.00001F;
    }
    @Override
    public String toString() {
        return "BeVec3f{" +
                "val=" + Arrays.toString(val) +
                '}';
    }
}