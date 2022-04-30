package com.example.feature_camerax

import androidx.camera.lifecycle.ProcessCameraProvider
import org.koin.dsl.module

object CameraXModule {
    val instance = module {
        factory { (view: CameraXActivity) ->
            CameraXPresenter(
                view = view,
                cameraProvider = ProcessCameraProvider.getInstance(view)
            )
        }
    }
}