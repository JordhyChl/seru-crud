package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 10/05/2020.
 */
class ListCabinetOutletRes (
    @SerializedName("id") var outlet_id : String,
    @SerializedName("name") var outlet_name: String,
    @SerializedName("phone") var outlet_phone: String,
    @SerializedName("date") var join_date: Long,
    @SerializedName("cabinet") var cabinet_code: String
)