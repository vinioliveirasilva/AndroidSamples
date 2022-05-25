package com.example.currencyexchange

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.currencyexchange.databinding.ActivityMainBinding
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp()
class MainApplication : Application()