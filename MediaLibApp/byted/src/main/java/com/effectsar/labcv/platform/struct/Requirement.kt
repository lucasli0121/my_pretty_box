package com.effectsar.labcv.platform.struct

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep

@Keep
class Requirement() : Parcelable {
    var id: String = ""
    var name: String = ""
    var md5: String = ""
    var url: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
        md5 = parcel.readString() ?: ""
        url = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(md5)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Requirement> {
        override fun createFromParcel(parcel: Parcel): Requirement {
            return Requirement(parcel)
        }

        override fun newArray(size: Int): Array<Requirement?> {
            return arrayOfNulls(size)
        }
    }
}