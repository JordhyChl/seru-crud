package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class RequestInfoRes (
    @SerializedName("id") var requestId: String,
    @SerializedName("cabinet") var cabinet: Cabinet,
    @SerializedName("location") var location: Location,
    @SerializedName("document") var document: Document
)

data class RequestState(
    @SerializedName("id") var stateId: String,
    @SerializedName("name") var stateName: String
)

data class Document(
    @SerializedName("bap_form") var bap_url: String,
    @SerializedName("form_a1") var a1_url: String,
    @SerializedName("adr_form") var adr_url: String
)