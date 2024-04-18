package com.hw19.data

import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val photoDao: PhotoDao) {

    fun getAllPhoto(): Flow<List<Photo>> {
        return photoDao.getAll()
    }

    suspend fun deleteAllPhotos() {
        photoDao.deleteAll()
    }

    suspend fun addPhoto(context: Context) {
        photoDao.addPhoto(Photo(getUriLastPhoto(context)))
    }

    private fun getUriLastPhoto(context: Context): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )
        cursor?.use {
            if (cursor.moveToFirst()) {
                val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndexData)
            }
        }
        return ""
    }
}