package com.example.feature_advance_account

import org.koin.dsl.module

object AdvanceAccountModule {
    val instance = module {
        factory { (view: AdvanceAccountActivity) ->
            AdvanceAccountPresenter(
                view = view
            )
        }
    }
}