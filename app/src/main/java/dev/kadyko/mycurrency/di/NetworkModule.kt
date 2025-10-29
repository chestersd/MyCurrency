package dev.kadyko.mycurrency.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.kadyko.mycurrency.data.local.CurrencyDatabase
import dev.kadyko.mycurrency.util.Constants // <-- Импорт
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideCurrencyApiService(retrofit: Retrofit) = retrofit.create(dev.kadyko.mycurrency.data.remote.CurrencyApiService::class.java) // Полный путь

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        CurrencyDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideCurrencyDao(database: CurrencyDatabase) = database.currencyDao()
}