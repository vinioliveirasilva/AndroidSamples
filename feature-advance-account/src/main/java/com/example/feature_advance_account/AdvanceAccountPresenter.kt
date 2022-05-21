package com.example.feature_advance_account

import android.app.Activity
import android.content.Intent
import com.example.core_router.FeatureRouter
import com.example.core_router.action.featureCamera.OpenCameraXAction
import com.example.feature_advance_account.base.StepContract
import com.example.feature_advance_account.base.StepPresenter
import com.example.feature_advance_account.sealedClasses.RequestCodes
import com.example.feature_advance_account.sealedClasses.Steps

class AdvanceAccountPresenter(
    private val view: AdvanceAccountActivity,
    private val router: FeatureRouter
) : StepPresenter() {

    override lateinit var currentStep: StepContract.StepEnum
    override lateinit var currentFlow: StepContract.FlowEnum

    override fun init(flow: StepContract.FlowEnum) {
        with(flow) {
            currentFlow = this
            currentStep = steps.first()
        }

        view.onNext()
    }

    override fun onNext() {
        when(currentStep) {
            is Steps.FirstScreen -> view.showFirstScreen()
            is Steps.SecondScreen -> view.showSecondScreen()
            is Steps.CameraBack -> openCameraBack()
            is Steps.CameraFront -> openCameraFront()
            is Steps.Finish -> view.finish()
        }

        currentStep = currentFlow.nextStep(currentStep)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(RequestCodes.toRequestCode(requestCode)) {
            is RequestCodes.CameraBack -> processCameraResult(resultCode, data)
            is RequestCodes.CameraFront -> processCameraResult(resultCode, data)
            else -> throw RuntimeException("Erro no resultado de uma activity")
        }
    }

    private fun processCameraResult(resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            onNext()
            ///validar
        } else {
            onPrevious()
        }
    }

    private fun openCameraBack() {
        router.startWithResult(
            receiver =  view,
            action = OpenCameraXAction,
            requestCode = RequestCodes.CameraBack.code,
            args = OpenCameraXAction.prepareParamsForBackLens()
        )
    }

    private fun openCameraFront() {
        router.startWithResult(
            receiver =  view,
            action = OpenCameraXAction,
            requestCode = RequestCodes.CameraFront.code,
            args = OpenCameraXAction.prepareParamsForFrontLens()
        )
    }
}