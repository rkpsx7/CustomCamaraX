package dev.akash.customcamarax.repo

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.ktx.storage
import dev.akash.customcamarax.utils.Constants
import kotlinx.coroutines.tasks.asDeferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryRepo @Inject constructor() {

    private val storageRef = Firebase.storage.reference

    suspend fun getImageItemsFromCloud(): ListResult? {
        val storageRef = storageRef.child(Constants.BASE_IMAGE_FOLDER_NAME)
        return (storageRef.listAll()).asDeferred().await()
    }

}