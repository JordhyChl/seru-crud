package com.seru.serujuragan.data.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 17/05/2020.
 */
class SubmitProsesPemindahan (
    @SerializedName("answer") val kelengkapanKabinet:SurveyKabinet,
    @SerializedName("images") val imageSurvey: List<Picture>
)

data class SurveyKabinet(
    @SerializedName("unit_cabinet_status") val cabinet_status : String,
    @SerializedName("unit_cabinet_value") val cabinet_val : Int,
    @SerializedName("unit_highlighter_status") val highlighter_status : String,
    @SerializedName("unit_highlighter_value") val highlighter_val : Int,
    @SerializedName("unit_keranjang_status") val keranjang_status : String,
    @SerializedName("unit_keranjang_value") val keranjang_val : Int,
    @SerializedName("unit_panduan_status") val panduan_status : String,
    @SerializedName("unit_panduan_value") val panduan_val : Int,
    @SerializedName("unit_kunci_status") val kunci_status : String,
    @SerializedName("unit_kunci_value") val kunci_val : Int,
    @SerializedName("unit_scrapper_status") val scrapper_status : String,
    @SerializedName("unit_scrapper_value") val scrapper_val : Int,
    @SerializedName("unit_lainnya_status") val other_status : String,
    @SerializedName("unit_lainnya_value") val other_val : Int
)
