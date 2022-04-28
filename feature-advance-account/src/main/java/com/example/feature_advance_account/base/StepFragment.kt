package com.example.feature_advance_account.base

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

abstract class StepFragment(
) : Fragment(), StepContract.Fragment {

    //abstract val modules: Module
    val stepActivity = activity as? StepActivity

    override fun onNext() {
        (activity as? StepActivity)?.onNext()
    }

    override fun onNext(keyValue: Pair<String, Parcelable?>) {
        (activity as? StepActivity)?.onNext(keyValue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //loadKoinModules(modules)
        super.onCreate(savedInstanceState)
        (activity as? StepActivity)?.enableNext()
    }

    override fun onDestroy() {
        super.onDestroy()
        //unloadKoinModules(modules)
        (activity as? StepActivity)?.enableNext()
    }

    fun onBackPressed() {
        (activity as? StepActivity)?.onPrevious()
        //flowManager.closeCurrent()
    }
}