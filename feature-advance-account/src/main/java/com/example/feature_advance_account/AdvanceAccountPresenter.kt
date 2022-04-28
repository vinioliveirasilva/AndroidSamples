package com.example.feature_advance_account

import com.example.feature_advance_account.base.StepContract
import com.example.feature_advance_account.base.StepPresenter
import com.example.feature_advance_account.sealedClasses.Steps

class AdvanceAccountPresenter(
    private val view: AdvanceAccountActivity
) : StepPresenter() {

    override lateinit var currentStep: StepContract.StepEnum
    override lateinit var currentFlow: StepContract.FlowEnum

    override fun init(flow: StepContract.FlowEnum) {
        with(flow) {
            currentFlow = this
            currentStep = steps.first()
        }

        onNext()
    }

    override fun onNext() {
        when(currentStep) {
            is Steps.FirstScreen -> view.showFirstScreen()
            is Steps.SecondScreen -> view.showSecondScreen()
            is Steps.Finish -> view.finish()
        }

        currentStep = currentFlow.nextStep(currentStep)
    }
}