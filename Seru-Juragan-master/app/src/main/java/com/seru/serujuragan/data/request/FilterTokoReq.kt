package com.seru.serujuragan.data.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 29/12/2019
 */
data class FilterTokoReq (
    @SerializedName("id") var id_toko: String,
    @SerializedName("name") var nama_toko : String,
    @SerializedName("district") var id_kec_toko : String,
    @SerializedName("village") var id_kel_toko : String,
    @SerializedName("phone") var pn_toko : String
)