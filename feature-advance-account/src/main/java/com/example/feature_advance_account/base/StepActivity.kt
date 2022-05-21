package com.example.feature_advance_account.base

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

abstract class StepActivity: AppCompatActivity(), StepContract.Activity {

    abstract val presenter: StepContract.Presenter

    abstract val modules: List<Module>

    override fun onCreate(savedInstanceState: Bundle?) {
        loadKoinModules(modules)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(modules)
    }

    override fun onNext() {
        presenter.onNext()
    }

    override fun onPrevious() {
        presenter.onPrevious()
    }

    override fun enableNext() {
        presenter.enableNext()
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

    fun setToolbarTitle(title: String) = apply {
        supportActionBar?.title = title
    }

    fun showToolbarHome(shouldShow: Boolean) = apply {
        supportActionBar?.setDisplayHomeAsUpEnabled(shouldShow)
    }
}