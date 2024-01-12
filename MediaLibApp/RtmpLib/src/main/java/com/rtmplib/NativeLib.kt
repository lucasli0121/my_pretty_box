package com.rtmplib

class NativeLib {

    /**
     * A native method that is implemented by the 'rtmplib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'rtmplib' library on application startup.
        init {
            System.loadLibrary("rtmplib")
        }
    }
}