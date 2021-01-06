package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class DetailTokoKabinetRes (
    @SerializedName("id") var outlet_id: String,
    @SerializedName("name") var shop_name: String,
    @SerializedName("owner") var owner_name: String,
    @SerializedName("phone") var shop_telp1: String,
    @SerializedName("phone2") var shop_telp2: String,
    @SerializedName("address") val address: String,
    @SerializedName("district") val district: District,
    @SerializedName("village") val village: Village,
    @SerializedName("location") val location: Location
)