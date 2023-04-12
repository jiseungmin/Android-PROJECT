package com.example.camerax

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import android.util.Log
import android.view.Display
import androidx.camera.core.*
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.PermissionChecker
import androidx.core.content.getSystemService
import com.example.camerax.databinding.ActivityMainBinding
import com.example.camerax.util.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {

    private lateinit var Binding: ActivityMainBinding

    private lateinit var cameraExecutor: ExecutorService
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }

    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private var displayId: Int = -1

    private var camera: Camera? = null

    private var isCapturing: Boolean = false

    private lateinit var imageCapture: ImageCapture

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(p0: Int) = Unit

        override fun onDisplayRemoved(p0: Int) = Unit

        override fun onDisplayChanged(p0: Int) {
            if (this@MainActivity.displayId == displayId) {

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(Binding.root)

        if (allPermissionsGranted()) {
            startCamera(Binding.viewFinder)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(Binding.viewFinder)
            } else {
                Toast.makeText(this, "카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { // 권한을 얻었는지 체크 하는 함수
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera(viewFinder: PreviewView) {
        displayManager.registerDisplayListener(displayListener, null)
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        }, 10)
    }

    private fun bindCaptureListner() = with(Binding){
        captureButton.setOnClickListener {
            if(isCapturing.not()){
                isCapturing = true
                captureCamera()
            }
        }
    }

    private var contentUri: Uri? = null

    private fun captureCamera() {
        if (::imageCapture.isInitialized.not()) return
        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val saveUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                contentUri = saveUri
                updateSavedImageContent()
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                isCapturing = false
            }

        })
    }

    private fun bindCameraUseCase() = with(Binding) {
        val rotation = viewFinder.display.rotation
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(LENS_FACING).build() // 카메라 설정

        cameraProviderFuture.addListener({
            val cameraProvinder: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
            }.build()

            val imageCaptureBuilder = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)

            imageCapture = imageCaptureBuilder.build()
            try {
                cameraProvinder.unbindAll() // 기존에 바인딩 되어 있는 카메라는 해체시켜준다.
                camera = cameraProvinder.bindToLifecycle(
                    this@MainActivity, cameraSelector, preview, imageCapture
                )
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
                bindCaptureListner()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)
    }

    private fun updateSavedImageContent(){
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it)?:throw FileNotFoundException())
                MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)
                false
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }


    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK
    }
}