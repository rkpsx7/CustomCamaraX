package dev.akash.customcamarax.utils

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import dev.akash.customcamarax.utils.Constants.CACHE_IMAGE_DEFAULT_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

object LocalCacheImageManager {

    fun saveImageAsCache(activity: AppCompatActivity, bitmap: Bitmap): Boolean {
        return try {
            activity.openFileOutput(CACHE_IMAGE_DEFAULT_NAME, AppCompatActivity.MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
                true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getImageForUpload(activity: AppCompatActivity): File? {
        return withContext(Dispatchers.IO) {
            val files = activity.filesDir.listFiles()
            files?.find {
                it.canRead() && it.isFile && it.name.equals(CACHE_IMAGE_DEFAULT_NAME)
            }
        }
    }
}