package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Mahendra Dev on 23/12/2019
 */
class UserLoginRes(
    @SerializedName("token") val token: String,
    @SerializedName("id_juragan") val idJuragan: String,
    @SerializedName("name_juragan") val nameJuragan: String,
    @SerializedName("location") val location: LocationJuragan,
    @SerializedName("scope") val scope: Scope
)

data class LocationJuragan(
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("latitude") val latitude: Float
)

data class Scope(
    @SerializedName("radius") val radius: Int,
    @SerializedName("threshold") val threshold: Int
)
//data class User(@field:SerializedName("token") val token : String? = null):Serializable