package dev.kadyko.mycurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kadyko.mycurrency.domain.usecase.GetCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.RefreshCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.ShouldRefreshDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val refreshCurrencyUseCase: RefreshCurrencyUseCase,
    private val shouldRefreshDataUseCase: ShouldRefreshDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CurrencyState())
    val state: StateFlow<CurrencyState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CurrencyEvent>()
    val events: SharedFlow<CurrencyEvent> = _events.asSharedFlow()

    fun loadCurrency(abbreviation: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Проверяем, нужно ли обновить данные
                if (shouldRefreshDataUseCase(abbreviation)) {
                    refreshCurrencyUseCase(abbreviation)
                }

                getCurrencyUseCase(abbreviation).collect { currency ->
                    _state.update {
                        it.copy(
                            currency = currency,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                _events.emit(CurrencyEvent.ShowError(e.message ?: "Failed to load data"))
            }
        }
    }

    fun refresh(abbreviation: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                refreshCurrencyUseCase(abbreviation)
                _events.emit(CurrencyEvent.DataRefreshed)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to refresh data"
                    )
                }
                _events.emit(CurrencyEvent.ShowError("Failed to refresh data"))
            }
        }
    }
}
