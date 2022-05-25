package com.example.currencyexchange.data.service

import com.example.currencyexchange.data.models.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/exchangerates_data/latest")
    suspend fun getRates(
        @Header("apikey") apiKey: String = "nTZZV26Q03vIKvLy2ayb0aoC3A20Ftpo",
        @Query("base") base:String
    ) : Response<CurrencyResponse>
}