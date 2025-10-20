package dev.kadyko.mycurrency.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.presentation.viewmodel.CurrencyEvent
import dev.kadyko.mycurrency.presentation.viewmodel.CurrencyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    currencyType: String,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
//    val scaffoldState = rememberScaffoldState()
    val events by viewModel.events.collectAsState(initial = null)
    val context = LocalContext.current
    val scaffoldState = remember { SnackbarHostState() }


    LaunchedEffect(currencyType) {
        viewModel.loadCurrency(currencyType)
    }

    LaunchedEffect(events) {
        when (events) {
            is CurrencyEvent.ShowError -> {
                val errorMessage = (events as CurrencyEvent.ShowError).message
                scaffoldState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
            }
            is CurrencyEvent.DataRefreshed -> {
                scaffoldState.showSnackbar(
                    message = "Data refreshed successfully",
                    duration = SnackbarDuration.Short
                )
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Курс $currencyType") },
                actions = {
                    IconButton(
                        onClick = { viewModel.refresh(currencyType) },
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading && state.currency == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null && state.currency == null -> {
                ErrorView(
                    error = state.error!!,
                    onRetry = { viewModel.loadCurrency(currencyType) },
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                state.currency?.let { currency ->
                    CurrencyContent(
                        currency = currency,
                        modifier = Modifier.padding(padding)
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No data available")
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyContent(currency: Currency, modifier: Modifier = Modifier) {
    val currencyInfo = listOf(
        "Валюта" to currency.name to currency.nameEng,
        "Код валюты" to currency.abbreviation to null,
        "Масштаб" to "${currency.scale} ${currency.quotName}" to null,
        "Действует с" to formatDate(currency.dateStart) to null,
        "Действует до" to formatDate(currency.dateEnd) to null
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(currencyInfo) { (title, value, subtitle) ->
            CurrencyCard(
                title = title,
                value = value,
                subtitle = subtitle
            )
        }
    }
}

@Composable
fun CurrencyCard(title: String, value: String, subtitle: String? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ErrorView(error: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "Ошибка",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Повторить")
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

data class CurrencyDisplayInfo(
    val title: String,
    val value: String,
    val subtitle: String? = null
)