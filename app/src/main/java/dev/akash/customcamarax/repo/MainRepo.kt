package dev.akash.customcamarax.repo

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepo @Inject constructor() {

    private val storageRef = Firebase.storage.reference

    fun uploadImage(path: String, uri: Uri): UploadTask {
        val imgRef = storageRef.child(path)
        return imgRef.putFile(uri)
    }
}