package com.example.feature_advance_account.base

import android.annotation.SuppressLint
import android.os.Parcelable
import com.example.feature_advance_account.base.StepContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

abstract class StepPresenter: StepContract.Presenter {

    abstract var currentStep: StepContract.StepEnum
    abstract var currentFlow: StepContract.FlowEnum

    override val keyValue = mutableMapOf<String, Parcelable?>()

    private var isNextStepEnabled = false

    override fun parseKeyValue(keyValue: Pair<String, Parcelable?>) {
        this.keyValue[keyValue.first] = keyValue.second
    }

    override fun onPrevious() {
        disableNext()
        currentStep = currentFlow.previousStep(currentStep)
    }

    override fun onNext() {
        if(isNextStepEnabled) {
            disableNext()
            throttleNextAction()
            return
        }
    }

    override fun enableNext() {
        isNextStepEnabled = true
    }

    private fun disableNext() {
        isNextStepEnabled = false
    }

    @SuppressLint("CheckResult")
    private fun throttleNextAction() {
        Observable.just("")
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onNext()
            }
    }
}