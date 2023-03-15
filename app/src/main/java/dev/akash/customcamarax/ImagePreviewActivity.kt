package dev.akash.customcamarax

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import dev.akash.customcamarax.databinding.ActivityImagePreviewBinding
import java.io.File

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_preview)

        loadCapturedImage()

        binding.btnCancel.setOnClickListener { finish() }

        binding.btnSavePic.setOnClickListener {
            uploadImageToFirebase()
        }

    }

    private fun uploadImageToFirebase() {

    }

    private fun loadCapturedImage() {
        val file = intent.getStringExtra("img_path")?.let { File(it) }
        binding.ivImagePrev.setImageURI(Uri.parse(intent.getStringExtra("img_path")))
    }
}