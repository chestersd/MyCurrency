package dev.kadyko.mycurrency.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "currency_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCurrencyApi(): dev.kadyko.mycurrency.data.remote.api.CurrencyApiService {
        return Retrofit.Builder()
            .baseUrl(dev.kadyko.mycurrency.data.remote.api.CurrencyApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(dev.kadyko.mycurrency.data.remote.api.CurrencyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyDatabase(@ApplicationContext context: Context): dev.kadyko.mycurrency.data.local.database.CurrencyDatabase {
        return dev.kadyko.mycurrency.data.local.database.CurrencyDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(database: dev.kadyko.mycurrency.data.local.database.CurrencyDatabase): dev.kadyko.mycurrency.data.local.dao.CurrencyDao {
        return database.currencyDao()
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        apiService: dev.kadyko.mycurrency.data.remote.api.CurrencyApiService,
        currencyDao: dev.kadyko.mycurrency.data.local.dao.CurrencyDao,
        preferences: DataStore<Preferences>
    ): dev.kadyko.mycurrency.domain.repository.CurrencyRepository {
        return dev.kadyko.mycurrency.data.repository.CurrencyRepositoryImpl(apiService, currencyDao, preferences)
    }

    @Provides
    @Singleton
    fun provideGetCurrencyUseCase(repository: dev.kadyko.mycurrency.domain.repository.CurrencyRepository): dev.kadyko.mycurrency.domain.usecase.GetCurrencyUseCase {
        return dev.kadyko.mycurrency.domain.usecase.GetCurrencyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshCurrencyUseCase(repository: dev.kadyko.mycurrency.domain.repository.CurrencyRepository): dev.kadyko.mycurrency.domain.usecase.RefreshCurrencyUseCase {
        return dev.kadyko.mycurrency.domain.usecase.RefreshCurrencyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideShouldRefreshDataUseCase(repository: dev.kadyko.mycurrency.domain.repository.CurrencyRepository): dev.kadyko.mycurrency.domain.usecase.ShouldRefreshDataUseCase {
        return dev.kadyko.mycurrency.domain.usecase.ShouldRefreshDataUseCase(repository)
    }
}
//package dev.kadyko.mycurrency.di
//
//import android.content.Context
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
//import dev.kadyko.mycurrency.data.local.dao.CurrencyDao
//import dev.kadyko.mycurrency.data.local.database.CurrencyDatabase
//import dev.kadyko.mycurrency.data.remote.api.CurrencyApiService
//import dev.kadyko.mycurrency.data.repository.CurrencyRepositoryImpl
//import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
//import dev.kadyko.mycurrency.domain.usecase.GetCurrencyUseCase
//import dev.kadyko.mycurrency.domain.usecase.RefreshCurrencyUseCase
//import dev.kadyko.mycurrency.domain.usecase.ShouldRefreshDataUseCase
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//import javax.inject.Singleton
//
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "currency_preferences")
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideCurrencyApi(): CurrencyApiService {
//        return Retrofit.Builder()
//            .baseUrl(CurrencyApiService.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(
//                OkHttpClient.Builder()
//                    .addInterceptor(HttpLoggingInterceptor().apply {
//                        level = HttpLoggingInterceptor.Level.BASIC
//                    })
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build()
//            )
//            .build()
//            .create(CurrencyApiService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideCurrencyDatabase(@ApplicationContext context: Context): CurrencyDatabase {
//        return CurrencyDatabase.getInstance(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideCurrencyDao(database: CurrencyDatabase): CurrencyDao {
//        return database.currencyDao()
//    }
//
//    @Provides
//    @Singleton
//    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
//        return PreferenceDataStoreFactory.create(
//            produceFile = { context.preferencesDataStoreFile("currency_preferences") }
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideCurrencyRepository(
//        apiService: CurrencyApiService,
//        currencyDao: CurrencyDao,
//        preferences: DataStore<Preferences>
//    ): CurrencyRepository {
//        return CurrencyRepositoryImpl(apiService, currencyDao, preferences)
//    }
//
//    @Provides
//    @Singleton
//    fun provideGetCurrencyUseCase(repository: CurrencyRepository): GetCurrencyUseCase {
//        return GetCurrencyUseCase(repository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideRefreshCurrencyUseCase(repository: CurrencyRepository): RefreshCurrencyUseCase {
//        return RefreshCurrencyUseCase(repository)
//    }
//
//    @Provides
//    @Singleton
//    fun provideShouldRefreshDataUseCase(repository: CurrencyRepository): ShouldRefreshDataUseCase {
//        return ShouldRefreshDataUseCase(repository)
//    }
//}
