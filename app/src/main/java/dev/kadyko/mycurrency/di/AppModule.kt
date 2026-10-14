package dev.kadyko.mycurrency.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.kadyko.mycurrency.data.local.CurrencyDao
import dev.kadyko.mycurrency.data.local.CurrencyDatabase
import dev.kadyko.mycurrency.data.remote.CurrencyApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCurrencyDatabase(
        @ApplicationContext context: Context
    ): CurrencyDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyDatabase::class.java,
            "currency_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(database: CurrencyDatabase): CurrencyDao {
        return database.currencyDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi {
        return Retrofit.Builder()
            .baseUrl(CurrencyApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
