package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 13/05/2020.
 */
data class DetailTokoAsalRes(
    @SerializedName("id") var outlet_id: String,
    @SerializedName("name") var shop_name: String,
    @SerializedName("owner") var owner_name: String,
    @SerializedName("phone") var shop_telp1: String,
    @SerializedName("phone2") var shop_telp2: String,
    @SerializedName("address") val address: String,
    @SerializedName("district") val district: District,
    @SerializedName("village") val village: Village,
    @SerializedName("location") val location: Location,
    @SerializedName("cabinet") val cabinetInfo: Cabinet
)

data class Cabinet(
    @SerializedName("code") val cabinetCode: String,
    @SerializedName("type") val cabinetType: String,
    @SerializedName("qr_code") val cabinetQrCode: String
)
