package com.example.core_router.di

import com.example.core_router.FeatureRouter
import com.example.core_router.StandardFeatureRouter
import com.example.core_router.rule.ActionRule
import com.example.core_router.rule.StandardActionRule
import org.koin.dsl.module

object RouterModule {
    val instance = module {
        factory<ActionRule> { StandardActionRule() }
        factory<FeatureRouter> { StandardFeatureRouter(actionRule = get()) }
    }
}
