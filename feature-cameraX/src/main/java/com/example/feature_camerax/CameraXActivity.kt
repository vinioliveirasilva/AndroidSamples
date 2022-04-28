package com.example.feature_camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

class CameraXActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView

    private lateinit var takePictureButton: AppCompatImageButton
    private lateinit var changeCameraButton: AppCompatImageButton
    private lateinit var toggleFlashButton: AppCompatImageButton
    private lateinit var zoomCameraButton: AppCompatImageButton
    private lateinit var cameraIdTextView: TextView

    private var cameraList: MutableMap<String, Int> = mutableMapOf()
    private var cameraId: String = ""
    private var selectedLens = CameraSelector.LENS_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ui)
        supportActionBar?.hide()
        previewView = findViewById(R.id.photo_preview)
        zoomCameraButton = findViewById(R.id.toggle_zoom)
        changeCameraButton = findViewById(R.id.change_camera)
        cameraIdTextView = findViewById(R.id.camera_id_tv)

        cameraId = intent.getStringExtra(CAMERA_ID).orEmpty()
        selectedLens = intent.getIntExtra(CAMERA_LENS, selectedLens)

        changeCameraButton.setOnClickListener {
            cameraList
                .filter { it.value != selectedLens }
                .map { Pair(it.key, it.value) }
                .first()
                .let {
                    startActivityWithFadeAnimation(it.first, it.second)
                }
        }

        zoomCameraButton.setOnClickListener {
            startActivityWithFadeAnimation(cameraId, selectedLens)
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startActivityWithFadeAnimation(cameraId: String, lens: Int) {
        finish()
        startActivity(newIntent(this, cameraId, lens))
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        cameraIdTextView.text = "Cameras:"
        val frontalLens = CameraSelector.LENS_FACING_FRONT

        cameraList.map {
            "\nId = ${it.key} - ${if(it.value == frontalLens) "Frontal" else "Traseira"}"
        }.forEach {
            cameraIdTextView.text = cameraIdTextView.text.toString().plus(it)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setupCameraCharacteristics(cameras: List<CameraInfo>) {
        cameras.forEach {
            with(Camera2CameraInfo.from(it)) {
                cameraList[this.cameraId] =
                    this.getCameraCharacteristic(
                        CameraCharacteristics.LENS_FACING
                    ) as Int
            }
        }

        setupUI()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startActivityWithFadeAnimation(cameraId, selectedLens)
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val preview : Preview = Preview.Builder()
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .addCameraFilter { cameras ->
                setupCameraCharacteristics(cameras)
                cameras.filter {
                    if(cameraId.isBlank()) {
                        Camera2CameraInfo.from(it).getCameraCharacteristic(CameraCharacteristics.LENS_FACING) == selectedLens
                    } else {
                        cameraId == Camera2CameraInfo.from(it).cameraId
                    }
                }.also {
                    cameraId = Camera2CameraInfo.from(it.first()).cameraId
                    cameraList.filter { it.value == selectedLens && it.key != cameraId }.forEach { nextCamera ->
                         cameraId = nextCamera.key
                    }
                }
            }
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 22
        private const val CAMERA_ID = "CAMERA_ID"
        private const val CAMERA_LENS = "CAMERA_LENS"

        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        fun newIntent(
            context: Context,
            cameraId: String = "",
            lensType: Int = 0
        ) = Intent(context, CameraXActivity::class.java).apply {
            putExtra(CAMERA_ID, cameraId)
            putExtra(CAMERA_LENS, lensType)
        }
    }
}