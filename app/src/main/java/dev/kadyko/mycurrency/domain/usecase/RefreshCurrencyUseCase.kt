package dev.kadyko.mycurrency.domain.usecase

import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import javax.inject.Inject

class RefreshCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(abbreviation: String) {
        repository.refreshCurrency(abbreviation)
    }
}