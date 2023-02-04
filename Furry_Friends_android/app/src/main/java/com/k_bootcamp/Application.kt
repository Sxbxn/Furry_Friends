package com.k_bootcamp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application() {
//    companion object {
//        lateinit var prefs: PreferenceUtils
//    }
    override fun onCreate() {
//        prefs = PreferenceUtils(applicationContext)
        super.onCreate()
    }
}