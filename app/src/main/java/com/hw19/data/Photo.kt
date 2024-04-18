package com.hw19.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    @ColumnInfo(name = "imgUri")
    val imgUri: String
) : Parcelable

