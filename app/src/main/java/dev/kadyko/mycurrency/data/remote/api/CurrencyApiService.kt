package dev.kadyko.mycurrency.data.remote.api

import dev.kadyko.mycurrency.data.remote.model.CurrencyResponse
import retrofit2.http.GET

interface CurrencyApiService {

    @GET("exrates/currencies/451") // EUR
    suspend fun getEurCurrency(): CurrencyResponse

    @GET("exrates/currencies/456") // RUB
    suspend fun getRubCurrency(): CurrencyResponse

    @GET("exrates/currencies/431") // USD
    suspend fun getUsdCurrency(): CurrencyResponse

    companion object {
        const val BASE_URL = "https://api.nbrb.by/"
    }
}