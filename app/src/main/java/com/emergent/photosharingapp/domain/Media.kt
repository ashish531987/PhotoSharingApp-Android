package com.emergent.photosharingapp.domain

import android.os.Parcel
import android.os.Parcelable

data class Media(val id : Long,
                 val user: User,
                 val downloadURI: String,
                 val likesCount: Int,
                 val commentsCount: Int,
                 val likedByMe : Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readParcelable(User::class.java.classLoader) as User,
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(user, flags)
        parcel.writeString(downloadURI)
        parcel.writeInt(likesCount)
        parcel.writeInt(commentsCount)
        parcel.writeByte(if (likedByMe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }
    }
}
