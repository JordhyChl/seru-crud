package com.seru.serujuragan.data

import com.google.gson.annotations.SerializedName
import com.seru.serujuragan.ui.base.BaseMainFragment

/**
 * Created by Mahendra Dev on 17/12/2019
 */
data class MainData(val fragment: BaseMainFragment, val title: Int, val menu: Int)

data class Pdf(
    @SerializedName("id_survey") var id_survey: String,
    @SerializedName("id_image") var id_image_signature: String
)