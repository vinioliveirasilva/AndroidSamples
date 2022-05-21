package com.example.feature_advance_account.base

import android.content.Intent
import android.os.Parcelable

interface StepContract {
    interface Activity {
        fun enableNext()
        fun onNext()
        fun onNext(pairKeyValue: Pair<String, Parcelable?>)
        fun finish()
        fun onPrevious()
    }

    interface Fragment {
        fun onNext(keyValue: Pair<String, Parcelable?>)
        fun onNext()
    }

    interface Presenter {
        val keyValue: MutableMap<String, Parcelable?>
        fun init(flow: FlowEnum)
        fun onNext()
        fun onPrevious()
        fun parseKeyValue(keyValue: Pair<String, Parcelable?>)
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun enableNext()
    }

    interface StepEnum
    interface FlowEnum {
        val steps: List<StepEnum>
        fun nextStep(currentStep: StepEnum): StepEnum
        fun previousStep(currentStep: StepEnum): StepEnum
    }
}