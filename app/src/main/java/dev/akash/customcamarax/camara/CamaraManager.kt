package dev.akash.customcamarax.camara

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import dev.akash.customcamarax.utils.convertImageProxyToBitmap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamaraManager(
    private val activity: AppCompatActivity,
    private val surfaceProvider: Preview.SurfaceProvider,
    val onCaptureClick: (Bitmap,Long) -> Unit,
    val animateShutter: () -> Unit
) {

    private var imageCapture: ImageCapture? = null

    private var cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imgExecutor: ExecutorService = Executors.newSingleThreadExecutor()


    fun runCamera() {
        cameraProviderFuture.addListener({
            val camaraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setJpegQuality(100)
                .build()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(surfaceProvider)
            }

            try {
                camaraProvider.unbindAll()
                camaraProvider.bindToLifecycle(activity, cameraSelector, preview, imageCapture)
            } catch (ignore: Exception) {

            }
        }, ContextCompat.getMainExecutor(activity))
    }


    fun capture() {
        imageCapture?.let {
            val timeStamp = System.currentTimeMillis()

            it.takePicture(
                imgExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        animateShutter()
                        val bitmap = image.convertImageProxyToBitmap()
                        onCaptureClick.invoke(bitmap, timeStamp)
                        image.close()
                    }
                }
            )
        }
    }

    fun switchCam() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA

        runCamera() //restarting camara
    }


}