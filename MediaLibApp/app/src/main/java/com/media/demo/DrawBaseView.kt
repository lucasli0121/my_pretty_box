package com.media.demo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

open class DrawBaseView(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback  {
    protected var bmCanvas : Canvas? = null
    protected var bmBuf : Bitmap? = null
    var surfaceDestroy: Boolean = false
    var surfaceCreate: Boolean = false
    var onChoiceListener: OnChoiceListener? = null
    var choice = false
        set(c) {
            if(!field && c) {
                onChoiceListener?.onChoice(this)
            }
            if(c != field) {
                field = c
                postInvalidate()
            }
        }

    init {
        //下面两句设置surfaceView背景为透明
//        setZOrderOnTop(true)
//        holder.setFormat(PixelFormat.TRANSLUCENT)
        holder.addCallback(this)
        isFocusableInTouchMode = true
        keepScreenOn = true
    }

    interface OnChoiceListener {
        fun onChoice(v: DrawBaseView)
    }

    open fun initBitmapBuf(initCanvas: Boolean=true) : Bitmap? {
        var bm: Bitmap? = null
        if(width > 0 && height > 0) {
            bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            if(bm != null) {
                bm?.setHasAlpha(true)
                if(initCanvas) {
                    bmCanvas = Canvas(bm!!)
                }
            }
        }
        return bm
    }

    open fun getBitmap() : Bitmap? {
        return bmBuf
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceCreate = true
        surfaceDestroy = false
        bmBuf = initBitmapBuf()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceDestroy = true
        surfaceCreate = false
    }
    override fun postInvalidate() {
        threadDraw()
    }

    override fun invalidate() {
        makeDraw()
    }

    private fun threadDraw() = Thread {
        if (surfaceDestroy) {
            return@Thread
        }
        makeDraw()
    }.start()

    /*
        锁定surface holder并调用doDraw，向surface canvas上画bitmap
     */
    private fun makeDraw() {
        if(holder != null && surfaceCreate) {
            synchronized(holder) {
                var canvas = holder.lockCanvas()
                val paint = Paint()
                paint.style = Paint.Style.FILL
                paint.color = resources.getColor(R.color.white, null)
                canvas?.drawRect(Rect(0, 0, width, height), paint)
                doDraw(canvas)
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
    /*
        可重载
        向canvas上画bitmap对象
     */
    open fun doDraw(canvas: Canvas?) {
        if(bmBuf != null) {
            canvas?.drawBitmap(bmBuf!!, 0F, 0F, null)
        }
    }

    /*
        在画布上画米子格，外框为黑色，里面米子线为灰色
     */
    open fun drawGrid(canvas: Canvas?) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.flags = Paint.ANTI_ALIAS_FLAG
        if(choice) {
            paint.color = resources.getColor(R.color.red, null)
        } else {
            paint.color = resources.getColor(R.color.black, null)
        }
        canvas?.drawRect(Rect(0, 0, width, height), paint)
        paint.pathEffect = DashPathEffect(floatArrayOf(5f,5f,5f,5f), 1f)
        if(choice) {
            paint.color = resources.getColor(R.color.red, null)
        } else {
            paint.color = resources.getColor(android.R.color.darker_gray, null)
        }
        canvas?.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        canvas?.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)
    }
}