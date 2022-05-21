package com.example.core_router

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.core_router.action.Action
import com.example.core_router.rule.ActionRule

typealias IntentParams = Bundle.() -> Unit

interface FeatureRouter {
    fun start(
        context: Context,
        action: Action,
        vararg flags: Int,
        args: IntentParams = {}
    )

    fun startAndFinish(
        receiver: Activity,
        action: Action,
        vararg flags: Int,
        args: IntentParams = {}
    )

    fun startAndFinish(
        receiver: Fragment,
        action: Action,
        vararg flags: Int,
        args: IntentParams = {}
    )

    fun startWithResult(
        receiver: Activity,
        action: Action,
        requestCode: Int,
        vararg flags: Int,
        args: IntentParams = {}
    )

    fun startWithResult(
        receiver: Fragment,
        action: Action,
        requestCode: Int,
        vararg flags: Int,
        args: IntentParams = {}
    )
}

internal class StandardFeatureRouter(private val actionRule: ActionRule) : FeatureRouter {

    override fun start(
        context: Context,
        action: Action,
        vararg flags: Int,
        args: IntentParams
    ) {
        if (actionRule.shouldAllowNavigation(action)) {
            context.startActivity(createIntent(context, action, flags, args))
        } else {
            actionRule.onNotAllowedNavigation(context, action)
        }
    }

    override fun startAndFinish(
        receiver: Activity,
        action: Action,
        vararg flags: Int,
        args: IntentParams
    ) {
        start(receiver, action, *flags, args = args)

        if (actionRule.shouldAllowNavigation(action)) {
            receiver.finish()
        }
    }

    override fun startAndFinish(
        receiver: Fragment,
        action: Action,
        vararg flags: Int,
        args: IntentParams
    ) {
        receiver.context?.run {
            start(this, action, *flags, args = args)
        } ?: run {
            throw IllegalStateException("The received fragment $this not attached to a context.")
        }

        if (actionRule.shouldAllowNavigation(action)) {
            receiver.activity?.finish()
        }

    }

    override fun startWithResult(
        receiver: Activity,
        action: Action,
        requestCode: Int,
        vararg flags: Int,
        args: IntentParams
    ) {
        if (actionRule.shouldAllowNavigation(action)) {
            receiver.run {
                startActivityForResult(
                    createIntent(this, action, flags, args),
                    requestCode
                )
            }
        } else {
            actionRule.onNotAllowedNavigation(receiver, action)
        }
    }

    override fun startWithResult(
        receiver: Fragment,
        action: Action,
        requestCode: Int,
        vararg flags: Int,
        args: IntentParams
    ) {
        if (actionRule.shouldAllowNavigation(action)) {
            receiver.run {
                context?.let { context ->
                    startActivityForResult(
                        createIntent(context, action, flags, args),
                        requestCode
                    )
                } ?: run {
                    throw IllegalStateException("The received fragment $this not attached to a context.")
                }
            }
        } else {
            receiver.context?.run {
                actionRule.onNotAllowedNavigation(this, action)
            }
        }
    }

    private fun createIntent(
        context: Context,
        action: Action,
        flags: IntArray? = null,
        args: IntentParams = {}
    ) = Intent(action.name)
        .putExtras(Bundle().apply(args))
        .setPackage(context.packageName)
        .apply {
            flags?.forEach { addFlags(it) }
        }
}
