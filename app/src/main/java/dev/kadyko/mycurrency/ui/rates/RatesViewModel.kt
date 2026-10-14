package dev.kadyko.mycurrency.ui.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.data.local.CurrencyEntity
import dev.kadyko.mycurrency.data.preferences.UserPreferences
import dev.kadyko.mycurrency.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RatesUiState(
    val currencies: List<CurrencyEntity> = emptyList(),
    val searchQuery: String = "",
    val baseCurrency: String = "BYN",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RatesUiState> = combine(
        repository.allCurrencies,
        _searchQuery,
        userPreferences.baseCurrency,
        _isLoading,
        _errorMessage
    ) { currencies, query, base, isLoading, error ->
        val filtered = if (query.isBlank()) {
            currencies
        } else {
            currencies.filter {
                it.code.contains(query, ignoreCase = true) ||
                        it.name.contains(query, ignoreCase = true)
            }
        }
        RatesUiState(
            currencies = filtered,
            searchQuery = query,
            baseCurrency = base,
            isLoading = isLoading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RatesUiState()
    )

    init {
        viewModelScope.launch {
            userPreferences.baseCurrency.collectLatest { base ->
                refreshRates(base)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun refreshRates(base: String = uiState.value.baseCurrency) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.refreshRates(base)
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to update rates"
            }
        }
    }

    fun toggleFavorite(currencyCode: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(currencyCode, isFavorite)
        }
    }
}
