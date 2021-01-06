package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

class ListCabinetRes (
    @SerializedName("id") var request_id : String,
    @SerializedName("outlet_asal") var outlet_asal : MandiriOutletInfo,
    @SerializedName("outlet_tujuan") var outlet_tujuan : MandiriOutletInfo,
    @SerializedName("cabinet") var cabinet: Cabinet,
    @SerializedName("schedule") var schedule: Long,
    @SerializedName("type") var request_type: RequestType,
    @SerializedName("status") val request_status : CabinetStatus
)

data class MandiriOutletInfo(
    @SerializedName("phone") var outlet_phone : String,
    @SerializedName("id_outlet") var outlet_id : String,
    @SerializedName("name") var outlet_name: String
)

data class RequestType (
    @SerializedName("id") val requestId : Int?,
    @SerializedName("name") val requestName : String?
)

data class CabinetStatus (
    //@SerializedName("section") val section : String?,
    @SerializedName("id") val statusId : Int?,
    @SerializedName("name") val statusName : String?
)