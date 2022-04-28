package com.example.core_router.rule

import android.content.Context
import com.example.core_router.action.Action

interface ActionRule {
    fun shouldAllowNavigation(action: Action): Boolean
    fun onNotAllowedNavigation(context: Context, action: Action)
}

internal class StandardActionRule : ActionRule {
    override fun shouldAllowNavigation(action: Action): Boolean = true

    override fun onNotAllowedNavigation(context: Context, action: Action) = Unit
}
