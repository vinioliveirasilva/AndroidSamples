package com.example.currencyexchange.data.repositories

import com.example.currencyexchange.data.models.CurrencyResponse
import com.example.currencyexchange.data.service.CurrencyApi
import com.example.currencyexchange.util.Resource
import java.lang.Exception
import javax.inject.Inject

class StandardMainRepository @Inject constructor (
    private val api: CurrencyApi
) : MainRepository {
    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(base = base)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("vish deu treta ${response.message() ?: ""}")
            }
        } catch (e: Exception) {
            Resource.Error("vish deu treta ${e.message}")
        }
    }
}