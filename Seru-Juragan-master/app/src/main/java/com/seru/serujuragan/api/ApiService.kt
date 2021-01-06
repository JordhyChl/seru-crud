package com.seru.serujuragan.api

import com.seru.serujuragan.BuildConfig
import com.seru.serujuragan.data.Pdf
import com.seru.serujuragan.data.model.BaseModel
import com.seru.serujuragan.data.model.BaseModelData
import com.seru.serujuragan.data.model.BaseModelList
import com.seru.serujuragan.data.request.*
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.util.Constants
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body reqUserLoginLogin: UserLoginReq): Observable<BaseModel<UserLoginRes>>

    @GET("etc/task")
    fun getTaskCount(@Header("Content-Type") contentType:String,
                     @Header("Authorization") token:String): Observable<BaseModelData>

    @GET("verify/mitra")
    fun getListNewToko(@Header("Content-Type") contentType:String,
                       @Header("Authorization") token:String): Observable<BaseModelList<ListNewTokoRes>>

    @GET("verify/mitra/list/validate")
    fun getListValidateToko(@Header("Content-Type") contentType:String,
                    @Header("Authorization") token:String): Observable<BaseModelList<ListTokoRes>>

    @GET("verify/mitra/list/all")
    fun getListStatusToko(@Header("Content-Type") contentType:String,
                            @Header("Authorization") token:String): Observable<BaseModelList<ListTokoRes>>

    @PUT("verify/mitra/update")
    fun updateToko(@Header("Content-Type") contentType:String,
                   @Header("Authorization") token:String,
                   @Body reqUpdateToko: UpdateTokoReq) : Observable<BaseModel<AddTokoRes>>

    @POST("verify/mitra/search/validate")
    fun filterValidasiToko(@Header("Content-Type") contentType:String,
                               @Header("Authorization") token:String,
                               @Body reqSearchToko: FilterTokoReq): Observable<BaseModelList<ListTokoRes>>

    @POST("verify/mitra/search/all")
    fun filterSemuaToko(@Header("Content-Type") contentType:String,
                   @Header("Authorization") token:String,
                   @Body reqSearchToko: FilterTokoReq): Observable<BaseModelList<ListTokoRes>>

    @POST("verify/mitra/search/location")
    fun filterLocationToko(@Header("Content-Type") contentType:String,
                           @Header("Authorization") token:String,
                           @Query("latitude") lat: Double,
                           @Query("longitude") long: Double): Observable<BaseModelList<ListTokoRes>>

    @POST("verify/mitra/add") //add calon mitra
    fun addToko(@Header("Content-Type") contentType:String,
                @Header("Authorization") token:String,
                @Body reqAddToko: AddTokoReq) : Observable<BaseModel<AddTokoRes>>

    @GET("verify/mitra/detail") //detail calon mitra
    fun getDetailToko(@Header("Content-Type") contentType:String,
                      @Header("Authorization") token:String,
                      @Query("outlet_id") idToko: String): Observable<BaseModel<DetailTokoRes>>

    @GET("verify/mitra/listAudit")
    fun getListAuditToko(@Header("Content-Type") contentType:String,
                         @Header("Authorization") token:String): Observable<BaseModelList<ListAuditTokoRes>>

    @GET("management/mandiri/request")
    fun getListOutletAsal(@Header("Content-Type") contentType:String,
                          @Header("Authorization") token:String,
                          @Query("id")idTokoAsal: String,
                          @Query("name")nameTokoAsal: String,
                          @Query("phone")phoneTokoAsal: String,
                          @Query("cabinet")cabinetAsal: String): Observable<BaseModelList<ListCabinetOutletRes>>

    @GET("management/mandiri/request/{id}")
    fun getDetailOutletAsal(@Header("Content-Type") contentType:String,
                            @Header("Authorization") token:String,
                            @Path("id") idToko: String): Observable<BaseModel<DetailTokoAsalRes>>

    @GET("management/mandiri/request/{id}/to")
    fun getListOutletTujuan(@Header("Content-Type") contentType:String,
                             @Header("Authorization") token:String,
                            @Path("id") idTokoAsal: String,
                            @Query("id")idTokoTujuan: String,
                            @Query("name")nameTokoTujuan: String,
                            @Query("phone")phoneTokoTujuan: String): Observable<BaseModelList<ListCabinetOutletRes>>

    @GET("management/mandiri/request/{idAsal}/to/{idTujuan}")
    fun getDetailOutletTujuan(@Header("Content-Type") contentType:String,
                              @Header("Authorization") token:String,
                              @Path("idAsal") idTokoAsal: String,
                              @Path("idTujuan") idTokoTujuan: String): Observable<BaseModel<DetailTokoAsalRes>>

    @POST("management/mandiri/request/{idAsal}/to/{idTujuan}")
    fun submitRequestPemindahan(@Header("Content-Type") contentType:String,
                                @Header("Authorization") token:String,
                                @Path("idAsal") idTokoAsal: String,
                                @Path("idTujuan") idTokoTujuan: String,
                                @Body cabinetMandiriReq: CabinetMandiriReq) : Observable<BaseModel<CabinetMandiriRes>>


    @GET("management/mandiri")
    fun getListStatusKabinet(@Header("Content-Type") contentType:String,
                             @Header("Authorization") token:String,
                             @Query("id")idRequest: String,
                             @Query("name")nameTokoAsal: String,
                             @Query("phone")phoneTokoAsal: String,
                             @Query("qrcode")qrCodeCabinet: String,
                             @Query("status_id") statusRequestId: String): Observable<BaseModelList<ListCabinetRes>>

    @GET("management/mandiri/process/{idRequest}")
    fun getInfoRequest(@Header("Content-Type") contentType:String,
                         @Header("Authorization") token:String,
                         @Path("idRequest") idRequest: String): Observable<BaseModel<RequestInfoRes>>

    @GET("management/mandiri/process/{idRequest}/detail")
    fun getDetailRequest(@Header("Content-Type") contentType:String,
                           @Header("Authorization") token:String,
                           @Path("idRequest") idRequest: String): Observable<BaseModel<DetailRequestMandiriRes>>


    @GET("management/mandiri/process/{idRequest}/schedule")
    fun getScheduleRequest(@Header("Content-Type") contentType:String,
                           @Header("Authorization") token:String,
                           @Path("idRequest") idRequest: String): Observable<BaseModel<RequestScheduleRes>>

    @POST("management/mandiri/process/{idRequest}/schedule")
    fun submitScheduleRequest(@Header("Content-Type") contentType:String,
                              @Header("Authorization") token:String,
                              @Path("idRequest") idRequest: String,
                              @Body dateRequest:RequestScheduleReq): Observable<BaseModel<CabinetMandiriRes>>

    @POST("management/mandiri/process/{idRequest}/submit")
    fun submitMovingProcess(@Header("Content-Type") contentType:String,
                              @Header("Authorization") token:String,
                              @Path("idRequest") idRequest: String,
                              @Body submitProsesPemindahan: SubmitProsesPemindahan): Observable<BaseModel<CabinetMandiriRes>>


    @GET("database/mitra")
    fun getListDbToko(@Header("Content-Type") contentType:String,
                        @Header("Authorization") token:String): Observable<BaseModelList<ListDbTokoRes>>

    @GET("database/mitra/{id}")
    fun getDetailDbToko(@Header("Content-Type") contentType:String,
                        @Header("Authorization") token:String,
                        @Path("id") idToko: String): Observable<BaseModel<DetailTokoRes>>

    @GET("database/hunter")
    fun getListDbHunter(@Header("Content-Type") contentType:String,
                        @Header("Authorization") token:String): Observable<BaseModelList<ListDbHunterRes>>

    @GET("profile")
    fun getProfile(@Header("Content-Type") contentType:String,
                   @Header("Authorization") token:String): Observable<BaseModel<UserProfileRes>>

    @GET("area/provinces")
    fun getAllProvince(@Header("Content-Type") contentType:String,
                   @Header("Authorization") token:String): Observable<BaseModelList<DataProvinces>>

    @GET("area/cities")
    fun getAllCity(@Header("Content-Type") contentType:String,
                   @Header("Authorization") token:String): Observable<BaseModelList<DataCities>>

    @GET("area/districts")
    fun getAllDistrict(@Header("Content-Type") contentType:String,
                    @Header("Authorization") token:String): Observable<BaseModelList<DataDistrict>>

    @GET("area/villages")
    fun getAllVillage(@Header("Content-Type") contentType:String,
                      @Header("Authorization") token:String,
                      @Query("id_district") kecamatanID:String): Observable<BaseModelList<DataVillages>>

    @Multipart
    @POST("file/image/upload")
    fun uploadFoto(
                   @Header("Authorization") token:String,
                   @Part imgBody: MultipartBody.Part) : Observable<BaseModel<UploadPicRes>>

    @POST("management/mandiri/{path}/{id}/checkin")
    fun checkinToko(@Header("Content-Type") contentType:String,
                    @Header("Authorization") token:String,
                    @Path("path") requestPath:String,
                    @Path("id") idRequest: String,
                    @Query("latitude") latitude:String,
                    @Query("longitude") longitude:String) : Observable<Response<ResponseBody>>

    @POST("v1/pdf/share")
    fun getSharePdf(@Header("Content-Type") contentType:String,
                    @Header("Authorization") token:String,
                    @Body pdfReq: Pdf) : Observable<Response<ResponseBody>>

    @GET("management/mandiri/{id}/{type}")
    fun getPDFShare(@Header("Content-Type") contentType:String,
                    @Header("Authorization") token:String,
                    @Path("type") requestType:String,
                    @Path("id") idRequest: String): Observable<Response<ResponseBody>>

    @GET("export/{path}")
    fun getPDF(@Path("path") path: String,
               @Query("id") idRequest: String): Observable<Response<ResponseBody>>

    companion object {

        private val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        private val client : OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                .connectTimeout(70, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
        }.build()


        fun create(): ApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun export(): ApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.EXPORT_URL)
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun local(): ApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.LOCAL_BASE_URL)
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}