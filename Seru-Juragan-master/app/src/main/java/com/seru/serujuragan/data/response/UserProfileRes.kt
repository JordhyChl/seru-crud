package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 23/12/2019
 */
data class UserProfileRes (
    @SerializedName("id") val id: String,
    @SerializedName("id_unilever_owner") val idUnilever: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("created") val created: String,
    @SerializedName("address") val address: String,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("area") val areaData: Area

)

data class Area (
    //@SerializedName("postcode") val postcode: String,
    @SerializedName("country") val country: UserCountry,
    //@SerializedName("country_name") val countryName: String,
    @SerializedName("district") val district: UserDistrict,
    //@SerializedName("district_name") val districtName: String,
    @SerializedName("province") val province: UserProvince,
    //@SerializedName("province_name") val provienceName: String,
    @SerializedName("village") val village: UserVillage
    //@SerializedName("villages_name") val villagesName: String
)

data class UserCountry (
    @SerializedName("id") val countryID: String,
    @SerializedName("name") val countryName: String
)

data class UserDistrict (
    @SerializedName("id") val districtID: String,
    @SerializedName("name") val districtName: String
)

data class UserProvince (
    @SerializedName("id") val provinceID: String,
    @SerializedName("name") val provinceName: String
)

data class UserVillage (
    @SerializedName("id") val villagesID: String,
    @SerializedName("name") val villagesName: String
)