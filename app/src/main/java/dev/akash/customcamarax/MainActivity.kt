package dev.akash.customcamarax

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.google.common.util.concurrent.ListenableFuture
import dev.akash.customcamarax.databinding.ActivityMainBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val requestCodePermission = 707
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector

    private var imageCapture: ImageCapture? = null
    private lateinit var imgExecutor: ExecutorService

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        checkCamaraPermission()
        initialise()

        binding.btnClickPic.setOnClickListener {
            capture()
        }

        binding.btnSwitchCam.setOnClickListener {
            switchCam()
        }
    }

    private fun switchCam() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        else
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        startCamera() //restarting camara
    }


    private fun startCamera() {
        cameraProviderFuture.addListener({
            val camaraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setJpegQuality(100)
                .build()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.camPreview.surfaceProvider)
            }

            try {
                camaraProvider.unbindAll()
                camaraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (ignore: Exception) {

            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capture() {
        imageCapture?.let {
            val fileName = "Jukshio_IMG_Preview.jpg"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        previewImage(file.absolutePath)
                        Log.i("MAIN_ACTIVITY", "The image has been saved in ${file.toUri()}")
                    }

                    override fun onError(exception: ImageCaptureException) {

                    }

                }
            )
        }
    }

    private fun previewImage(path: String) {
        val intent = Intent(this, ImagePreviewActivity::class.java)
        intent.putExtra("img_path",path)
        startActivity(intent)
    }

    private fun initialise() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imgExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }

    private fun checkCamaraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                requestCodePermission
            )
        } else {
            initialise()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialise()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun onResume() {
        startCamera()
        super.onResume()
    }

}