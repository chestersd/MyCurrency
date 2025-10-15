package dev.kadyko.mycurrency.data.remote.model

data class CurrencyResponse(
    @SerializedName("Cur_ID") val id: Int,
    @SerializedName("Cur_ParentID") val parentId: Int,
    @SerializedName("Cur_Code") val code: String,
    @SerializedName("Cur_Abbreviation") val abbreviation: String,
    @SerializedName("Cur_Name") val name: String,
    @SerializedName("Cur_Name_Bel") val nameBel: String,
    @SerializedName("Cur_Name_Eng") val nameEng: String,
    @SerializedName("Cur_QuotName") val quotName: String,
    @SerializedName("Cur_QuotName_Bel") val quotNameBel: String,
    @SerializedName("Cur_QuotName_Eng") val quotNameEng: String,
    @SerializedName("Cur_NameMulti") val nameMulti: String,
    @SerializedName("Cur_Name_BelMulti") val nameBelMulti: String,
    @SerializedName("Cur_Name_EngMulti") val nameEngMulti: String,
    @SerializedName("Cur_Scale") val scale: Int,
    @SerializedName("Cur_Periodicity") val periodicity: Int,
    @SerializedName("Cur_DateStart") val dateStart: String,
    @SerializedName("Cur_DateEnd") val dateEnd: String
)

