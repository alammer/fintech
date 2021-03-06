package ru.fintech.devlife.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PosterDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosters(posterLocals: List<PosterLocal>)

    @Query("SELECT * FROM posters WHERE category = :category")
    suspend fun getPostersByCategory(category: String): List<PosterLocal>?

    @Query("DELETE FROM posters")
    fun clearPosters()

}