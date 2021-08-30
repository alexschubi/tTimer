package com.alexschubi.ttimer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Item(
    var Index: Int,
    var Text: String,
    var Date: LocalDateTime?,
    var Span: String?,
    var Notified: Boolean,
    var Deleted: Boolean,
    var Color: String
                 ): Parcelable

