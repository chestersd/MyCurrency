package dev.kadyko.mycurrency.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val code: String,
    val name: String,
    val rate: Double,
    val baseCurrency: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)
