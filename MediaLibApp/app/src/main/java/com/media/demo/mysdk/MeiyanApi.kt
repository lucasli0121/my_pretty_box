package com.media.demo.mysdk

import android.content.Context
import com.media.demo.obj.BoxConf

open class MeiyanApi(var context: Context) {
    var boxCfg: BoxConf? = null

    open fun init(conf: BoxConf?) : Boolean {
        boxCfg = conf
        return true
    }

    open fun unInit() {
    }
    open fun reinit() : Boolean {
        return true
    }
    open fun reloadConfig(){

    }
    open fun renderTextureId(textureId: Int, len: Int, w: Int, h: Int): Int {
        return 0
    }
    open fun renderBuffer(data: ByteArray, len: Int, w: Int, h: Int): Int {
        return 0
    }
}