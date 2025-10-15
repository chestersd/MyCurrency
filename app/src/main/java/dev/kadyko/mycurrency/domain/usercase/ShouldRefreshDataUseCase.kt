package dev.kadyko.mycurrency.domain.usercase

class ShouldRefreshDataUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(abbreviation: String): Boolean {
        return repository.shouldRefreshData(abbreviation)
    }
}