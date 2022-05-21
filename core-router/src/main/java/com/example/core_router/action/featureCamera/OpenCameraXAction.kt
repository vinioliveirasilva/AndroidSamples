package com.example.core_router.action.featureCamera

import android.os.Bundle
import com.example.core_router.action.Action

object OpenCameraXAction : Action {
    override val name: String = "com.example.camerax.open"

    fun prepareParamsForFrontLens(
    ): Bundle.() -> Unit {
        return {
            putInt(Keys.CAMERA_LENS, CAMERA_FRONT)
        }
    }
    fun prepareParamsForBackLens(
    ): Bundle.() -> Unit {
        return {
            putInt(Keys.CAMERA_LENS, CAMERA_BACK)
        }
    }

    object Keys {
        const val CAMERA_LENS = "CAMERA_LENS"
    }

    private const val CAMERA_BACK = 1
    private const val CAMERA_FRONT = 0
}