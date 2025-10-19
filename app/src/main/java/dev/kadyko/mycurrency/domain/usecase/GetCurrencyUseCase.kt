package dev.kadyko.mycurrency.domain.usecase

import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(abbreviation: String): Flow<Currency?> {
        return repository.getCurrency(abbreviation)
    }
}