package xyz.alexschubi.ttimer.data

import androidx.room.*
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity(tableName = "itemsTable")
data class sItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "mIndex") var Index: Long,
    @ColumnInfo(name = "Text") var Text: String = "",
    @ColumnInfo(name = "Date") var TimeStamp: Long?,
    @ColumnInfo(name = "Span") var Span: String?,
    @ColumnInfo(name = "Color") var Color: String = "purple",
    @ColumnInfo(name = "Notified") var Notified: Boolean = false,
    @ColumnInfo(name = "Deleted") var Deleted: Boolean = false,
    )