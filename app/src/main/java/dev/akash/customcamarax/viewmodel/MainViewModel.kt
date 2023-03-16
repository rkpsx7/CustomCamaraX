package dev.akash.customcamarax.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.akash.customcamarax.repo.MainRepo
import dev.akash.customcamarax.utils.Constants
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repo: MainRepo
) : ViewModel() {

    fun uploadImageToFirebase(
        imageForUpload: File?,
        fileName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        onProgressUpdate: (Double) -> Unit,
    ) {
        viewModelScope.launch {
            Uri.fromFile(imageForUpload)?.let { uri ->
                val path = "${Constants.BASE_IMAGE_FOLDER_NAME}/$fileName"

                repo.uploadImage(path, uri)
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
}