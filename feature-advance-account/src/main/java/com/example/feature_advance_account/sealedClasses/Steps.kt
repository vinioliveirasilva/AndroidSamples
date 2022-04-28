package com.example.feature_advance_account.sealedClasses

import com.example.feature_advance_account.base.StepContract

sealed class Steps : StepContract.StepEnum {
    object FirstScreen : Steps()
    object SecondScreen : Steps()
    object Finish : Steps()
}
