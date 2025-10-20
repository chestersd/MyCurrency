package dev.kadyko.mycurrency.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("exrates/currencies/{id}")
    suspend fun getCurrencyById(@Path("id") id: Int): RemoteCurrencyDto
}