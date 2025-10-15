package dev.kadyko.mycurrency.domain.model

data class Currency(
    val id: Int,
    val abbreviation: String,
    val name: String,
    val nameEng: String,
    val scale: Int,
    val dateStart: String,
    val dateEnd: String,
    val quotName: String
)