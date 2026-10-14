package dev.kadyko.mycurrency.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.data.local.CurrencyEntity
import dev.kadyko.mycurrency.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    val favorites: StateFlow<List<CurrencyEntity>> = repository.favoriteCurrencies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeFromFavorites(currencyCode: String) {
        viewModelScope.launch {
            repository.toggleFavorite(currencyCode, false)
        }
    }
}
