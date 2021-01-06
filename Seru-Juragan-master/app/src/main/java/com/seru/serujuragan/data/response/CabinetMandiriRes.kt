package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class CabinetMandiriRes (
    @SerializedName("id") var requestId: String,
    @SerializedName("cabinet") var cabinet: Cabinet
)