package com.unscroll.app

import android.app.Application
import com.unscroll.app.di.AppContainer

class UnscrollApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
