package dev.kadyko.mycurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import dev.kadyko.mycurrency.domain.usecase.GetEurCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetRubCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetUsdCurrencyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    init {
        loadCurrency(451, "RUB")
        loadCurrency(456, "USD")
        loadCurrency(431, "EUR")
    }

    private fun loadCurrency(id: Int, abbr: String) {
        viewModelScope.launch {
            repository.fetchAndSaveCurrency(id, abbr)
            when (abbr) {
                "RUB" -> getRubCurrencyUseCase().collect { _rubState.value = it }
                "USD" -> getUsdCurrencyUseCase().collect { _usdState.value = it }
                "EUR" -> getEurCurrencyUseCase().collect { _eurState.value = it }
            }
        }
    }
}