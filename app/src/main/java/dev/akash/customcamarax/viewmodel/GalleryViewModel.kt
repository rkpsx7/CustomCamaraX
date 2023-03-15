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
            val listToBeSortedByRecent = ArrayList<Pair<Long,String>>()
            val urlList = ArrayList<String>()

            res?.let { response ->
                response.items.forEach {
                    val downloadUrl = (it.downloadUrl).asDeferred().await()
                    val updatedTimeMillis = (it.metadata).asDeferred().await().updatedTimeMillis
                    listToBeSortedByRecent.add(Pair(updatedTimeMillis,downloadUrl.toString()))
                }
            }

            listToBeSortedByRecent.sortedByDescending { it.first }.forEach {
                urlList.add(it.second)
            }
            _imageUrlList.postValue(urlList)
        }
    }

}