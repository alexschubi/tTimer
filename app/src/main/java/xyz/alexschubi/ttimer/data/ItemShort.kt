package xyz.alexschubi.ttimer.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

data class ItemShort(
    var Index: Long,
    var Text: String = "",
    var TimeStamp: Long?,
    var Color: String = "purple",
    var Notified: Boolean = false,
    var Deleted: Boolean = false ): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "purple",
        parcel.readBoolean(),
        parcel.readBoolean() )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(Index)
        parcel.writeString(Text)
        parcel.writeLong(TimeStamp ?: -1)
        parcel.writeString(Color)
        parcel.writeBoolean(Notified)
        parcel.writeBoolean(Deleted)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemShort> {
        override fun createFromParcel(parcel: Parcel): ItemShort {
            return ItemShort(parcel)
        }

        override fun newArray(size: Int): Array<ItemShort?> {
            return arrayOfNulls(size)
        }
    }
}

