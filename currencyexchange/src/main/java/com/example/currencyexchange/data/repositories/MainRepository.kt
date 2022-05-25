package com.example.currencyexchange.data.repositories

import com.example.currencyexchange.data.models.CurrencyResponse
import com.example.currencyexchange.util.Resource

interface MainRepository {
    suspend fun getRates(base: String) : Resource<CurrencyResponse>
}