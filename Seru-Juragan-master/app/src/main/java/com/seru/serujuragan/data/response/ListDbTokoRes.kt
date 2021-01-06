package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class ListDbTokoRes (
    @SerializedName("id_unilever") var id_uli : String,
    @SerializedName("id_seru") var id_seru : String,
    @SerializedName("name") var nama_toko: String,
    @SerializedName("owner") var owner_toko: String,
    @SerializedName("join_date") var tanggal_bergabung: Long
)