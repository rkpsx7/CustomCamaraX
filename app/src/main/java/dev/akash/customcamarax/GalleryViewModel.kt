package dev.akash.customcamarax

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import javax.inject.Inject

class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _imageUrlList = MutableLiveData<List<String>>()
    val imageUrlList: LiveData<List<String>>
        get() = _imageUrlList

    fun getAllImages() {
        viewModelScope.launch {
            val storageRef = Firebase.storage.reference.child(Constants.BASE_IMAGE_FOLDER_NAME)
            val urlList = ArrayList<String>()
            val query = (storageRef.listAll()).asDeferred().await()
            query.items.forEach {
                val downloadUrl = (it.downloadUrl).asDeferred().await()
                urlList.add(downloadUrl.toString())
            }

            _imageUrlList.postValue(urlList)
        }
    }

}