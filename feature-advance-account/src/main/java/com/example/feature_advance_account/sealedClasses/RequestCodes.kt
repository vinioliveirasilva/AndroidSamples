package com.example.feature_advance_account.sealedClasses

sealed class RequestCodes(val code: Int) {
    object CameraBack: RequestCodes(CAMERA_BACK)
    object CameraFront: RequestCodes(CAMERA_FRONT)
    object Unknown: RequestCodes(UNKNOWN)

    companion object {
        private const val CAMERA_BACK: Int = 100
        private const val CAMERA_FRONT: Int = 101
        private const val UNKNOWN: Int = 199

        fun toRequestCode(code: Int) = when (code) {
            CAMERA_BACK -> CameraBack
            CAMERA_FRONT -> CameraFront
            else -> Unknown
        }
    }
}