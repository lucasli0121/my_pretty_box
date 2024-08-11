package com.effectsar.labcv.platform.struct

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep

@Keep
class Extra() : Parcelable {

    // feature_all_model_resource
    var is_model_resource: Boolean = false
    var relative_path: String = ""

    // feature_sticker
    var key: String = ""
    var type: String = ""


    constructor(parcel: Parcel) : this() {
        is_model_resource = parcel.readInt() == 1
        relative_path = parcel.readString() ?: ""
        key = parcel.readString() ?: ""
        type = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        if (is_model_resource) {
            parcel.writeInt(1)
        } else {
            parcel.writeInt(0)
        }
        parcel.writeString(relative_path)
        parcel.writeString(key)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Extra> {
        override fun createFromParcel(parcel: Parcel): Extra {
            return Extra(parcel)
        }

        override fun newArray(size: Int): Array<Extra?> {
            return arrayOfNulls(size)
        }
    }
}