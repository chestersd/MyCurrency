package dev.kadyko.mycurrency.data.mapper

import dev.kadyko.mycurrency.data.remote.model.CurrencyResponse
import dev.kadyko.mycurrency.data.local.entity.CurrencyEntity
import dev.kadyko.mycurrency.domain.model.Currency

fun CurrencyResponse.toEntity(): CurrencyEntity {
    return CurrencyEntity(
        id = this.id,
        abbreviation = this.abbreviation,
        name = this.name,
        nameEng = this.nameEng,
        scale = this.scale,
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        quotName = this.quotName
    )
}

fun CurrencyEntity.toCurrency(): Currency {
    return Currency(
        id = this.id,
        abbreviation = this.abbreviation,
        name = this.name,
        nameEng = this.nameEng,
        scale = this.scale,
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        quotName = this.quotName
    )
}