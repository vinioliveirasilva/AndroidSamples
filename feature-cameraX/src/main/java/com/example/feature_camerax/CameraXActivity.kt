package com.example.feature_camerax

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.camera.core.CameraSelector
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
            intent.getStringExtra(CAMERA_ID).orEmpty(),
            intent.getIntExtra(CAMERA_LENS, CameraSelector.LENS_FACING_BACK),
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

    fun startActivityWithFadeAnimation(cameraId: String, lens: Int) {
        finish()
        startActivity(newIntent(this, cameraId, lens))
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
    }

    fun updateUI(description: String) {
        with(cameraIdTextView) {
            text = text.toString().plus(description)
        }
    }

    private fun setupComponents() {
        previewView = findViewById(R.id.photo_preview)
        zoomCameraButton = findViewById(R.id.toggle_zoom)
        changeCameraButton = findViewById(R.id.change_camera)
        cameraIdTextView = findViewById(R.id.camera_id_tv)
    }

    private fun setupToolbar() {
        supportActionBar?.hide()
    }

    private fun setupClickListeners() {
        changeCameraButton.setOnClickListener { presenter.doOnChangeCamera() }
        zoomCameraButton.setOnClickListener { presenter.doOnZoomCamera() }
    }

    private fun showPermissionDenialToastAndFinish() {
        Toast.makeText(this,
            "Permissions not granted by the user.",
            Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
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