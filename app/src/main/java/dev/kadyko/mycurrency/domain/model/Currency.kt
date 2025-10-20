package dev.kadyko.mycurrency.domain.model

data class Currency(
    val abbreviation: String,
    val name: String,
    val quotName: String,
    val scale: Int,
    val officialRate: Double
)