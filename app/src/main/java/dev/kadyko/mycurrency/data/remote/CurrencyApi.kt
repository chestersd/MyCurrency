package dev.kadyko.mycurrency.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {

    @GET("v6/latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") baseCurrency: String = "USD"
    ): CurrencyResponseDto

    companion object {
        const val BASE_URL = "https://open.er-api.com/"
    }
}
