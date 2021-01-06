package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 16/01/2020
 */
class ListTokoRes (
    @SerializedName("id_outlet") var outlet_id : String,
    @SerializedName("name") var outlet_name: String,
    @SerializedName("phone") var outlet_phone: String,
    @SerializedName("address") var outlet_address: String,
    @SerializedName("survey_date") var date_survey: Long,
    @SerializedName("delivery_date") var date_delivery: Long,
    @SerializedName("status") val status_survey : ValidateStatus,
    @SerializedName("location") val shop_location : LokasiToko,
    @SerializedName("distance") val shop_distance : Double
)

data class ValidateStatus (
    @SerializedName("section") val section : String,
    @SerializedName("status_id") val statusId : String,
    @SerializedName("status") val statusName : String
)

data class LokasiToko (
    @SerializedName("latitude") val shop_latitude : Double,
    @SerializedName("longitude") val shop_longitude : Double
)