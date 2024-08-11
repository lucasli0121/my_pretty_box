package com.media.demo

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.TOOL_TYPE_STYLUS
import android.view.View
import android.widget.HorizontalScrollView
import com.media.demo.util.LineUtils
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class PointEx(x: Float, y: Float, z: Float) {
    var x: Float = x
    var y: Float = y
    var r: Float = z
    var ns: Long = 0
    init {
        ns = SystemClock.elapsedRealtimeNanos()
    }
    fun draw(view: View, canvas: Canvas?, p: Paint) {
        canvas ?.drawCircle(x, y, r, p)
        view.postInvalidate()
    }
    fun calcTmDiff(lastPt: PointEx?): Pair<Long, Long> {
        var diffs = ns - (lastPt?.ns ?: 0)
        var dms = diffs / 1000000
        var dns = diffs % 1000000
        return Pair(dms,dns)
    }
}

/*
    实现自定义的HorizontalScrollView类，用来防止onTouchEvent事件被抢占
 */
class HScrollView(context: Context?, attrs: AttributeSet?) : HorizontalScrollView(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var type = ev?.getToolType(0)
        if(type == TOOL_TYPE_STYLUS) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var type = ev?.getToolType(0)
        if(type == TOOL_TYPE_STYLUS) {
            return false
        }
        return super.onTouchEvent(ev)
    }
}

/*
    提供用笔书写的视图组件，通过onTouchEvent事件里面的参数，实现坐标以及压感的书写
 */
class WriteView(context: Context?, attrs: AttributeSet?) : DrawBaseView(context, attrs) {
    private val Tag : String = "WriteView"
    // Define a enum class about TouchEvent status
    enum class TouchStatus(i: Int) {
        NONE(0),
        DOWN(1),
        DOWN_MOVE(2),
        UP(3),
        UP_MOVE(4)
    }
    lateinit var drawBitmapListener: OnDrawBitmapListener
    //表示触摸的状态
    private var touchStatus = TouchStatus.NONE
    lateinit var touchCanvas: Canvas
    private var ptLst = ArrayList<PointEx>()
    private var pathList = ArrayList<ArrayList<PointEx>>()
    private var bmLst = ArrayList<Bitmap>()
    var thick = 10.0f

    fun getPathList(): ArrayList<ArrayList<PointEx>> {
        return pathList
    }

    override fun getBitmap() : Bitmap? {
        drawBitmap()
        return bmBuf
    }

    fun redo2() {
        if(pathList.size > 0) {
            var paint = Paint()
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.flags = Paint.ANTI_ALIAS_FLAG
            //橡皮擦的功能
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            var lst = pathList.last()
            for(i in 0 until lst.size) {
                var pt1 = lst[i]
                pt1.draw(this, bmCanvas!!, paint)
                if(i + 1 < lst.size - 1) {
                    var pt2 = lst[i+1]
                    writePoints(pt1, pt2, paint)
                } else if(i+1 == lst.size - 1){
                    var pt2 = lst[i+1]
                    writeEndPoints(pt1, pt2, paint)
                }
            }
            pathList.remove(pathList.last())
            paint.xfermode = null
        }
    }
    fun redo() {
        if(bmLst.size > 0) {
            synchronized(bmLst) {
                var bm = bmLst.last()
                bm.recycle()
                bmLst.remove(bm)
            }
        }
        if(pathList.size > 0) {
            pathList.remove(pathList.last())
        }
        postInvalidate()
    }
    fun clearAll() {
        pathList.clear()
        ptLst.clear()
        bmBuf?.recycle()
        bmBuf = null
        bmCanvas = null
        bmLst.forEach { it.recycle() }
        bmLst.clear()
        invalidate()
    }

