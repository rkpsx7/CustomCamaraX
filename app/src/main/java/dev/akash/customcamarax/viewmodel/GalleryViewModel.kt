package dev.akash.customcamarax.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.akash.customcamarax.repo.GalleryRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import javax.inject.Inject

class GalleryViewModel @Inject constructor(
    val repo: GalleryRepo
) : ViewModel() {

    private val _imageUrlList = MutableLiveData<List<String>>()
    val imageUrlList: LiveData<List<String>>
        get() = _imageUrlList

    fun getAllImages() {
        viewModelScope.launch {
            val res = repo.getImageItemsFromCloud()
            val urlList = ArrayList<String>()

            res?.let { response ->
                response.items.forEach {
                    val downloadUrl = (it.downloadUrl).asDeferred().await()
                    urlList.add(downloadUrl.toString())
                }
            }

            _imageUrlList.postValue(urlList)
        }
    }

}