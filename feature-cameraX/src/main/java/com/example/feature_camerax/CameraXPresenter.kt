package com.example.feature_camerax

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.provider.MediaStore
import android.util.Log
import android.view.Surface.ROTATION_0
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
    private val cameraThreadExecutor: Executor,
    private val contentResolver: ContentResolver
) {

    private var imageCapture: ImageCapture = getImageCaptureUseCase()
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

    private val imageCaptureCallbacks = object : ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
            Log.e("CameraXError", "Photo capture failed: ${exc.message}", exc)
        }

        override fun onImageSaved(output: ImageCapture.OutputFileResults){
            val msg = "Photo capture succeeded: ${output.savedUri}"
            view.showToast(msg)
            view.finishWithSuccess(
                Intent().apply {
                    putExtra("IMAGE_URI", output.savedUri)
                }
            )
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
            observeCameraState(
                cameraProvider.bindToLifecycle(
                    view as LifecycleOwner,
                    cameraSelector,
                    getPreviewUseCase(),
                    imageCapture
                ).cameraInfo
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

    @SuppressLint("RestrictedApi")
    private fun getImageCaptureUseCase(): ImageCapture {
        return ImageCapture
            .Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            //.setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(ROTATION_0)
                //usar outro callback no lugar desse, para separar a logica entre callback
                //de captura e preview
            .setUseCaseEventCallback(useCaseCallback)
            .build()
    }

    private fun getImageOutputOptions() : ImageCapture.OutputFileOptions {
        val name = FILENAME_FORMAT//SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        return ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
    }

    fun onTakePicture() {
        imageCapture.takePicture(
            getImageOutputOptions(),
            cameraThreadExecutor,
            imageCaptureCallbacks
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(view) { cameraState ->
            run {
                view.showCameraState(
                    cameraState.type.name.plus(
                        " - cameraId: ${Camera2CameraInfo.from(cameraInfo).cameraId}"
                    )
                )
                cameraState.error?.let {
                    view.showToast(it.code.toString())
                }
            }
        }
    }

    private companion object {
        const val FRONT_LEN = "Frontal"
        const val BACK_LEN = "Traseira"
        const val FILENAME_FORMAT = "AAAAAFILE"
    }
}