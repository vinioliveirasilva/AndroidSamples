package br.com.uol.ps.myaccount.core.router.rule

import android.content.Context
import br.com.uol.ps.myaccount.core.router.actions.Action

interface ActionRule {
    fun shouldAllowNavigation(action: Action): Boolean
    fun onNotAllowedNavigation(context: Context, action: Action)
}

internal class StandardActionRule : ActionRule {
    override fun shouldAllowNavigation(action: Action): Boolean = true

    override fun onNotAllowedNavigation(context: Context, action: Action) = Unit
}
