package com.alexschubi.ttimer

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
@Parcelize
data class Item(val Index: Int,
                val Text: String,
                var Date: LocalDateTime?,
                var Span: String?,
                var Notified: Boolean,
                var Deleted: Boolean,
                var Color: String
                 ): Parcelable

