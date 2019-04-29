package com.willowtreeapps.namegame.network.api.model

import android.os.Parcel
import android.os.Parcelable

data class SocialLink(
        val type: String?,
        val callToAction: String?,
        val url: String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeString(type)
            it.writeString(callToAction)
            it.writeString(url)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SocialLink> {
        override fun createFromParcel(parcel: Parcel): SocialLink {
            return SocialLink(parcel)
        }

        override fun newArray(size: Int): Array<SocialLink?> {
            return arrayOfNulls(size)
        }
    }
}