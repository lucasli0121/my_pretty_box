package com.effectsar.labcv.platform.struct

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MaterialResponse(
    var code: String = "",
    var message: String = "",
    var data: MaterialDataResponse = MaterialDataResponse()
)

@Keep
data class MaterialDataResponse(
    var medias: List<Material> = listOf()
)

@Keep
class Material() : Parcelable {
    var id: String = ""
    var title: String = ""
    var tips: String = ""

    /** {zh} 
     * 资源icon，http链接，Android需要开始HTTP访问
     */
    /** {en} 
     * Resource icon, http link, Android needs to start HTTP access
     */
    var icon: String = ""

    /** {zh} 
     * 资源zip包链接，下载下来会解压成文件夹
     */
    /** {en} 
     * Resource zip package link, it will be decompressed into a folder after downloading.
     */
    var url: String = ""

    /** {zh} 
     * 资源md5
     */
    /** {en} 
     * Resource md5
     */
    var md5: String = ""

    /** {zh} 
     * 文件名
     */
    /** {en} 
     * File name
     */
    @SerializedName("file_name")
    var fileName: String = ""

    /** {zh} 
     * 素材对应的视频地址
     */
    /** {en} 
     * Video address corresponding to the material
     */
    var video: String = ""

    /** {zh} 
     * 依赖的模型文件的数据结构
     */
    /** {en} 
     * The data structure of the dependent model file
     */
    var requirements: List<Requirement> = listOf()

    /** {zh} 
     * 依赖的模型文件的数据结构
     */
    /** {en} 
     * The data structure of the dependent model file
     */
    var extra: Extra = Extra()

    /** {zh} 
     * 下载进度
     */
    /** {en} 
     * Download progress
     */
    var progress: Int = 0
    var isDownloading: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Material

        if (id != other.id) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        title = parcel.readString() ?: ""
        tips = parcel.readString() ?: ""
        icon = parcel.readString() ?: ""
        url = parcel.readString() ?: ""
        md5 = parcel.readString() ?: ""
        fileName = parcel.readString() ?: ""
        video = parcel.readString() ?: ""
        progress = parcel.readInt()
        isDownloading = parcel.readInt() == 1
        requirements = parcel.readArrayList(Requirement::class.java.classLoader) as? List<Requirement> ?: emptyList()
        extra = parcel.readParcelable(Extra::class.java.classLoader) ?: Extra()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(tips)
        parcel.writeString(icon)
        parcel.writeString(url)
        parcel.writeString(md5)
        parcel.writeString(fileName)
        parcel.writeString(video)
        parcel.writeInt(progress)
        if (isDownloading) {
            parcel.writeInt(1)
        } else {
            parcel.writeInt(0)
        }
        parcel.writeList(requirements)
        parcel.writeParcelable(extra, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Material> {
        override fun createFromParcel(parcel: Parcel): Material {
            return Material(parcel)
        }

        override fun newArray(size: Int): Array<Material?> {
            return arrayOfNulls(size)
        }
    }
}