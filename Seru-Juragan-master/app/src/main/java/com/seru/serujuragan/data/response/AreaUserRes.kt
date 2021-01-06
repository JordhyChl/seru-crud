package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 31/12/2019
 */
class AreaUserRes(
    val provinces: DataProvinces,
    val cities: DataCities,
    val district: DataDistrict,
    val villages: DataVillages
)

data class DataProvinces (

    @SerializedName("id") val province_id : String,
    @SerializedName("id_country") val country_id : String,
    @SerializedName("reference_id") val reference_id : String,
    @SerializedName("name") val province_name : String
)

data class DataCities (

    @SerializedName("id") val city_id : String,
    @SerializedName("id_province") val province_id: String,
    @SerializedName("reference_id") val reference_id : String,
    @SerializedName("name") val city_name : String
)

data class DataDistrict (

    @SerializedName("id") val district_id : String,
    @SerializedName("id_city") val city_id: String,
    @SerializedName("reference_id") val reference_id : String,
    @SerializedName("name") val district_name : String
)

data class DataVillages (

    @SerializedName("id") val village_id : String,
    @SerializedName("id_district") val district_id: String,
    @SerializedName("reference_id") val reference_id : String,
    @SerializedName("name") val village_name : String
)