package dev.kadyko.mycurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import dev.kadyko.mycurrency.domain.usecase.GetEurCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetRubCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetUsdCurrencyUseCase
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

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                // Запускаем fetch на IO Dispatcher
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val rubJob = async { repository.fetchAndSaveCurrency("RUB") }
                    val usdJob = async { repository.fetchAndSaveCurrency("USD") }
                    val eurJob = async { repository.fetchAndSaveCurrency("EUR") }

                    rubJob.await()
                    usdJob.await()
                    eurJob.await()
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
                _errorState.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}