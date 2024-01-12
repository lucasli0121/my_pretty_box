package com.media.demo

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.media.demo.databinding.RecordTestActivityBinding
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class RecordTestActivity: AppCompatActivity(R.layout.record_test_activity), WriteView.OnDrawBitmapListener {
    lateinit var binding: RecordTestActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecordTestActivityBinding.inflate(layoutInflater)
        binding.writeView.drawBitmapListener = this
    }

    /*
        被WriteView的OnDraw回调，用于处理bitmap
     */
    override fun onDrawBitmap(bm: Bitmap) {
        try {
            var byBuf = ByteBuffer.allocate(bm.byteCount)
            bm.copyPixelsToBuffer(byBuf)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}