package dev.akash.customcamarax.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.akash.customcamarax.viewmodel.GalleryViewModel
import dev.akash.customcamarax.viewmodel.MainViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @ClassKey(MainViewModel::class)
    @IntoMap
    abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @ClassKey(GalleryViewModel::class)
    @IntoMap
    abstract fun galleryViewModel(galleryViewModel: GalleryViewModel): ViewModel

}