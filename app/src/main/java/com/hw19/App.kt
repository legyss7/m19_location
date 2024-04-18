package com.hw19

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(API_KEY)
    }

    companion object {
        private const val API_KEY = "7bf47b1d-2db3-4cf4-81ea-f88687636eea"
    }
}


