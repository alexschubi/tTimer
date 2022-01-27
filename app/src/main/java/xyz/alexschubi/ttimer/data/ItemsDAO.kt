package xyz.alexschubi.ttimer.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemsDAO{ //TODO use coroutines / livedata
    @Insert
    fun insert(item: sItem)

    @Update
    fun update(item: sItem)

    @Query("SELECT * FROM itemsTable WHERE mIndex = :key")
    fun get(key: Int): sItem?

    @Query("SELECT * FROM itemsTable")
    fun getAll(): MutableList<sItem>

    @Query("SELECT * FROM itemsTable WHERE Deleted = 0")
    fun getActiveItems(): MutableList<sItem>

    @Query("SELECT * FROM itemsTable ORDER BY mIndex DESC LIMIT 1")
    fun getLast(): sItem

    @Query("DELETE FROM itemsTable")
    fun clear()

    @Query("DELETE FROM itemsTable WHERE mIndex = :key")
    fun delete(key: Int)

    @Query("SELECT COUNT(*) FROM itemsTable ")
    fun getItemsAmount(): Int
}