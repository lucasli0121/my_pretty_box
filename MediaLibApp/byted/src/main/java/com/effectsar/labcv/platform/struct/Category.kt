package com.effectsar.labcv.platform.struct

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CategoryResponse(
    var code: String = "", var message: String = "", var data: CategoryDataResponse = CategoryDataResponse()
)

@Keep
data class CategoryDataResponse(
    var categories: List<Category> = listOf()
)

@Keep
class Category() : Parcelable {
    @SerializedName("category_id")
    var categoryId: Long = -1L

    @SerializedName("category_key")
    var categoryKey: String = ""

    @SerializedName("category_name")
    var categoryName: String = ""

    @SerializedName("parent")
    var parent: Int = 0

    @SerializedName("children")
    var children: List<Long> = emptyList()

    var childCategory = mutableListOf<Category>()

    constructor(parcel: Parcel) : this() {
        categoryId = parcel.readLong()
        categoryKey = parcel.readString() ?: ""
        categoryName = parcel.readString() ?: ""
        parent = parcel.readInt()
        children = parcel.readArrayList(null) as? List<Long> ?: emptyList()
        childCategory = parcel.readArrayList(Category::class.java.classLoader) as? MutableList<Category> ?: mutableListOf()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(categoryId)
        parcel.writeString(categoryKey)
        parcel.writeString(categoryName)
        parcel.writeInt(parent)
        parcel.writeList(children)
        parcel.writeList(childCategory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}