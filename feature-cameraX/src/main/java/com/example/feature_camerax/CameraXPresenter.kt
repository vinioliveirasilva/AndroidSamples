package com.example.feature_camerax

import android.annotation.SuppressLint
import android.hardware.camera2.CameraCharacteristics
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor

class CameraXPresenter(
    private val view: CameraXActivity,
    private val cameraProvider: ListenableFuture<ProcessCameraProvider>,
    private val cameraThreadExecutor: Executor
) {

    private var cameraList: MutableMap<String, Int> = mutableMapOf()
    private var cameraId: String = ""
    private var selectedLens = CameraSelector.LENS_FACING_BACK
    private lateinit var previewView: PreviewView

    private var mustRestartCamera = false
    private var hasLifecycleDetached: Boolean = true
        set(value) {
            if(value && mustRestartCamera) {
                startCamera()
                mustRestartCamera = false
            }

            field = value
        }

    @SuppressLint("RestrictedApi")
    private val useCaseCallback = object : UseCase.EventCallback {
        override fun onAttach(cameraInfo: CameraInfo) {
            hasLifecycleDetached = false
        }

        override fun onDetach() {
            hasLifecycleDetached = true
        }
    }

    fun init(
        hasAllPermissionsGranted: Boolean,
        previewView: PreviewView
    ) {
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
            .firstOrNull()
            ?.let { config ->
                    cameraId = config.first
                    selectedLens = config.second
                }

        mustRestartCamera = true
        stopCamera()
    }

    fun doOnZoomCamera() {
        mustRestartCamera = true
        stopCamera()
    }

    fun doOnPermissionsGranted() {
        startCamera()
    }

    private fun stopCamera() {
        cameraProvider.get().run {
            unbindAll()
        }
    }

    private fun startCamera() {
        cameraProvider.addListener(
            { bindPreview(cameraProvider.get()) },
            cameraThreadExecutor
        )
    }

    @SuppressLint("RestrictedApi")
    private fun getPreviewUseCase(): Preview {
        return Preview
            .Builder()
            .setUseCaseEventCallback(useCaseCallback)
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
    }

    private fun getCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .addCameraFilter { cameras ->
                setupCameraCharacteristics(cameras)
                cameras
                    .filter { getCurrentCameraFilter(it) }
                    .also { updateCameraList(it) }
            }
            .build()
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val cameraSelector = getCameraSelector()

        if(cameraProvider.hasCamera(cameraSelector)) {
            cameraProvider.bindToLifecycle(
                view as LifecycleOwner,
                cameraSelector,
                getPreviewUseCase()
            )
        } else {
            view.showCameraInitializationError()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun updateCameraList(camerasInfo: List<CameraInfo>) {
        camerasInfo.firstOrNull()?.let {
            cameraId = Camera2CameraInfo.from(it).cameraId
            cameraList
                .filter { camera -> camera.value == selectedLens && camera.key != cameraId }
                .forEach { nextCamera -> cameraId = nextCamera.key }
        }
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
        cameraList.clear()
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