package dev.kadyko.mycurrency.ui.converter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.data.local.CurrencyEntity
import dev.kadyko.mycurrency.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConverterUiState(
    val availableCurrencies: List<CurrencyEntity> = emptyList(),
    val fromCurrency: String = "BYN",
    val toCurrency: String = "USD",
    val amountText: String = "100",
    val convertedResult: Double = 0.0
)

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialFrom: String = savedStateHandle.get<String>("from") ?: "BYN"
    private val initialTo: String = savedStateHandle.get<String>("to") ?: "USD"

    private val _fromCurrency = MutableStateFlow(initialFrom)
    private val _toCurrency = MutableStateFlow(initialTo)
    private val _amountText = MutableStateFlow("100")
    private val _convertedResult = MutableStateFlow(0.0)

    val uiState: StateFlow<ConverterUiState> = combine(
        repository.allCurrencies,
        _fromCurrency,
        _toCurrency,
        _amountText,
        _convertedResult
    ) { currencies, from, to, amount, result ->
        ConverterUiState(
            availableCurrencies = currencies,
            fromCurrency = from,
            toCurrency = to,
            amountText = amount,
            convertedResult = result
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConverterUiState()
    )

    init {
        calculateConversion()
    }

    fun onAmountChanged(newAmount: String) {
        _amountText.value = newAmount
        calculateConversion()
    }

    fun onFromCurrencySelected(currencyCode: String) {
        _fromCurrency.value = currencyCode
        calculateConversion()
    }

    fun onToCurrencySelected(currencyCode: String) {
        _toCurrency.value = currencyCode
        calculateConversion()
    }

    fun swapCurrencies() {
        val temp = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = temp
        calculateConversion()
    }

    private fun calculateConversion() {
        viewModelScope.launch {
            val amount = _amountText.value.toDoubleOrNull() ?: 0.0
            val result = repository.convertCurrency(
                fromCode = _fromCurrency.value,
                toCode = _toCurrency.value,
                amount = amount
            )
            _convertedResult.value = result
        }
    }
}
