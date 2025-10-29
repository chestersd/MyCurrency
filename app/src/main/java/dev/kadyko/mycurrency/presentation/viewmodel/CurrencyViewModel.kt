package dev.kadyko.mycurrency.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import dev.kadyko.mycurrency.domain.usecase.GetEurCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetRubCurrencyUseCase
import dev.kadyko.mycurrency.domain.usecase.GetUsdCurrencyUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val getRubCurrencyUseCase: GetRubCurrencyUseCase,
    private val getUsdCurrencyUseCase: GetUsdCurrencyUseCase,
    private val getEurCurrencyUseCase: GetEurCurrencyUseCase
) : ViewModel() {

    private val _rubState = MutableStateFlow<Currency?>(null)
    val rubState: StateFlow<Currency?> = _rubState

    private val _usdState = MutableStateFlow<Currency?>(null)
    val usdState: StateFlow<Currency?> = _usdState

    private val _eurState = MutableStateFlow<Currency?>(null)
    val eurState: StateFlow<Currency?> = _eurState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val currencyLoadExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("CurrencyViewModel", "Необработанное исключение в корутине загрузки валют", throwable)
        viewModelScope.launch {
            // Обновляем состояние ошибки в UI
            _errorState.value = throwable.message ?: "An unexpected error occurred"
            _isLoading.value = false
        }
    }

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch(currencyLoadExceptionHandler) { // <-- Передаём обработчик
            _isLoading.value = true
            _errorState.value = null // Сбрасываем предыдущую ошибку
            var fetchError: Throwable? = null // <-- Переменная для хранения ошибки

            try {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val rubJob = async {
                        try {
                            repository.fetchAndSaveCurrency("RUB")
                        } catch (e: Exception) {
                            // Логируем ошибку конкретной задачи
                            Log.e("CurrencyViewModel", "Ошибка при загрузке RUB", e)
                            // Не выбрасываем исключение, а сохраняем его
                            e // <-- Возвращаем исключение из async задачи
                        }
                    }
                    val usdJob = async {
                        try {
                            repository.fetchAndSaveCurrency("USD")
                        } catch (e: Exception) {
                            Log.e("CurrencyViewModel", "Ошибка при загрузке USD", e)
                            e // <-- Возвращаем исключение из async задачи
                        }
                    }
                    val eurJob = async {
                        try {
                            repository.fetchAndSaveCurrency("EUR")
                        } catch (e: Exception) {
                            Log.e("CurrencyViewModel", "Ошибка при загрузке EUR", e)
                            e // <-- Возвращаем исключение из async задачи
                        }
                    }

                    // Ожидаем завершения всех задач и проверяем результаты
                    val results = listOf(rubJob, usdJob, eurJob).map { it.await() }
                    // Проверяем, было ли возвращено исключение из какой-либо задачи
                    val exceptionResult = results.find { it is Exception }
                    if (exceptionResult != null) {
                        // Если да, выбрасываем его, чтобы перехватить в currencyLoadExceptionHandler
                        throw exceptionResult as Throwable
                    }
                }

                // Подписки на Flow в основном потоке (только если загрузка прошла успешно)
                // Запускаем в отдельных корутинах, чтобы они не влияли на основную через currencyLoadExceptionHandler
                launch {
                    getRubCurrencyUseCase().collect { _rubState.value = it }
                }
                launch {
                    getUsdCurrencyUseCase().collect { _usdState.value = it }
                }
                launch {
                    getEurCurrencyUseCase().collect { _eurState.value = it }
                }
            } catch (e: Exception) {
                // Этот catch НЕ должен сработать, если исключение правильно выбрасывается из async задач.
                // Но если что-то пойдёт не так в основном блоке launch (не в async), оно сюда попадёт.
                // Однако, основная ошибка будет обработана в currencyLoadExceptionHandler.
                // Мы можем оставить его, но он будет ловить ошибки *вне* async задач и await.
                Log.e("CurrencyViewModel", "Ошибка в основной корутине (не в async или await)", e)
                // Не вызываем throw e, так как currencyLoadExceptionHandler уже установлен для launch
                // и ошибка из async задач выше выбросится туда напрямую.
            } finally {
                // Убедимся, что индикатор загрузки скрыт В ЛЮБОМ СЛУЧАЕ
                // Этот блок finally выполнится, даже если исключение не было перехвачено в catch выше
                // и уйдёт в currencyLoadExceptionHandler.
                // Однако, если исключение выброшено в launch, и currencyLoadExceptionHandler его обрабатывает,
                // то основная корутина launch завершится, и finally выполнится.
                // Но если ошибка происходит в async задаче и выбрасывается через throw e из async,
                // она "выходит" из await и попадает в launch, где currencyLoadExceptionHandler её ловит.
                // В этом случае finally в launch всё равно должен выполниться.
                _isLoading.value = false
            }
        }
    }

    fun loadSingleCurrency(currencyCode: String) {
        viewModelScope.launch(currencyLoadExceptionHandler) {
            _errorState.value = null // Сбросить ошибку перед обновлением
            try {
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    repository.fetchAndSaveCurrency(currencyCode)
                }
                // Подписка на обновление конкретной валюты не нужна,
                // так как Flow автоматически обновится и UI перерисуется
                // Мы можем обновить соответствующий StateFlow, чтобы показать, что обновление прошло.
                // Но Flow уже делает это за нас.
                // Если нужно, можно добавить индикатор "обновлено", но это необязательно.
                Log.d("CurrencyViewModel", "Успешно обновлена валюта: $currencyCode")
            } catch (e: Exception) {
                // Ошибка будет перехвачена currencyLoadExceptionHandler
                Log.e("CurrencyViewModel", "Ошибка при обновлении валюты $currencyCode", e)
                // _errorState будет обновлён в обработчике
            }
        }
    }
    // ---

    // --- Функция для получения конкретного состояния ---
    fun getStateForCurrency(currencyCode: String): StateFlow<Currency?> {
        return when (currencyCode) {
            "RUB" -> rubState
            "USD" -> usdState
            "EUR" -> eurState
            else -> throw IllegalArgumentException("Unknown currency code: $currencyCode")
        }
    }
}