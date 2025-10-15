package dev.kadyko.mycurrency.domain.usercase

class RefreshCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(abbreviation: String) {
        repository.refreshCurrency(abbreviation)
    }
}