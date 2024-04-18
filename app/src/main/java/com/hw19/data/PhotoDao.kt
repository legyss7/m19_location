package com.hw19.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos ")
    fun getAll(): Flow<List<Photo>>

    @Insert
    suspend fun addPhoto(photo: Photo)

    @Query("DELETE FROM photos")
    suspend fun deleteAll()
}