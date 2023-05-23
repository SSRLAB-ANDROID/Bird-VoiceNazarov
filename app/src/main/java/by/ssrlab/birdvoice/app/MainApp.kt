package by.ssrlab.birdvoice.app

import android.app.Application
import android.content.Context

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}