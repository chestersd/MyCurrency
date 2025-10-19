package dev.kadyko.mycurrency.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey
    val id: Int,
    val abbreviation: String,
    val name: String,
    val nameEng: String,
    val scale: Int,
    val dateStart: String,
    val dateEnd: String,
    val quotName: String,
    val lastUpdated: Long = System.currentTimeMillis()
)