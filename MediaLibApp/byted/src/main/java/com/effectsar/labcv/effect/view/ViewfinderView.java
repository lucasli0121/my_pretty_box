/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.effectsar.labcv.effect.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;


import com.effectsar.labcv.R;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class ViewfinderView extends View {

    /** {zh} 
     * 刷新界面的时间
     */
    /** {en} 
     * Time to refresh the interface
     */
    private static final long ANIMATION_DELAY = 2;
    private static final int OPAQUE = 0xFF;
    /** {zh} 
     * 中间那条线每次刷新移动的距离
     */
    /** {en} 
     * The distance that the middle line moves each time it refreshes
     */

    private static final int SPEEN_DISTANCE = 10;
    private static final int MAX_RESULT_POINTS = 20;
    private static final String TAG = ViewfinderView.class.getSimpleName();
    /** {zh} 
     * 扫描框中的中间线的宽度
     */
    /** {en} 
     * Width of the middle line in the scan box
     */
    private static int MIDDLE_LINE_WIDTH;
    /** {zh} 
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    /** {en} 
     * The gap between the middle line in the scanning frame and the left and right sides of the scanning frame
     */

    private static int MIDDLE_LINE_PADDING;
    /** {zh} 
     * 遮掩层的颜色
     */
    /** {en} 
     * The color of the mask
     */

    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private final int CORNER_PADDING;
    /** {zh} 
     * 画笔对象的引用
     */
    /** {en} 
     * References to brush objects
     */

    private final Paint paint;
    /** {zh} 
     * 第一次绘制控件
     */
    /** {en} 
     * Draw control for the first time
     */

    private boolean isFirst = true;
    /** {zh} 
     * 中间滑动线的最顶端位置
     */
    /** {en} 
     * The top position of the middle sliding line
     */

    private int slideTop;
    /** {zh} 
     * 中间滑动线的最底端位置
     */
    /** {en} 
     * The bottom position of the middle sliding line
     */

    private int slideBottom;
    private Bitmap resultBitmap;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private boolean hasNotified = false;
    private Rect screenRect;



    public OnDrawFinishListener getListener() {
        return listener;
    }

    public void setOnDrawFinishListener(OnDrawFinishListener listener) {
        this.listener = listener;
    }

    private OnDrawFinishListener listener;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        CORNER_PADDING = dip2px(context, -1.0F);
        MIDDLE_LINE_PADDING = dip2px(context, 20.0F);
        MIDDLE_LINE_WIDTH = dip2px(context, 3.0F);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG); //    {zh} 开启反锯齿    {en} Turn on anti-aliasing

        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask); //    {zh} 遮掩层颜色    {en} Masking layer color
        resultColor = resources.getColor(R.color.result_view);

        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
    }




    @Override
    public void onDraw(Canvas canvas) {
        if (screenRect == null) {
            return;
        }
        //    {zh} 绘制遮掩层    {en} Draw mask layer
        drawCover(canvas, screenRect);

        if (resultBitmap != null) { //    {zh} 绘制扫描结果的图    {en} Draw a map of the scan results
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(0xA0);
            canvas.drawBitmap(resultBitmap, null, screenRect, paint);
        } else {

            //    {zh} 画扫描框边上的角    {en} Draw the corner on the edge of the scanning frame
            drawRectEdges(canvas, screenRect);

            //    {zh} 绘制扫描线    {en} Draw the scan line
            drawScanningLine(canvas, screenRect);

            List<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(screenRect.left + point.getX(), screenRect.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(screenRect.left + point.getX(), screenRect.top
                            + point.getY(), 3.0f, paint);
                }
            }

            //    {zh} 只刷新扫描框的内容，其他地方不刷新    {en} Only refresh the contents of the scan box, not elsewhere
            postInvalidateDelayed(ANIMATION_DELAY, screenRect.left, screenRect.top,
                    screenRect.right, screenRect.bottom);
            if (listener != null && !hasNotified) {
                listener.onDrawFinish(screenRect);
                hasNotified = true;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int top = h / 3;
        int left = (w - top) / 2;
        screenRect = new Rect(left, top, w - left, h - top);
    }

    /** {zh} 
     * 绘制扫描线
     *
     * @param frame 扫描框
     */
    /** {en} 
     * Draw scan line
     *
     * @param frame  scan frame
     */

    private void drawScanningLine(Canvas canvas, Rect frame) {

        //    {zh} 初始化中间线滑动的最上边和最下边    {en} Initialize the top and bottom edges of the middle line slide
        if (isFirst) {
            isFirst = false;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }

        //    {zh} 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE    {en} Draw the middle line, and every time you refresh the interface, the middle line moves down SPEEN_DISTANCE
        slideTop += SPEEN_DISTANCE;
        if (slideTop >= slideBottom) {
            slideTop = frame.top;
        }

        canvas.save();
        canvas.clipRect(new Rect(frame.left, frame.top, frame.right, slideTop));
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_scan_mask), null, frame, paint);
        canvas.restore();
    }

    /** {zh} 
     * 绘制遮掩层
     */
    /** {en} 
     * Draw mask layer
     */

    private void drawCover(Canvas canvas, Rect frame) {

        //    {zh} 获取屏幕的宽和高    {en} Get the width and height of the screen
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);

        canvas.save();
        canvas.clipRect(frame, Region.Op.DIFFERENCE);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.restore();
    }

    /** {zh} 
     * 描绘方形的四个角
     *
     * @param canvas
     * @param frame
     */
    /** {en} 
     * Draw the four corners of the square
     *
     * @param canvas
     * @param frame
     */

    private void drawRectEdges(Canvas canvas, Rect frame) {

        paint.setColor(Color.WHITE);
        paint.setAlpha(OPAQUE);

        Resources resources = getResources();
        /** {zh} 
         * 这些资源可以用缓存进行管理，不需要每次刷新都新建
         */
        /** {en} 
         * These resources can be managed with a cache and do not need to be created every time you refresh
         */

        Bitmap bitmapCornerTopleft = BitmapFactory.decodeResource(resources,
                R.drawable.ic_rect_left_top);
        Bitmap bitmapCornerTopright = BitmapFactory.decodeResource(resources,
                R.drawable.ic_rect_right_top);
        Bitmap bitmapCornerBottomLeft = BitmapFactory.decodeResource(resources,
                R.drawable.ic_rect_left_bottom);
        Bitmap bitmapCornerBottomRight = BitmapFactory.decodeResource(
                resources, R.drawable.ic_rect_right_bottom);

        canvas.drawBitmap(bitmapCornerTopleft, frame.left + CORNER_PADDING,
                frame.top + CORNER_PADDING, paint);
        canvas.drawBitmap(bitmapCornerTopright, frame.right - CORNER_PADDING
                        - bitmapCornerTopright.getWidth(), frame.top + CORNER_PADDING,
                paint);
        canvas.drawBitmap(bitmapCornerBottomLeft, frame.left + CORNER_PADDING,
                frame.bottom - CORNER_PADDING - bitmapCornerBottomLeft
                        .getHeight(), paint);
        canvas.drawBitmap(bitmapCornerBottomRight, frame.right - CORNER_PADDING
                - bitmapCornerBottomRight.getWidth(), frame.bottom
                - CORNER_PADDING - bitmapCornerBottomRight.getHeight(), paint);

        bitmapCornerTopleft.recycle();
        bitmapCornerTopright.recycle();
        bitmapCornerBottomLeft.recycle();
        bitmapCornerBottomRight.recycle();

    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

    public Rect getScreenRect() {
        return screenRect;
    }

    /** {zh} 
     * dp转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    /** {en} 
     * DP to px
     *
     * @param context
     * @param dipValue
     * @return
     */

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public interface OnDrawFinishListener {
        void onDrawFinish(Rect frame);
    }
}
