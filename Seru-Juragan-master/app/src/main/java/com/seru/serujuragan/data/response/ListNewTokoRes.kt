package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 30/12/2019
 */
data class ListNewTokoRes(

    @SerializedName("id_district") val id_district : String,
    @SerializedName("districtname") val district_name : String,
    @SerializedName("village") val listaArea : MutableList<AreaDetail>
)

data class AreaDetail (

    @SerializedName("id_village") val id_village : String,
    @SerializedName("village_name") val villageName : String,
    @SerializedName("deal") val deal : Int,
    @SerializedName("tunda") val tunda : Int
)