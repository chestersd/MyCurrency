package dev.kadyko.mycurrency.ui.rates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.kadyko.mycurrency.R
import dev.kadyko.mycurrency.data.local.CurrencyEntity

@Composable
fun RatesScreen(
    viewModel: RatesViewModel,
    onCurrencyClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(R.string.base_currency)}: ${state.baseCurrency}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp).width(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.currencies.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No rates found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.currencies, key = { it.code }) { currency ->
                    CurrencyItem(
                        currency = currency,
                        onFavoriteClick = {
                            viewModel.toggleFavorite(currency.code, !currency.isFavorite)
                        },
                        onItemClick = { onCurrencyClick(currency.code) }
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyItem(
    currency: CurrencyEntity,
    onFavoriteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    val costInBase = if (currency.code == currency.baseCurrency) {
        1.0
    } else if (currency.rate > 0) {
        1.0 / currency.rate
    } else {
        0.0
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    val rateText = if (currency.code == currency.baseCurrency) {
                        "1.0000"
                    } else {
                        String.format("%.4f %s", costInBase, currency.baseCurrency)
                    }

                    Text(
                        text = rateText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (currency.code != currency.baseCurrency && currency.rate > 0) {
                        Text(
                            text = "1 ${currency.baseCurrency} = ${String.format("%.4f", currency.rate)} ${currency.code}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (currency.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (currency.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
