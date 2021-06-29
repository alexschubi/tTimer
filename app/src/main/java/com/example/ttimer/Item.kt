package com.example.ttimer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
@Parcelize
data class Item(val Index: Int,
                val Text: String,
                val Date: LocalDateTime?,
                var Span: String?,
                var Notified: Boolean,
                var Deleted: Boolean
                 ): Parcelable

