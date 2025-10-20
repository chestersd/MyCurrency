package dev.kadyko.mycurrency.domain.usecase

import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRubCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<Currency?> {
        return repository.getCurrencyByAbbr("RUB")
    }
}