package com.example.feature_camerax

import android.annotation.SuppressLint
import android.hardware.camera2.CameraCharacteristics
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

class CameraXPresenter(
    private val view: CameraXActivity,
    private val cameraProvider: ListenableFuture<ProcessCameraProvider>
) {

    private var cameraList: MutableMap<String, Int> = mutableMapOf()
    private var cameraId: String = ""
    private var selectedLens = CameraSelector.LENS_FACING_BACK
    private lateinit var previewView: PreviewView

    fun init(
        cameraId: String,
        selectedCameraLensId: Int,
        hasAllPermissionsGranted: Boolean,
        previewView: PreviewView
    ) {
        this.cameraId = cameraId
        this.selectedLens = selectedCameraLensId
        this.previewView = previewView

        if(hasAllPermissionsGranted) {
            startCamera()
        } else {
            view.requestCameraPermissions()
        }
    }

    fun doOnChangeCamera() {
        cameraList
            .filter { it.value != selectedLens }
            .map { Pair(it.key, it.value) }
            .first()
            .let { view.startActivityWithFadeAnimation(it.first, it.second) }
    }

    fun doOnZoomCamera() {
        view.startActivityWithFadeAnimation(cameraId, selectedLens)
    }

    fun doOnPermissionsGranted() {
        view.startActivityWithFadeAnimation(cameraId, selectedLens)
    }

    private fun startCamera() {
        cameraProvider.addListener({
            val cameraProvider = cameraProvider.get()
            bindPreview(cameraProvider, previewView)
        }, ContextCompat.getMainExecutor(view))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider, previewView: PreviewView) {
        val preview : Preview = Preview.Builder()
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .addCameraFilter { cameras ->
                setupCameraCharacteristics(cameras)
                cameras
                    .filter { getCurrentCameraFilter(it) }
                    .also { updateCameraList(it) }
            }
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(view as LifecycleOwner, cameraSelector, preview)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun updateCameraList(camerasInfo: List<CameraInfo>) {
        cameraId = Camera2CameraInfo.from(camerasInfo.first()).cameraId
        cameraList
            .filter { camera -> camera.value == selectedLens && camera.key != cameraId }
            .forEach { nextCamera -> cameraId = nextCamera.key }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getCurrentCameraFilter(cameraInfo: CameraInfo): Boolean {
        val camera2Info = Camera2CameraInfo.from(cameraInfo)

        return if(cameraId.isBlank()) {
            camera2Info.getCameraCharacteristic(CameraCharacteristics.LENS_FACING) == selectedLens
        } else {
            cameraId == camera2Info.cameraId
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setupCameraCharacteristics(cameras: List<CameraInfo>) {
        cameras.forEach {
            with(Camera2CameraInfo.from(it)) {
                cameraList[cameraId] =
                    getCameraCharacteristic(CameraCharacteristics.LENS_FACING) as Int
            }
        }

        cameraList
            .map { cameraDescriptionTextFormatter(it) }
            .joinToString()
            .also { description -> view.updateUI(description) }
    }

    private fun cameraDescriptionTextFormatter(
        cameraDescriptionMap: Map.Entry<String, Int>
    ): String {
        with(cameraDescriptionMap) {
            return "\nId = $key - ${
                if(value == CameraSelector.LENS_FACING_FRONT) FRONT_LEN
                else BACK_LEN
            }"
        }
    }

    private companion object {
        const val FRONT_LEN = "Frontal"
        const val BACK_LEN = "Traseira"
    }
}