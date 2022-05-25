package com.example.currencyexchange.data.models

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Float>
)