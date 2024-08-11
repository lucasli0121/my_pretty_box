package com.effectsar.labcv.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Size;


import com.effectsar.labcv.core.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static android.os.Environment.DIRECTORY_DCIM;

public class BitmapUtils {
    public enum  BEImageDirectionMode{
        BEImageClipTop,
        BEImageClipLeft,
        BEImageClipBottom,
        BEImageClipRight,
        BEImageClipCenter,
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        while ((height / inSampleSize) >= reqHeight || (width / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    /** {zh} 
     * 压缩Bitmap的大小
     * Compress Bitmap size
     * @param imagePath     图片文件路径
     * @param requestWidth  压缩到想要的宽度
     * @param requestHeight 压缩到想要的高度
     * @return
     */
    /** {en} 
     * Compress Bitmap size
     * Compress Bitmap size
     * @param imagePath     Picture file path
     * @param requestWidth   Compress to desired width
     * @param requestHeight  Compress to desired height
     * @return
     */

    public static Bitmap decodeBitmapFromFile(String imagePath, int requestWidth, int requestHeight) {
        try{
            if (!TextUtils.isEmpty(imagePath)) {
                if (requestWidth <= 0 || requestHeight <= 0) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                    return rotateImage(bitmap, imagePath);
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;//   {zh} 不加载图片到内存，仅获得图片宽高     {en} Do not load pictures into memory, only get picture width and height 
                BitmapFactory.decodeFile(imagePath, options);
                if (options.outHeight == -1 || options.outWidth == -1) {
                    try {
                        ExifInterface exifInterface = new ExifInterface(imagePath);
                        int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//   {zh} 获取图片的高度     {en} Get the height of the picture 
                        int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//   {zh} 获取图片的宽度     {en} Get the width of the picture 

                        options.outWidth = width;
                        options.outHeight = height;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight); //   {zh} 计算获取新的采样率     {en} Calculate to get the new sampling rate 
                LogUtils.d( "inSampleSize: " + options.inSampleSize);
                options.inJustDecodeBounds = false;
                return rotateImage(BitmapFactory.decodeFile(imagePath, options),imagePath);

            } else {
                return null;
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap rotateImage(Bitmap bitmap,String path) throws IOException {
        if (bitmap == null) return null;
        if (TextUtils.isEmpty(path)) return null;
        int rotate = 0;
        ExifInterface exif;
        exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        if (rotate == 0)return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    public static ByteBuffer bitmap2ByteBuffer(final Bitmap bitmap){
        int bytes = bitmap.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        return buffer;

    }


    public static Bitmap getBitmapFromPixels(ByteBuffer byteBuffer, int width, int height) {

        Bitmap mCameraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        byteBuffer.position(0);
        mCameraBitmap.copyPixelsFromBuffer(byteBuffer);
        byteBuffer.position(0);
        return mCameraBitmap;
    }

    public static Bitmap getBitmapFromYuv(ByteBuffer data, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(data.array(), ImageFormat.NV21, width, height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 50, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap mCameraBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return mCameraBitmap;
    }


    /** {zh} 
     * 判断SDCard是否存在,并可写
     *
     * @return
     */
    /** {en} 
     * Determine whether SDCard exists and can be written
     *
     * @return
     */

    public static boolean checkSDCard() {
        String flag = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(flag);
    }

    public static File saveToLocal(Bitmap bitmap){
        if (null == bitmap) return null;
        String temp = FileUtils.createtFileName(".png");
        if (!checkSDCard()){
            LogUtils.e("sdcard not mounted");
            return null;

        }
        File dcimFile =  Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        File tempFile = new File(dcimFile,temp);
        LogUtils.e(tempFile.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tempFile = null;
        }catch (IOException e){
            e.printStackTrace();
            tempFile = null;
        }
        return tempFile;
    }



    public static File saveToLocalWithIndex(Bitmap bitmap, int i){
        if (null == bitmap) return null;
        String temp = i + ".png";
        if (!checkSDCard()){
            LogUtils.e("sdcard not mounted");
            return null;

        }
        File dcimFile =  Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        File tempFile = new File(dcimFile,temp);
        LogUtils.e(tempFile.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tempFile = null;
        }catch (IOException e){
            e.printStackTrace();
            tempFile = null;
        }
        return tempFile;
    }



    public static byte[] getDataFromImage(Image image, int colorFormat) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    break;
                case 2:
                        channelOffset = width * height;
                        outputStride = 2;
                    break;
                default:
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }

        }
        return data;
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    public static byte[] rotateNV21Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        int offset = imageHeight * imageWidth;
        int halfWidth = imageWidth / 2;
        int halfHeight = imageHeight / 2;
        for (int x = 0; x < halfWidth; x++) {
            for (int y = halfHeight - 1; y >= 0; y --) {
                int index = (halfWidth * y  + x) * 2;
                yuv[i] = data[offset + index] ;
                i ++;
                yuv[i] =data[offset + index + 1];
                i++;
            }
        }
        return yuv;
    }

    public static byte[] I420Tonv21(byte[] data, int width, int height) {
        byte[] ret = new byte[data.length];
        int total = width * height;

        ByteBuffer bufferY = ByteBuffer.wrap(ret, 0, total);
        ByteBuffer bufferVU = ByteBuffer.wrap(ret, total, total / 2);

        bufferY.put(data, 0, total);
        for (int i = 0; i < total / 4; i += 1) {
            bufferVU.put(data[i + total + total / 4]);
            bufferVU.put(data[total + i]);
        }

        return ret;
    }

    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
        return bmp;
    }

    /** {zh} 
     * 图片裁剪，默认为居中裁剪
     * @param image 默认图片
     * @param scale 裁剪比例
     * @return 处理后的图片
     */
    /** {en} 
     * Image crop, the default is center cropping
     * @param image default image
     * @param scale cropping scale
     * @return processed image
     */
    public static Bitmap cilpWithImage(Bitmap image, Size scale) {
        Bitmap lastBitmap = null;
        int _imageWidth;
        int _imageHeight;
        int _offsetX;
        int _offsetY;

        if (image.getWidth() / scale.getWidth() * scale.getHeight() <= image.getHeight())
        {
            _imageWidth = image.getWidth();
            _imageHeight = image.getWidth() / scale.getWidth() * scale.getHeight();
            _offsetX = 0;
            _offsetY = (image.getHeight() - image.getWidth() / scale.getWidth() * scale.getHeight()) / 2;
        }
        else {
            _imageWidth = image.getHeight() / scale.getHeight() * scale.getWidth();
            _imageHeight = image.getHeight();
            _offsetX = (image.getWidth() - image.getHeight() / scale.getHeight() * scale.getWidth()) / 2;
            _offsetY = 0;
        }
        lastBitmap = Bitmap.createBitmap(image, _offsetX, _offsetY, _imageWidth, _imageHeight);
        return lastBitmap;
    }

    /** {zh} 
     * 图片裁剪，默认为居中裁剪
     * @param image 默认图片
     * @param scale 裁剪比例
     * @param mode  裁剪模式
     * @return 处理后的图片
     */
    /** {en} 
     * Image crop, default is center cropping
     * @param image default image
     * @param scale cropping scale
     * @param mode  cropping mode
     * @return processed image
     */
    public static Bitmap cilpWithImage(Bitmap image, Size scale, BEImageDirectionMode mode) {
        Bitmap lastBitmap = null;
        int _imageWidth;
        int _imageHeight;
        int _offsetX;
        int _offsetY;

        if (image.getWidth() / scale.getWidth() * scale.getHeight() <= image.getHeight())
        {
            _imageWidth = image.getWidth();
            _imageHeight = image.getWidth() / scale.getWidth() * scale.getHeight();
            switch (mode) {
                case BEImageClipTop:
                    _offsetX = 0;
                    _offsetY = 0;
                    break;
                case BEImageClipBottom:
                    _offsetX = 0;
                    _offsetY = image.getHeight() - image.getWidth() / scale.getWidth() * scale.getHeight();
                    break;
                default:
                    _offsetX = 0;
                    _offsetY = (image.getHeight() - image.getWidth() / scale.getWidth() * scale.getHeight()) / 2;
                    break;
            }
        }
        else {
            _imageWidth = image.getHeight() / scale.getHeight() * scale.getWidth();
            _imageHeight = image.getHeight();
            switch (mode) {
                case BEImageClipLeft:
                    _offsetX = 0;
                    _offsetY = 0;
                    break;
                case BEImageClipRight:
                    _offsetX = image.getWidth() - image.getHeight() / scale.getHeight() * scale.getWidth();
                    _offsetY = 0;
                    break;
                default:
                    _offsetX = (image.getWidth() - image.getHeight() / scale.getHeight() * scale.getWidth()) / 2;
                    _offsetY = 0;
                    break;
            }
        }
        lastBitmap = Bitmap.createBitmap(image, _offsetX, _offsetY, _imageWidth, _imageHeight);
        return lastBitmap;
    }
}
