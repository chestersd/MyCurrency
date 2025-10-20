package dev.kadyko.mycurrency.data.remote

import com.google.gson.annotations.SerializedName

data class RemoteCurrencyDto(
    @SerializedName("Cur_ID") val curId: Int,
    @SerializedName("Cur_ParentID") val curParentId: Int,
    @SerializedName("Cur_Code") val curCode: String,
    @SerializedName("Cur_Abbreviation") val curAbbreviation: String,
    @SerializedName("Cur_Name") val curName: String,
    @SerializedName("Cur_QuotName") val curQuotName: String,
    @SerializedName("Cur_Scale") val curScale: Int,
    @SerializedName("Cur_OfficialRate") val curOfficialRate: Double
)