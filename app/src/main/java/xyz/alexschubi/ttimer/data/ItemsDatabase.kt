package xyz.alexschubi.ttimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [sItem::class, suppPreferences::class], version = 1, exportSchema = false)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun itemsDAO(): ItemsDAO
    abstract fun preferencesDAO(): PreferencesDAO
    companion object {
        @Volatile
        private var INSTANCE: ItemsDatabase? = null
        fun getDatabase(context: Context): ItemsDatabase? {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ItemsDatabase::class.java, "items_database"
                    ).allowMainThreadQueries().build()
                }
                return INSTANCE
            }
        }
        fun destroyInstance(){
            INSTANCE = null
        }
    }
}