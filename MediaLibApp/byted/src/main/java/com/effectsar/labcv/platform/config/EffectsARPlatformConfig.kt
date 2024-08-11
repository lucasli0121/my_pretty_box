package com.effectsar.labcv.platform.config

import com.effectsar.labcv.platform.base.PlatformApp
import java.io.File

class EffectsARPlatformConfig(builder: Builder) {

    var resourcePath = builder.resourcePath
    var appVersion = builder.appVersion
    var language = builder.language
    var channel = builder.channel
    var envKey = builder.envKey
    var ppeKey = builder.ppeKey
    var url = builder.url

    open class Builder {
        // {zh} 资源存储根路径 {en} Resource storage root path
        internal lateinit var resourcePath: File
        internal var appVersion: String = ""
        internal var language: String = ""
        internal var channel: String = ""
        internal var envKey: String = ""
        internal var ppeKey: String = ""
        internal var url: String = ""

        fun setResourcePath(path: File): Builder {
            resourcePath = path
            return this
        }

        fun appVersion(version: String): Builder {
            appVersion = version
            return this
        }

        fun language(language: String): Builder {
            this.language = language
            return this
        }

        fun channel(channel: String): Builder {
            this.channel = channel
            return this
        }

        fun envKey(key: String): Builder {
            this.envKey = key
            return this
        }

        fun ppeKey(ppe: String): Builder {
            this.ppeKey = ppe
            return this
        }

        fun url(url: String): Builder {
            this.url = url
            return this
        }

        fun build(): EffectsARPlatformConfig {
            if (this::resourcePath.isInitialized.not()) {
                resourcePath = PlatformApp.instance.getExternalFilesDir("assets/resource")!!
            }
            return EffectsARPlatformConfig(this)
        }
    }
}