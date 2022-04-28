package com.example.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.feature_advance_account.AdvanceAccountActivity
import com.example.sample.databinding.ActivityMainBinding
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var actionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            modules(listOf())
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        actionButton = binding.dummyButton

        actionButton.setOnClickListener {
            startActivity(
                AdvanceAccountActivity.newIntent(this)
            )
        }
    }
}