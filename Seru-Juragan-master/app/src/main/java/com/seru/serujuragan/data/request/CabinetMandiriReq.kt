package com.seru.serujuragan.data.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 14/05/2020.
 */
class CabinetMandiriReq (
    @SerializedName("estimate") var moveEstimate: MoveEstimate,
    @SerializedName("notes") var moveNotes : MoveNotes,
    @SerializedName("reason") var reasonWithdrawal : String,
    @SerializedName("signature") var signatureId : String
)

data class MoveEstimate(
    @SerializedName("withdrawal") var withdrawalDate : Long,
    @SerializedName("shipping") var shippingDate : Long
)

data class MoveNotes(
    @SerializedName("withdrawal") var withdrawalNote : String,
    @SerializedName("shipping") var shippingNote : String
)