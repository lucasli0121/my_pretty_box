package com.effectsar.labcv.effect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;

import com.effectsar.labcv.common.utils.DensityUtils;
import com.effectsar.labcv.core.util.LogUtils;

public class RemoteResourceUndownloadView extends View {

    private Context mContext;

    //  {zh} 进度的取值范围：0～1  {en} The value range of the progress: 0~ 1
    public float progress=0;
    private final Paint mPaint = new Paint();

    // {zh} 设置View默认的大小 {en} Set the default size of the View
    private final float mDefaultWidth = DensityUtils.dp2px(mContext,10);
    private final float mDefaultPadding = DensityUtils.dp2px(mContext,0);
    //  {zh} 定义设置进度圆的默认半径  {en} Define the default radius for setting the progress circle
    private float mRadius = mDefaultWidth/2;
    // {zh} 测量后的实际view的大小 {en} The size of the actual view after measurement
    private float mMeasureWidth;
    private float mMeasureHeight;
    private RectF mRectF;

    // {zh} 圆环的默认宽度 {en} Default width of the ring
    private final float mProgressBarHeight = DensityUtils.dp2px(mContext,5f);
    private final float mProgressBarContentHeight = DensityUtils.dp2px(mContext,3.2f);

    // {zh} 设置未加载进度的默认颜色 {en} Set default color for unloaded progress
    private final int mUnReachedBarColor = 0xffc4c4c4;
    // {zh} 设置已加载进度的默认颜色 {en} Set the default color for loaded progress
    private final int mReachedBarColor = 0xffffffff;

    public RemoteResourceUndownloadView(Context context) {
        super(context);
        mContext = context;
        // {zh} 初始化画笔风格，图形参数，如圆圈的颜色，绘制的文字等 {en} Initialization brush style, graphic parameters such as circle color, drawn text, etc
        initView();
    }

    private void initView() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtils.d("progress: "+w + ", h: " +h+", oldw: "+oldw+", oldh: "+ oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // {zh} 测量计算 {en} Measurement calculation
        mMeasureWidth = measureSize(widthMeasureSpec);
        mMeasureHeight = measureSize(heightMeasureSpec);
        // {zh} 重新测量 {en} Re-measure
        setMeasuredDimension((int)mMeasureWidth, (int)mMeasureHeight);
        // {zh} 绘制圆环的半径 {en} Draw the radius of the ring
        mRadius=(mMeasureWidth -mDefaultPadding*2-mProgressBarHeight-getPaddingLeft()-getPaddingRight())/2;
        // {zh} 绘制进度圆弧的外切矩形定义 {en} Definition of a tangent rectangle for drawing a progress arc
        mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);

    }
    private float measureSize(int measureSpec) {
        float result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            // {zh} 当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小 {en} When specMode = EXACTLY, exact value mode, that is, when we specify a specific size for View in the layout file
            result = specSize;
        }else {
            result = mDefaultWidth;   // {zh} 指定默认大小 {en} Specify default size
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();

        // {zh} 将画布移动到中心 view中心为原点(0,0) {en} Move the canvas to the center view center as the origin (0,0)
        canvas.translate(mMeasureWidth/2,mMeasureHeight/2);
        mPaint.setStyle(Paint.Style.STROKE);
        // {zh} 绘制未加载的进度，也就是绘制环背景 {en} Draw unloaded progress, i.e. draw the ring background
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setStrokeWidth(mProgressBarHeight);
        // {zh} 点(0,0)为原心 {en} Point (0, 0) is the original center
        canvas.drawCircle(0, 0, mRadius, mPaint);

        // {zh} 绘制已加载的圆环进度 {en} Draw Loaded Ring Progress
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mProgressBarContentHeight);
        // {zh} 计算进度圆弧角度 {en} Calculate progress arc angle
        float sweepAngle = progress * 360;
        // {zh} 绘制圆弧进度 {en} Draw arc progress
        canvas.drawArc(mRectF, 0,
                sweepAngle, false, mPaint);
        // {zh} 绘制显示进行的颜色 {en} Draw the color that shows the progress
        mPaint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }


    // {zh} 将设置的db转为屏幕像素 {en} Convert the set db to screen pixels
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    // {zh} 更新进度 {en} Update progress
    public void setProgress(int number) {
        if (number>100){
            number=100;
        }
        if (number<0){
            number=0;
        }
        progress=number;
        invalidate();
    }

}
