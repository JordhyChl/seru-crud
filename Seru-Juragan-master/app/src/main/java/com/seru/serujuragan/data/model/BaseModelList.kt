package com.seru.serujuragan.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 31/12/2019
 */
class BaseModelList<MODEL>(
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : List<MODEL>
)
