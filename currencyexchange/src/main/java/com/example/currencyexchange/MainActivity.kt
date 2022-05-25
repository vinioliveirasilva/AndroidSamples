package com.example.currencyexchange

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.currencyexchange.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        with(binding) {
            btConvert.setOnClickListener {
                viewModel.convert(
                    quantity.text.toString(),
                    inputCurrency.selectedItem.toString(),
                    outputCurrency.selectedItem.toString()
                )
            }

            lifecycleScope.launchWhenStarted {
                viewModel.conversion.collect { event ->
                    progressbar.isVisible = event is MainViewModel.CurrencyEvent.Loading
                    when(event) {
                        is MainViewModel.CurrencyEvent.Success -> {
                            textView2.text = event.resultText
                            textView2.setTextColor(Color.BLUE)
                        }
                        is MainViewModel.CurrencyEvent.Failure -> {
                            textView2.text = event.errorText
                            textView2.setTextColor(Color.RED)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}