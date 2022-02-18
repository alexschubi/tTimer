package xyz.alexschubi.ttimer.data

import androidx.room.*

@Entity(tableName = "preferencesTable")
data class suppPreferences(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "sIndex") var sIndex: Long = 1, //maybe a better solution than resetting a table with max 1
    @ColumnInfo(name = "pref_theme") var Theme: String = "followSystem",
    @ColumnInfo(name = "pref_sync_enabled") var SyncEnabled: Boolean = false,
    @ColumnInfo(name = "pref_sync_connection") var SyncConnection: String? = null,
    @ColumnInfo(name = "pref_firebase_enabled") var FirebaseEnabled: Boolean = false,
    @ColumnInfo(name = "pref_notifications_enabled") var Notifications: Boolean = true,
    @ColumnInfo(name = "app_version") var AppVersion: String = "2.0",
    @ColumnInfo(name = "sort_mode") var SortMode: Int = 0,
    @ColumnInfo(name = "first_start") var FirstStart: Boolean = true,
    @ColumnInfo(name = "pref_date_format") var DateFormat: String = "EE dd.MM.yyyy HH:mm"
)
