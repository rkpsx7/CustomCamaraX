package dev.akash.customcamarax

import android.app.Application
import dev.akash.customcamarax.di.ApplicationComponent
import dev.akash.customcamarax.di.DaggerApplicationComponent

class JukshioApplication : Application() {

    lateinit var daggerAppComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        daggerAppComponent = DaggerApplicationComponent.builder().build()
    }

}