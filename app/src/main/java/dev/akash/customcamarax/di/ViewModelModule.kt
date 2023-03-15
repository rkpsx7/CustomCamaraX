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


//
//    @Singleton
//    @Provides
//    fun providesRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://storage.googleapis.com")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Singleton
//    @Provides
//    fun providesNewsHomeApi(retrofit: Retrofit): NewsApi {
//        return retrofit.create(NewsApi::class.java)
//    }
}