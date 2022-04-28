package com.example.feature_advance_account.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

abstract class StepActivity: AppCompatActivity(), StepContract.Activity {

    abstract val presenter: StepContract.Presenter

    abstract val modules: List<Module>

    private var isNextStepEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        loadKoinModules(modules)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(modules)
    }

    override fun onNext() {
        if(isNextStepEnabled) {
            disableNext()
            throttleNextAction()
        }
    }

    override fun onPrevious() {
        disableNext()
        presenter.onPrevious()
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
                presenter.onNext()
            }
    }

    override fun enableNext() {
        isNextStepEnabled = true
    }

    override fun onNext(pairKeyValue: Pair<String, Parcelable?>) {
        presenter.parseKeyValue(pairKeyValue)
        presenter.onNext()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        onPrevious()
        with(supportFragmentManager) {
            if(backStackEntryCount > 1) {
                popBackStack()
                beginTransaction().remove(fragments.last()).commit()
            } else {
                finish()
                overridePendingTransition(
                    androidx.transition.R.anim.abc_fade_in,
                    androidx.transition.R.anim.abc_fade_out
                )
            }
        }
    }
}