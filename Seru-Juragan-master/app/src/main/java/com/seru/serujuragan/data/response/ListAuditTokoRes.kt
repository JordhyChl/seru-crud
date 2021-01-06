package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 03/02/2020
 */
class ListAuditTokoRes (
    @SerializedName("id_outlet") var outlet_id : String,
    @SerializedName("name") var outlet_name: String,
    @SerializedName("phone") var outlet_phone: String,
    @SerializedName("phone2") var outlet_phone2: String,
    @SerializedName("survey_date") var date_survey: Long,
    @SerializedName("delivery_date") var date_delivery: Long,
    @SerializedName("status") val status_survey : AuditStatus,
    @SerializedName("location") val shop_location : LokasiToko,
    @SerializedName("distance") val shop_distance : Double
)

data class AuditStatus (
    @SerializedName("section") val section : String?,
    @SerializedName("id_status") val statusId : Int?,
    @SerializedName("status") val statusName : String?
)