    private fun drawBitmap() {
        if(bmBuf != null) {
            bmBuf?.recycle()
            bmBuf = null
        }
        if(width > 0 && height > 0 && bmLst.size > 0) {
            bmBuf = initBitmapBuf()
            val paint = Paint()
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.flags = Paint.ANTI_ALIAS_FLAG
            paint.color = resources.getColor(R.color.black, null)
            synchronized(bmLst) {
                bmLst.forEach { bmCanvas?.drawBitmap(it, 0f, 0f, paint) }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val z = event.pressure
        var type = event.getToolType(0)
        Log.i(Tag, "onTouchEvent, x=${x}, y=${y}, z=${z}, type=${type},ns=${SystemClock.elapsedRealtimeNanos()}")
        choice = true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(type == TOOL_TYPE_STYLUS) {
                    touchStatus = TouchStatus.DOWN
                    drawWrite(x, y, z)
                }
            }
            MotionEvent.ACTION_UP -> {
                if(type == TOOL_TYPE_STYLUS) {
                    touchStatus = TouchStatus.UP
                    endDraw(x, y, z)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                touchStatus = TouchStatus.NONE
            }
            MotionEvent.ACTION_MOVE -> {
                if(touchStatus == TouchStatus.DOWN) {
                    touchStatus = TouchStatus.DOWN_MOVE
                } else if(touchStatus == TouchStatus.UP) {
                    touchStatus = TouchStatus.UP_MOVE
                }
                if(touchStatus == TouchStatus.DOWN_MOVE && type == TOOL_TYPE_STYLUS) {
                    drawWrite(x, y, z)
                }
            }
        }
        return true //super.onTouchEvent(event)
    }

    /*
        响应onTouchEvent事件, 带笔的压感，画一个点
     */
    private fun drawWrite(x: Float, y: Float, z: Float) {
        var curpt = PointEx(x, y, z * thick)
        val paint = Paint()
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.color = resources.getColor(R.color.black, null)
        if(touchStatus == TouchStatus.DOWN) {
            var bm = initBitmapBuf(false)
            touchCanvas = Canvas(bm!!)
            synchronized(bmLst) {
                bmLst.add(bm)
            }
        }
        if (ptLst.isEmpty()) {
            curpt.draw(this, touchCanvas, paint)
        } else {
            var lastpt = ptLst[ptLst.size - 1]
            writePoints(lastpt, curpt, paint)
        }
        ptLst.add(curpt)
    }

    /*
        根据算法计算屏幕上两个点的画法，并在convas上画
     */
    private fun writePoints(pt1: PointEx, pt2: PointEx, p: Paint)  {
        var dis = sqrt((pt2.x - pt1.x).pow(2.toFloat()) + (pt2.y - pt1.y).pow(2.toFloat()))
        if ( dis < 1 ) {
            pt2.draw(this, touchCanvas, p)
        } else {
            var dr = pt2.r - pt1.r
            var sr = dr / dis
            var r = pt1.r
            for( d1 in 0 until (dis).toInt()) {
                var inPt = LineUtils().calPointInLine(pt1, pt2, d1.toFloat())
                var pt = PointEx(inPt.first, inPt.second, r)
                pt.draw(this, touchCanvas, p)
                r += sr
            }
        }
    }
    /*
        响应onTouchEvent的 up事件，画结束的点
     */
    private fun endDraw(x: Float, y: Float, z: Float) {
        var pt = PointEx(x, y, z*thick)
        val paint = Paint()
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.color = resources.getColor(R.color.black, null)
        if(ptLst.size > 1) {
            var lastpt = ptLst[ptLst.size - 1]
            writeEndPoints(lastpt, pt, paint)
        }
        ptLst.add(pt)
        pathList.add(ptLst.clone() as ArrayList<PointEx>)
        ptLst.clear()
    }
    /*
        根据结束算法，画两个点，能画出结束点的延长线
     */
    private fun writeEndPoints(pt1: PointEx, pt2: PointEx, p: Paint) {
        var d = sqrt((pt2.x - pt1.x).pow(2.toFloat()) + (pt2.y - pt1.y).pow(2.toFloat()))
        while (d < pt2.r || d <= 0) {
            d += 1
        }
        var extpt = LineUtils().calExtendPointInLine(pt1, pt2, d)
        var endpt = PointEx(extpt.first, extpt.second, 0.0f)
        var dr = endpt.r - pt1.r
        var sr = dr / d
        var r = pt1.r
        for( d1 in 0..round(d).toInt()) {
            var inPt = LineUtils().calPointInLine(pt1, endpt, d1.toFloat())
            var pt = PointEx(inPt.first, inPt.second, r)
            pt.draw(this, touchCanvas, p)
            r += sr
        }
    }

    /*
        重载函数
        被父类DrawBaseView调用
        参数 canvas不是bmCanvas而是父类的surfaceView的canvas
     */
    override fun doDraw(canvas: Canvas?) {
        drawGrid(canvas)
        drawBitmap()
        //调用父类doDraw
        super.doDraw(canvas)
    }
    interface OnDrawBitmapListener {
        fun onDrawBitmap(bm: Bitmap)
    }
}