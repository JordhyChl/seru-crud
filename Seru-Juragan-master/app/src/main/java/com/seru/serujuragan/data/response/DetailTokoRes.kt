package com.seru.serujuragan.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 29/12/2019
 */
data class DetailTokoRes (
    @SerializedName("id") var outlet_id: String,
    @SerializedName("name") var shop_name: String,
    @SerializedName("owner") var owner_name: String,
    @SerializedName("phone") var shop_telp1: String,
    @SerializedName("phone2") var shop_telp2: String,
    @SerializedName("address") val address: String,
//    @SerializedName("province") val province: Province,
//    @SerializedName("city") val city : City,
    @SerializedName("district") val district: District,
    @SerializedName("village") val village: Village,
    @SerializedName("outlet_info") val outlet_info: OutletInfo?,
    @SerializedName("location_info") val location: Location,
    @SerializedName("status_outlet") val outlet_status: OutletStatus?
)

data class Province (
    @SerializedName("id") val provienceID: String,
    @SerializedName("name") val provienceName: String
)

data class City (

    @SerializedName("id") val cityID : Int,
    @SerializedName("name") val cityName : String
)

data class District (
    @SerializedName("id") val districtID: String,
    @SerializedName("name") val districtName: String
)

data class Village (
    @SerializedName("id") val villagesID: String,
    @SerializedName("name") val villagesName: String
)

data class OutletInfo (

    @SerializedName("blackout_intensity") val listrikPadam : String,
    @SerializedName("electricity_capacity") val listrikCapacity : String,
    @SerializedName("picture") val picture : List<Picture>,
    @SerializedName("area_radius") val area_radius : String,
    @SerializedName("freezer") val freezer : String,
    @SerializedName("id_outlet_type") val id_outlet_type : String,
    @SerializedName("id_ownership_status") val id_ownership_status : String,
    @SerializedName("id_street_type") val id_street_type : String,
    @SerializedName("kulkas") val kulkas : Kulkas,
    @SerializedName("perdana") val perdana : String,
    @SerializedName("selling") val selling : Selling
)

data class Picture (
    @SerializedName("id") var picture_id: String,
    @SerializedName("name") var picture_name: String
)

data class Kulkas (
    @SerializedName("exist") val exist : String,
    @SerializedName("type") val type : String
)

data class Selling (
    @SerializedName("selling") val selling : String,
    @SerializedName("name") val name : List<EsBrand>
)

data class EsBrand (
    var brandName : String
)

data class Location (
    @SerializedName("latitude") val shop_latitude: Double,
    @SerializedName("longitude") val shop_longitude: Double

)

data class OutletStatus(
    @SerializedName("recommendation_date") val dateRecommendation : Long,
    @SerializedName("status") val status : Status,
    @SerializedName("history") val timelineHistory : List<History>
)

data class Status(
    @SerializedName("date") val date_status : Long,
    @SerializedName("is_active") val status_active : Int,
    @SerializedName("note") val note_outlet : String,
    @SerializedName("section") val section : String,
    @SerializedName("status") val status_value : String,
    @SerializedName("status_id") val id_status : String
)

data class History(
    @SerializedName("date") val date_status : Long,
    @SerializedName("is_active") val status_active : Int,
    @SerializedName("section") val section : String,
    @SerializedName("status") val status_value : String,
    @SerializedName("status_id") val id_status : String
)