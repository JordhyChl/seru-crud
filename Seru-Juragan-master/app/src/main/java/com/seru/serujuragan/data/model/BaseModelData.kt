package com.seru.serujuragan.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 10/01/2020
 */
class BaseModelData (
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : String
)