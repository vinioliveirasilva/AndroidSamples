package com.example.currencyexchange.di

import com.example.currencyexchange.data.repositories.MainRepository
import com.example.currencyexchange.data.repositories.StandardMainRepository
import com.example.currencyexchange.data.service.CurrencyApi
import com.example.currencyexchange.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCurrencyApi() : CurrencyApi = Retrofit.Builder()
        .baseUrl("https://api.apilayer.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CurrencyApi::class.java)

    @Singleton
    @Provides
    fun provideMainRepository(api: CurrencyApi) : MainRepository = StandardMainRepository(api)

    @Singleton
    @Provides
    fun provideDispatchers() : DispatcherProvider = object : DispatcherProvider {
        override val main = Dispatchers.Main
        override val io = Dispatchers.IO
        override val default = Dispatchers.Default
        override val unconfined = Dispatchers.Unconfined
    }
}