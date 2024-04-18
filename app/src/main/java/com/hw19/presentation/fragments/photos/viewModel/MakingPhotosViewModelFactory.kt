package com.hw19.presentation.fragments.photos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class MakingPhotosViewModelFactory @Inject constructor(
    private val makePhoto: MakingPhotosViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MakingPhotosViewModel::class.java)) {
            return makePhoto as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}