package com.hw19.presentation.fragments.photos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class PhotosViewModelFactory @Inject constructor(
    private val listPhotos: PhotosViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotosViewModel::class.java)) {
            return listPhotos as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}