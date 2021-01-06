package com.seru.serujuragan.data.request

import com.google.gson.annotations.SerializedName
import com.seru.serujuragan.data.response.Cabinet
import com.seru.serujuragan.data.response.RecallCabinet
import com.seru.serujuragan.data.response.SentCabinet

/**
 * Created by Mahendra Dev on 15/05/2020.
 */
class RequestScheduleReq (
//    @SerializedName("id") var request_id : String,
//    @SerializedName("cabinet") var cabinet: Cabinet,
    @SerializedName("reschedule") var is_reschedule: Boolean,
    @SerializedName("tarik") var recall_info : RecallCabinet,
    @SerializedName("kirim") var sent_info: SentCabinet,
    @SerializedName("notes") var schedule_note: String
)