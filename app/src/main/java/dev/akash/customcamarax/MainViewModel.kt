package dev.akash.customcamarax

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    fun uploadImageToFirebase(
        imageForUpload: File?,
        fileName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        onProgressUpdate: (Double) -> Unit,

        ) {
        viewModelScope.launch {
            Uri.fromFile(imageForUpload)?.let { uri ->
                val storage = Firebase.storage.reference
                val imgRef = storage.child("${Constants.BASE_IMAGE_FOLDER_NAME}/$fileName")
                imgRef.putFile(uri)
                    .addOnSuccessListener {
                        onSuccess.invoke()
                    }
                    .addOnFailureListener {
                        onFailure.invoke()
                    }
                    .addOnProgressListener {
                        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                        onProgressUpdate.invoke(progress)
                    }

            }
        }
    }


    private fun getCompleteFilePath(fileName: String?): String {
        return "CamaraImages/$fileName"
    }
}