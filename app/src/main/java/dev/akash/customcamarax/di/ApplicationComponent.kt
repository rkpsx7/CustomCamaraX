package dev.akash.customcamarax.di

import androidx.lifecycle.ViewModel
import dagger.Component
import dev.akash.customcamarax.ui.activity.GalleryActivity
import dev.akash.customcamarax.ui.activity.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class])
interface ApplicationComponent {

    fun getViewModelMap(): Map<Class<*>, ViewModel>

    fun inject(mainActivity: MainActivity)
    fun inject(mainActivity: GalleryActivity)
}