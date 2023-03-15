package dev.akash.customcamarax

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import java.io.File

class MainViewModel : ViewModel() {

    private val _uploadProgress = MutableLiveData<Int>()
    val uploadProgress: LiveData<Int>
        get() = _uploadProgress

    fun uploadImageToFirebase(file: File) {

    }
}