package dev.akash.customcamarax.ui.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.akash.customcamarax.*
import dev.akash.customcamarax.utils.DateTimeUtils.getDateForImageName
import dev.akash.customcamarax.R
import dev.akash.customcamarax.camara.CamaraManager
import dev.akash.customcamarax.databinding.ActivityMainBinding
import dev.akash.customcamarax.databinding.LoadingLayoutBinding
import dev.akash.customcamarax.utils.LocalCacheImageManager
import dev.akash.customcamarax.utils.safeClick
import dev.akash.customcamarax.utils.visibilityGone
import dev.akash.customcamarax.utils.visibilityVisible
import dev.akash.customcamarax.viewmodel.MainViewModel
import dev.akash.customcamarax.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var camaraManager: CamaraManager

    private val requestCodePermission = 707
    private var isInPreviewState = false

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
            btnClickPic.safeClick {
                camaraManager.capture()
            }

            btnSwitchCam.setOnClickListener {
                camaraManager.switchCam()
            }

            btnGallery.setOnClickListener {
                startActivity(Intent(this@MainActivity, GalleryActivity::class.java))
            }
        }

    }

    private fun previewImage(bitmap: Bitmap, timeStamp: Long) {
        isInPreviewState = true
        CoroutineScope(Dispatchers.Main).launch {
            binding.camaraView.root.visibilityGone()
            binding.imagePreviewLayout.apply {
                root.visibilityVisible()
                ivImagePrev.setImageBitmap(bitmap)
                btnRetake.safeClick { retakePicture() }
                btnSavePic.safeClick {
                    val isSaved = LocalCacheImageManager.saveImageAsCache(this@MainActivity, bitmap)
                    if (isSaved)
                        launchUploadDialog(timeStamp)
                    else
                        showToast(getString(R.string.error_msg))
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

    private fun launchUploadDialog(timeStamp: Long) {
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
                LocalCacheImageManager.getImageForUpload(this@MainActivity),
                fileName,
                onSuccess = {
                    dialog.cancel()
                    showToast(getString(R.string.upload_success_msg))
                    retakePicture()
                },
                onFailure = {
                    dialog.cancel()
                    showToast(getString(R.string.failed_upload_msg))
                },
                onProgressUpdate = {
                    binding.tvProgress.text = buildString {
                        append(it.toInt())
                        append(" %")
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        binding.progressBar.setProgress(it.toInt(), true)
                    else binding.progressBar.progress = it.toInt()
                }
            )
        }
        dialog.show()
    }

    private fun retakePicture() {
        binding.camaraView.root.visibilityVisible()
        binding.imagePreviewLayout.root.visibilityGone()

        startCamara()
    }

    private fun initialise() {
        camaraManager = CamaraManager(
            this,
            binding.camaraView.camPreview.surfaceProvider,
            animateShutter = { animateShutter() },
            onCaptureClick = { bitmap, timeStamp ->
                previewImage(bitmap, timeStamp)
            }
        )

        startCamara()
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
                showToast(getString(R.string.cam_permission_denied))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (isInPreviewState)
            retakePicture()
        else
            super.onBackPressed()
    }

    override fun onResume() {
        if (::camaraManager.isInitialized)
            startCamara()
        super.onResume()
    }

    private fun startCamara() {
        isInPreviewState = false
        camaraManager.runCamera()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}