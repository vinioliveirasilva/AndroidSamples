package com.example.feature_camerax

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import org.koin.dsl.module

object CameraXModule {
    val instance = module {
        factory { (view: CameraXActivity) ->
            CameraXPresenter(
                view = view,
                cameraProvider = ProcessCameraProvider.getInstance(view),
                cameraThreadExecutor = ContextCompat.getMainExecutor(view)
            )
        }
    }
}