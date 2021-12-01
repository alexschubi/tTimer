package xyz.alexschubi.ttimer.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemsDAO{ //TODO use corutines / livedata
    @Insert
    fun insert(item: sItem)

    @Update
    fun update(item: sItem)

    @Query("SELECT * FROM itemsTable WHERE 'Index' = :key LIMIT 1")
    fun get(key: Int): sItem?

    @Query("SELECT * FROM itemsTable")
    fun getAll(): MutableList<sItem>

    @Query("DELETE FROM itemsTable")
    fun clear()

}