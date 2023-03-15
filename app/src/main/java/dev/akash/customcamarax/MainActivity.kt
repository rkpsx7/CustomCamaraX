package dev.akash.customcamarax

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import dev.akash.customcamarax.DateTimeUtils.getDateForImageName
import dev.akash.customcamarax.databinding.ActivityMainBinding
import dev.akash.customcamarax.databinding.LoadingLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel

    private val requestCodePermission = 707
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector

    private var imageCapture: ImageCapture? = null
    private lateinit var imgExecutor: ExecutorService

    private var isInPreviewState = false

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (application as JukshioApplication).daggerAppComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        checkCamaraPermission()
        initialise()
        setButtonActions()
    }

    private fun setButtonActions() {
        binding.camaraView.apply {
            btnClickPic.setOnClickListener {
                capture()
            }

            btnSwitchCam.setOnClickListener {
                switchCam()
            }

            btnGallery.setOnClickListener {
                startActivity(Intent(this@MainActivity, GalleryActivity::class.java))
            }
        }

    }

    private fun startCamera() {
        isInPreviewState = false
        cameraProviderFuture.addListener({
            val camaraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setJpegQuality(100)
                .build()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.camaraView.camPreview.surfaceProvider)
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
            val timeStamp = System.currentTimeMillis()

            it.takePicture(
                imgExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        animateShutter()
                        val bitmap = image.convertImageProxyToBitmap()
                        previewImage(bitmap, timeStamp)
                        image.close()
                    }
                }
            )
        }
    }

    private fun previewImage(bitmap: Bitmap, timeStamp: Long) {
        isInPreviewState = true
        CoroutineScope(Dispatchers.Main).launch {
            binding.camaraView.root.visibilityGone()
            binding.imagePreviewLayout.apply {
                root.visibilityVisible()
                ivImagePrev.setImageBitmap(bitmap)
                btnRetake.setOnClickListener { retakePicture() }
                btnSavePic.setOnClickListener {
                    val isSaved = saveImage(bitmap)
                    if (isSaved)
                        launchProgressDialog(timeStamp)
                }
            }
        }
    }

    private fun animateShutter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.root.postDelayed({
                binding.root.foreground = ColorDrawable(Color.WHITE)
                binding.root.postDelayed({
                    binding.root.foreground = null
                }, 25)
            }, 50)
        }
    }

    private fun launchProgressDialog(timeStamp: Long) {
        val binding = LoadingLayoutBinding.inflate(LayoutInflater.from(this))
        val dialog = Dialog(this, R.style.Theme_Dialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(binding.root)
        }

        val fileName = "Jukshio IMG ${getDateForImageName(timeStamp)}.jpg"

        lifecycleScope.launch {
            viewModel.uploadImageToFirebase(
                getImageForUpload(),
                fileName,
                onSuccess = {
                    dialog.cancel()
                    Toast.makeText(this@MainActivity, "Upload Successful", Toast.LENGTH_SHORT)
                        .show()
                    retakePicture()
                },
                onFailure = {
                    dialog.cancel()
                    Toast.makeText(this@MainActivity, "Failed to upload!", Toast.LENGTH_LONG).show()
                },
                onProgressUpdate = {
                    binding.tvProgress.text = "${it.toInt()}%"
                    binding.progressBar.progress = it.toInt()
                }
            )
        }
        dialog.show()
    }

    private fun saveImage(bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("Jukshio_IMG.jpg", MODE_PRIVATE).use { stream ->
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

    private suspend fun getImageForUpload(): File? {
        return withContext(Dispatchers.IO) {
            val files = filesDir.listFiles()
            files?.find {
                it.canRead() && it.isFile && it.name.equals("Jukshio_IMG.jpg")
            }
        }
    }

    private fun retakePicture() {
        binding.imagePreviewLayout.root.visibilityGone()
        binding.camaraView.root.visibilityVisible()
        startCamera()
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

    private fun switchCam() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        else
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        startCamera() //restarting camara
    }

    override fun onBackPressed() {
        if (isInPreviewState)
            retakePicture()
        else
            super.onBackPressed()
    }

    override fun onResume() {
        startCamera()
        super.onResume()
    }

}