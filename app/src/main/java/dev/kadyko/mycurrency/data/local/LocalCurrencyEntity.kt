package dev.kadyko.mycurrency.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class LocalCurrencyEntity(
    @PrimaryKey val abbreviation: String,
    val name: String,
    val quotName: String,
    val scale: Int,
    val officialRate: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)