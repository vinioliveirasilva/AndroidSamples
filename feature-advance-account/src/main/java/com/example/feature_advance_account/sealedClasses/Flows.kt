package com.example.feature_advance_account.sealedClasses

import com.example.feature_advance_account.base.StepContract

sealed class Flows(override val steps: List<Steps>) : StepContract.FlowEnum {
    object TestFlow : Flows(
        steps = listOf(
            Steps.FirstScreen,
            Steps.SecondScreen,
            Steps.Finish
        )
    )

    override fun nextStep(currentStep: StepContract.StepEnum): StepContract.StepEnum {
        val nextIndex = steps.indexOf(currentStep).inc()
        return if (nextIndex >= steps.size) {
            steps.last()
        } else {
            steps[nextIndex]
        }
    }

    override fun previousStep(currentStep: StepContract.StepEnum): StepContract.StepEnum {
        val nextIndex = steps.indexOf(currentStep).dec()
        return if (nextIndex < MIN_INDEX) {
            steps.first()
        } else {
            steps[nextIndex]
        }
    }

    private companion object {
        const val MIN_INDEX = 0
    }
}
