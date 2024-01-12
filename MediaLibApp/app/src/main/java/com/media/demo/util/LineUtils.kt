package com.media.demo.util

import com.media.demo.PointEx
import kotlin.math.pow
import kotlin.math.sqrt

class LineUtils {
    //计算两点的延长线上一点
    fun calExtendPointInLine(pt1: PointEx, pt2: PointEx, d: Float) : Pair<Float, Float> {
        var x1 = pt1.x
        var y1 = pt1.y
        var x2 = pt2.x
        var y2 = pt2.y
        var x = x2
        var y = y2
        if (x1 == x2) {
            x = x2
            y = if(y2 > y1) y2 + d else y2 - d
        } else {
            var k = (y2 - y1) / (x2 - x1)
            var c = d.pow(2.toFloat())
            var m = sqrt(c / (1 + k.pow(2.toFloat())))
            if(x2 < x1) {
                m *= -1
            }
            x = x2 + m
            y = y2 + k*m
        }
        return x to y
    }
    fun calPointInLine(pt1: PointEx, pt2: PointEx, d: Float) : Pair<Float, Float> {
        var x1 = pt1.x
        var y1 = pt1.y
        var x2 = pt2.x
        var y2 = pt2.y
        var x = x2
        var y = y2
        if (x1 == x2) {
            x = x1
            y = if(y2 > y1) y1 + d else y1 - d
        } else {
            var k = (y2 - y1) / (x2 - x1)
            var c = d.pow(2.toFloat())
            var m = sqrt(c / (1 + k.pow(2.toFloat())))
            if(x2 < x1) {
                m *= -1
            }
            x = x1 + m
            y = y1 + k*m
        }
        return x to y
    }
}