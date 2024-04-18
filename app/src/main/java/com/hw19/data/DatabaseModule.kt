package com.hw19.data

import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providePhotoDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        PhotoDatabase::class.java,
        "database"
    ).build()

    @Singleton
    @Provides
    fun providePhotoDao(photoDatabase: PhotoDatabase): PhotoDao {
        return photoDatabase.photoDao()
    }
}