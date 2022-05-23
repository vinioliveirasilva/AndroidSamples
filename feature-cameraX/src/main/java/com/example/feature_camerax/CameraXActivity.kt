package com.example.feature_camerax

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.parameter.parametersOf

class CameraXActivity : AppCompatActivity() {

    private val presenter: CameraXPresenter by inject {
        parametersOf(this)
    }

    private lateinit var previewView: PreviewView

    private lateinit var takePictureButton: AppCompatImageButton
    private lateinit var changeCameraButton: AppCompatImageButton
    private lateinit var toggleFlashButton: AppCompatImageButton
    private lateinit var zoomCameraButton: AppCompatImageButton
    private lateinit var cameraIdTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        loadKoinModules(CameraXModule.instance)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ui)

        setupComponents()
        setupToolbar()
        setupClickListeners()

        presenter.init(
            allPermissionsGranted(),
            previewView
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(CameraXModule.instance)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                presenter.doOnPermissionsGranted()
            } else {
                showPermissionDenialToastAndFinish()
            }
        }
    }

    fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    fun updateUI(description: String) {
        with(cameraIdTextView) {
            text = "Cameras: ".plus(description)
        }
    }

    private fun setupComponents() {
        previewView = findViewById(R.id.photo_preview)
        zoomCameraButton = findViewById(R.id.toggle_zoom)
        changeCameraButton = findViewById(R.id.change_camera)
        cameraIdTextView = findViewById(R.id.camera_id_tv)
        takePictureButton = findViewById(R.id.take_photo)
    }

    private fun setupToolbar() {
        supportActionBar?.hide()
    }

    private fun setupClickListeners() {
        changeCameraButton.setOnClickListener { presenter.doOnChangeCamera() }
        zoomCameraButton.setOnClickListener { presenter.doOnZoomCamera() }
        takePictureButton.setOnClickListener { presenter.onTakePicture() }
    }

    private fun showPermissionDenialToastAndFinish() {
        Toast.makeText(
            this,
            "Permissions not granted by the user.",
            Toast.LENGTH_SHORT
        )
            .show()
        finish()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    fun showCameraInitializationError() {
        Toast.makeText(
            this,
            "Erro ao tentar inicializar a camera",
            Toast.LENGTH_SHORT
        )
            .show()
        finish()
    }

    fun finishWithSuccess(data: Intent) {
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    fun showToast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun showCameraState(msg: String) {
        Toast.makeText(
            this,
            "CameraState: ".plus(msg),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private companion object {
        const val REQUEST_CODE_PERMISSIONS = 22

        val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}