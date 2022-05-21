package com.example.feature_advance_account.firstScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_advance_account.AdvanceAccountActivity
import com.example.feature_advance_account.base.StepActivity
import com.example.feature_advance_account.base.StepFragment
import com.example.feature_advance_account.databinding.FragmentFirstBinding

class FirstFragment : StepFragment() {

    lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? StepActivity)
            ?.setToolbarTitle("Primeiro fragmento")
            ?.showToolbarHome(true)
        binding.buttonFirst.setOnClickListener { onNext() }
    }
}