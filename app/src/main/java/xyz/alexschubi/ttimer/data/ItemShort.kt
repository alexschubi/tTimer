package xyz.alexschubi.ttimer.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
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

   // override fun writeToParcel(p0: Parcel?, p1: Int) { //p0.write//TODO LATEST }
}