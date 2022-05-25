package com.example.feature_advance_account.secondScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_advance_account.base.StepFragment
import com.example.feature_advance_account.databinding.FragmentSecondBinding
import org.koin.core.module.Module
import org.koin.dsl.module

class SecondFragment : StepFragment() {

    lateinit var binding: FragmentSecondBinding

    override val modules: Module = module {  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepActivity
            ?.setToolbarTitle("Segundo fragmento")
            ?.showToolbarHome(true)

        binding.buttonSecond.setOnClickListener {
            onNext()
        }
    }
}