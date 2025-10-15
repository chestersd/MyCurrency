package dev.kadyko.mycurrency.domain.usercase

class GetCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(abbreviation: String): Flow<Currency?> {
        return repository.getCurrency(abbreviation)
    }
}