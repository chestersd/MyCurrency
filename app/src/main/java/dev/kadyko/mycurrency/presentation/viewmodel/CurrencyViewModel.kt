package dev.kadyko.mycurrency.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import dev.kadyko.mycurrency.domain.usecase.GetEurCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetRubCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetUsdCurrencyUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val getRubCurrencyUseCase: GetRubCurrencyUseCase,
    private val getUsdCurrencyUseCase: GetUsdCurrencyUseCase,
    private val getEurCurrencyUseCase: GetEurCurrencyUseCase
) : ViewModel() {

    private val _rubState = MutableStateFlow<Currency?>(null)
    val rubState: StateFlow<Currency?> = _rubState

    private val _usdState = MutableStateFlow<Currency?>(null)
    val usdState: StateFlow<Currency?> = _usdState

    private val _eurState = MutableStateFlow<Currency?>(null)
    val eurState: StateFlow<Currency?> = _eurState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val currencyLoadExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("CurrencyViewModel", "Необработанное исключение в корутине загрузки валют", throwable)
        viewModelScope.launch {
            _errorState.value = throwable.message ?: "An unexpected error occurred"
            _isLoading.value = false
        }
    }

    fun retryLoadCurrencies() {
        _errorState.value = null
        loadCurrencies()
    }
    fun startInitialLoad() {
        loadCurrencies()
    }

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch(currencyLoadExceptionHandler) {
            _isLoading.value = true
            var fetchError: Throwable? = null

            try {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val rubJob = async {
                        try {
                            repository.fetchAndSaveCurrency("RUB")
                        } catch (e: Exception) {
                            Log.e("CurrencyViewModel", "Ошибка при загрузке RUB", e)
                            e
                        }
                    }
                    val usdJob = async {
                        try {
                            repository.fetchAndSaveCurrency("USD")
                        } catch (e: Exception) {
                            Log.e("CurrencyViewModel", "Ошибка при загрузке USD", e)
                            e
                        }
                    }
                    val eurJob = async {
                        try {
                            repository.fetchAndSaveCurrency("EUR")
                        } catch (e: Exception) {
                            Log.e("CurrencyViewModel", "Ошибка при загрузке EUR", e)
                            e
                        }
                    }

                    val results = listOf(rubJob, usdJob, eurJob).map { it.await() }
                    val exceptionResult = results.find { it is Exception }
                    if (exceptionResult != null) {
                        throw exceptionResult as Throwable
                    }
                }

                launch {
                    getRubCurrencyUseCase().collect { _rubState.value = it }
                }
                launch {
                    getUsdCurrencyUseCase().collect { _usdState.value = it }
                }
                launch {
                    getEurCurrencyUseCase().collect { _eurState.value = it }
                }
            } catch (e: Exception) {
                Log.e("CurrencyViewModel", "Ошибка в основной корутине (не в async или await)", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSingleCurrency(currencyCode: String) {
        viewModelScope.launch(currencyLoadExceptionHandler) {
            _errorState.value = null

            try {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    repository.fetchAndSaveCurrency(currencyCode)
                }
                Log.d("CurrencyViewModel", "Успешно обновлена валюта: $currencyCode")
            } catch (e: Exception) {
                Log.e("CurrencyViewModel", "Ошибка при обновлении валюты $currencyCode", e)
            }
        }
    }

    fun getStateForCurrency(currencyCode: String): StateFlow<Currency?> {
        return when (currencyCode) {
            "RUB" -> rubState
            "USD" -> usdState
            "EUR" -> eurState
            else -> throw IllegalArgumentException("Unknown currency code: $currencyCode")
        }
    }
}