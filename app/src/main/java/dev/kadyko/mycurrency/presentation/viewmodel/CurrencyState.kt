package dev.kadyko.mycurrency.presentation.viewmodel

import dev.kadyko.mycurrency.domain.model.Currency

data class CurrencyState(
    val currency: Currency? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)