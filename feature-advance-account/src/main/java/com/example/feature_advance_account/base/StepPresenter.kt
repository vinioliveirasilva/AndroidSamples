package com.example.feature_advance_account.base

import android.os.Parcelable
import com.example.feature_advance_account.base.StepContract

abstract class StepPresenter: StepContract.Presenter {

    abstract var currentStep: StepContract.StepEnum
    abstract var currentFlow: StepContract.FlowEnum

    override val keyValue = mutableMapOf<String, Parcelable?>()

    override fun parseKeyValue(keyValue: Pair<String, Parcelable?>) {
        this.keyValue[keyValue.first] = keyValue.second
    }

    override fun onPrevious() {
        currentStep = currentFlow.previousStep(currentStep)
    }
}