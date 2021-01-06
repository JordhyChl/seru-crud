package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class DetailRequestMandiriRes (
    @SerializedName("id") var requestId: String,
    @SerializedName("cabinet") var cabinet: Cabinet,
    @SerializedName("outlet_asal") var detailTokoAsal: DetailTokoKabinetRes,
    @SerializedName("outlet_tujuan") var detailTokoTujuan: DetailTokoKabinetRes,
    @SerializedName("reason") var recallReason: String,
    @SerializedName("status_reason") var rejectReason: String
)