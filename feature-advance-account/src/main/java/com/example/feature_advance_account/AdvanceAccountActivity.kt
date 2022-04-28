package com.example.feature_advance_account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.core_router.di.RouterModule
import com.example.feature_advance_account.base.StepActivity
import com.example.feature_advance_account.base.StepFragment
import com.example.feature_advance_account.databinding.ActivityAdvanceAccountBinding
import com.example.feature_advance_account.firstScreen.FirstFragment
import com.example.feature_advance_account.sealedClasses.Flows
import com.example.feature_advance_account.secondScreen.SecondFragment
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf

class AdvanceAccountActivity : StepActivity() {

    override val presenter: AdvanceAccountPresenter by inject {
        parametersOf(this)
    }

    override val modules: List<Module> = listOf(
        AdvanceAccountModule.instance,
        RouterModule.instance
    )

    private lateinit var fragContainer: ConstraintLayout

    private val tag = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(ActivityAdvanceAccountBinding.inflate(layoutInflater)) {
            setContentView(root)

            fragContainer = fragmentContainer

            fab.setOnClickListener { view ->
                Snackbar
                    .make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            }
        }

        presenter.init(Flows.TestFlow)
    }

    fun showFirstScreen() = addFragment(FirstFragment())
    fun showSecondScreen() = addFragment(SecondFragment())

    fun setToolbarTitle(title: String) = apply {
        supportActionBar?.title = title
    }

    fun showToolbarHome(shouldShow: Boolean) = apply {
        supportActionBar?.setDisplayHomeAsUpEnabled(shouldShow)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun addFragment(frag: StepFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                fragContainer.id,
                frag
            )
            .addToBackStack(tag)
            .commit()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, AdvanceAccountActivity::class.java)
    }
}