package dev.kadyko.mycurrency.presentation.viewmodel

sealed class CurrencyEvent {
    object DataRefreshed : CurrencyEvent()
    data class ShowError(val message: String) : CurrencyEvent()
}
