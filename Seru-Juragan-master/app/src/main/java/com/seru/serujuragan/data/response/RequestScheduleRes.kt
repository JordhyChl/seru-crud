package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 15/05/2020.
 */
class RequestScheduleRes (
    @SerializedName("id") var request_id : String,
    @SerializedName("cabinet") var cabinet: Cabinet,
    @SerializedName("tarik") var recall_info : RecallCabinet,
    @SerializedName("kirim") var sent_info: SentCabinet,
    @SerializedName("notes") var schedule: String
)

data class RecallCabinet(
    @SerializedName("schedule") var schedule: Long,
    @SerializedName("vehicle_number") var vehicle_number: String,
    @SerializedName("driver_number") var driver_number: String
)

data class SentCabinet(
    @SerializedName("schedule") var schedule: Long,
    @SerializedName("vehicle_number") var vehicle_number: String,
    @SerializedName("driver_number") var driver_number: String
)