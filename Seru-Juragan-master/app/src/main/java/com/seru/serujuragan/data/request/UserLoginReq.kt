package com.seru.serujuragan.data.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 23/12/2019
 */
data class UserLoginReq(
    @SerializedName("nohandphone") val phoneNumber:String,
    @SerializedName("pin") val pin:String
)