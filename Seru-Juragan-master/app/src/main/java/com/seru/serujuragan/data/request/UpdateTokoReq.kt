package com.seru.serujuragan.data.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Mahendra Dev on 17/01/2020
 */
class UpdateTokoReq(
    @SerializedName("lokasi_mitra") var shop_location: ShopLocationUpdate,
    @SerializedName("outlet") var outlet : OutletUpdate,
    @SerializedName("survey") var survey : SurveyUpdate,
    @SerializedName("state") var state : StateUpdate
)

data class ShopLocationUpdate(
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double
)
data class OutletUpdate(
    @SerializedName("id") var outlet_id: String,
    @SerializedName("name") var outlet_name: String,
    @SerializedName("owner") var owner_name: String,
    @SerializedName("address") var outle_address: String,
    @SerializedName("phone") var owner_phone1: String,
    @SerializedName("phone2") var owner_phone2: String,
    @SerializedName("id_district") val id_district : String,
    @SerializedName("id_village") val id_village : String
)

data class SurveyUpdate (
    @SerializedName("id_outlet_type") val id_outlet_type : String,
    @SerializedName("id_ownership_status") val id_ownership_status : String,
    @SerializedName("id_street_type") val id_street_type : String,
    @SerializedName("area_radius") val area_radius : String,
    @SerializedName("selling") val selling : SellingUpdate,
    @SerializedName("kulkas") val kulkas : KulkasUpdate,
    @SerializedName("perdana") val perdana : String,
    @SerializedName("freezer") val tersediaTempat : String,
    @SerializedName("electricity_capacity") val listrikCapacity : String,
    @SerializedName("blackout_intensity") val freqListrict : String,
    @SerializedName("picture") val picture : List<PictureUpdate>
)

data class SellingUpdate (
    @SerializedName("selling") val selling : String,
    @SerializedName("name") val name : List<EsBrand>
)

data class KulkasUpdate (
    @SerializedName("exist") val exist : String,
    @SerializedName("type") val type : String
)

data class StateUpdate (
    @SerializedName("state_id") val state_id : String,
    @SerializedName("schedule") val schedule : Long,
    @SerializedName("note") val note : String
)

data class PictureUpdate(
    @SerializedName("id") var picture_id: String,
    @SerializedName("name") var picture_name: String
)