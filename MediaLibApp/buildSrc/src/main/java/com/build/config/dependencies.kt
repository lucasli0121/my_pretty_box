/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.build.config

object Versions {
    const val compileSdkVersion = 34
    const val buildToolsVersion = "33.0.1"
    const val minSdkVersion = 29
    const val targetSdkVersion = 32
    const val versionCode = 202405173
    const val versionName = "1.1.11.5"

    const val gradleVersion = "8.1.1"
    const val kotlinVersion = "1.9.0"
    const val gradleBintrayVersion = "1.+"
    const val mavenGradleVersion = "2.1"
    const val navigationVersion =  "2.5.3"

}
object ClassPath {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradleVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val gradleBintrayPlugin = "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.gradleBintrayVersion}"
    const val androidMavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradleVersion}"
    const val navigationGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationVersion}"

}
object Libs {
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:2.0.0"
    const val junit = "junit:junit:4.+"

    object Compose {
        const val snapshot = ""
        const val kotlinCompilerVersion = "1.5.2"
        const val composeBom = "androidx.compose:compose-bom:2023.08.00"

        const val foundation = "androidx.compose.foundation:foundation"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout"
        const val material = "androidx.compose.material:material"
        const val materialIconsExtended = "androidx.compose.material:material-icons-extended"
        const val runtime = "androidx.compose.runtime:runtime"
        const val runtimeLivedata = "androidx.compose.runtime:runtime-livedata"
        const val animation = "androidx.compose.animation:animation"
        const val ui = "androidx.compose.ui:ui"
        const val tooling = "androidx.compose.ui:ui-tooling"
        const val toolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val test = "androidx.compose.ui:ui-test"
        const val uiTest = "androidx.compose.ui:ui-test-junit4"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
        const val uiUtil = "androidx.compose.ui:ui-util"
        const val viewBinding = "androidx.compose.ui:ui-viewbinding"
    }

    object Kotlin {
        const val kotlinBom = "org.jetbrains.kotlin:kotlin-bom:${Versions.kotlinVersion}"
        const val kotlinCompiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable"
        const val kotlinStdJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions"
    }
    object Coroutines {
        private const val version = "1.7.0"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Material {
        const val material3 = "androidx.compose.material3:material3:1.0.1"
        const val material = "com.google.android.material:material:1.6.0-alpha02"
    }
    object Accompanist {
        private const val version = "0.32.0"
        const val systemUIController = "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
    }
    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val coreKtx = "androidx.core:core-ktx:1.10.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val annotation = "androidx.annotation:annotation:1.6.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
        const val legacy = "androidx.legacy:legacy-support-v4:1.0.0"
        object Activity {
            const val activityCompose = "androidx.activity:activity-compose:1.6.1"
        }

        object Navigation {
            private const val version = "2.5.3"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
            const val componse = "androidx.navigation:navigation-compose:$version"
            const val testing = "androidx.navigation:navigation-testing:$version"
        }
        object Lifecycle {
            private const val version = "2.6.2"
            const val lifeRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
            const val lifeSaveState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"
        }

        object Test {
            private const val version = "1.5.0"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"
            object Ext {
                private const val version = "1.1.5"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }
            const val espressoCore = "androidx.test.espresso:espresso-core:3.5.1"
        }

    }
    object Http {
        const val asyncHttp = "com.loopj.android:android-async-http:1.4.11"
        const val okHttp3Bom = "com.squareup.okhttp3:okhttp-bom:4.10.0"
        const val okHttp3 = "com.squareup.okhttp3:okhttp"
        const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor"
    }
    object Bumptech {
        private  const val version = "4.16.0"
        const val glide = "com.github.bumptech.glide:glide:$version"
        const val glide_okhttp = "com.github.bumptech.glide:okhttp3-integration:4.16.0"
        const val compiler = "com.github.bumptech.glide:compiler:4.16.0"
    }
    object Refresh {
        private const val version = "2.1.0"
        const val refreshKernel = "io.github.scwang90:refresh-layout-kernel:$version"
        const val refreshClassics = "io.github.scwang90:refresh-header-classics:$version"
    }

    object Serialize {
        const val gson = "com.google.code.gson:gson:2.8.8" //https://github.com/google/gson
    }

    object Utils {
        const val blankUtil =  "com.blankj:utilcodex:1.30.6"
        const val adapterHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"
        const val mpChart = "com.github.PhilJay:MPAndroidChart:v3.1.0"
    }
    object MqUtils {
//        const val mqServer = "org.eclipse.paho:org.eclipse.paho.android.service:1.1.1"
        const val mqtt = "org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5"
        const val hannesaMqtt = "com.github.hannesa2:paho.mqtt.android:4.2"
    }
}
