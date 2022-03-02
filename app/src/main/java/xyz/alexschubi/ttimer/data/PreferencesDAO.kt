package xyz.alexschubi.ttimer.data

import androidx.room.*

@Dao
interface PreferencesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(preferences: suppPreferences)

    @Update
    fun update(preferences: suppPreferences)

    @Query("SELECT * FROM preferencesTable ORDER BY sIndex DESC LIMIT 1")
    fun getLast(): suppPreferences

}