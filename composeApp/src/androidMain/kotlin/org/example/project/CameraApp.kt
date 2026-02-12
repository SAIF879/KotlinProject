package org.example.project

import android.app.Application
import org.example.project.camera.di.cameraModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class CameraApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@CameraApp)
            modules(cameraModule)
        }
    }
}
