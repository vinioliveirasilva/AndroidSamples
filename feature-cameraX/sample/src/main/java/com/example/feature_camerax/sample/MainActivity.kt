package com.example.feature_camerax.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.feature_camerax.CameraXActivity
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startKoin {
            modules(listOf())
        }

        findViewById<Button>(R.id.call_module).setOnClickListener {
            startActivity(CameraXActivity.newIntent(this))
        }
    }
}