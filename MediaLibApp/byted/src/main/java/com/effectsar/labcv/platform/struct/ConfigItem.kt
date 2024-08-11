package com.effectsar.labcv.platform.struct

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ConfigResponse(
    var code: Int = -1, var message: String = "", var data: CategoryData = CategoryData()
)

@Keep
class CategoryData() : Parcelable {
    var version: String = ""
    var title: String = ""
    var icon: String = ""
    var tabs: List<CategoryTabItem> = emptyList()

    constructor(parcel: Parcel) : this() {
        version = parcel.readString() ?: ""
        title = parcel.readString() ?: ""
        icon = parcel.readString() ?: ""
        tabs = parcel.readArrayList(CategoryTabItem::class.java.classLoader) as? List<CategoryTabItem> ?: emptyList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(version)
        parcel.writeString(title)
        parcel.writeString(icon)
        parcel.writeList(tabs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<CategoryData> {
        override fun createFromParcel(parcel: Parcel): CategoryData {
            return CategoryData(parcel)
        }

        override fun newArray(size: Int): Array<CategoryData?> {
            return arrayOfNulls(size)
        }
    }
}

@Keep
class CategoryTabItem() : Parcelable {
    var title: String = ""
    var icon: String = ""
    var items: List<Material> = emptyList()

    @SerializedName("sub_tabs")
    var subTabs: List<CategoryTabItem> = emptyList()

    constructor(parcel: Parcel) : this() {
        title = parcel.readString() ?: ""
        icon = parcel.readString() ?: ""
        items = parcel.readArrayList(Material::class.java.classLoader) as? List<Material> ?: emptyList()
        subTabs = parcel.readArrayList(CategoryTabItem::class.java.classLoader) as? List<CategoryTabItem> ?: emptyList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(icon)
        parcel.writeList(items)
        parcel.writeList(subTabs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<CategoryTabItem> {
        override fun createFromParcel(parcel: Parcel): CategoryTabItem {
            return CategoryTabItem(parcel)
        }

        override fun newArray(size: Int): Array<CategoryTabItem?> {
            return arrayOfNulls(size)
        }
    }
}