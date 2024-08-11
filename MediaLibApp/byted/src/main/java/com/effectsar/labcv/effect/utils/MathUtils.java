package com.effectsar.labcv.effect.utils;

public class MathUtils {

    public static boolean floatEqual(float a, float b){
        return Math.abs(a - b) < 0.01;
    }
}
