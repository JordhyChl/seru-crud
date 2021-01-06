package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class ListDbHunterRes (
    @SerializedName("id") var id_hunter : String,
    @SerializedName("name") var hunter_name: String,
    @SerializedName("summary") var task_summary: Summary
)

data class Summary(
    @SerializedName("deal") var approve_task: Int,
    @SerializedName("tunda") var pending_task: Int,
    @SerializedName("batal") var cancel_task: Int,
    @SerializedName("total") var total_task: Int
)