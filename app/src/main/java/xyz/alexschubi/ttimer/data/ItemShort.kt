package xyz.alexschubi.ttimer.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Parcelize
data class ItemShort(
    var Index: Long,
    var Text: String = "",
    var TimeStamp: Long?,
    var Color: String = "purple",
    var Notified: Boolean = false,
    var Deleted: Boolean = false ): Parcelable {

    companion object : Parceler<ItemShort>{
        override fun ItemShort.write(parcel: Parcel, flags: Int) {
            parcel.writeLong(Index)
            parcel.writeString(Text)
            parcel.writeLong(TimeStamp ?: -1)
            parcel.writeString(Color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                parcel.writeBoolean(Notified)
                parcel.writeBoolean(Deleted)
            } else {
                parcel.writeValue(Notified)
                parcel.writeValue(Deleted)
            }
        }

        override fun create(parcel: Parcel): ItemShort {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ItemShort(parcel.readLong(),
                    parcel.readString() ?: "",
                    parcel.readLong(),
                    parcel.readString() ?: "purple",
                    parcel.readBoolean(),
                    parcel.readBoolean()
                )
            } else {
                ItemShort(parcel.readLong(),
                    parcel.readString() ?: "",
                    parcel.readLong(),
                    parcel.readString() ?: "purple",
                    parcel.readValue(Boolean::class.java.classLoader) as Boolean,
                    parcel.readValue(Boolean::class.java.classLoader) as Boolean
                )
            }
        }

    }

}