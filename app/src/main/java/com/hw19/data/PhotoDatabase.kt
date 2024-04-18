package com.hw19.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Photo::class],
    version = 1
)
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}
