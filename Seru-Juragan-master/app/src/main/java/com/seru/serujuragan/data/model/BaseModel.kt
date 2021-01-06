package com.seru.serujuragan.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Mahendra Dev on 23/12/2019
 */
class BaseModel<MODEL>(
    @SerializedName("data")
    var data: MODEL
) : Serializable {
    @SerializedName("code")
    var code: Int? = null
    @SerializedName("message")
    var message: String? = null
}