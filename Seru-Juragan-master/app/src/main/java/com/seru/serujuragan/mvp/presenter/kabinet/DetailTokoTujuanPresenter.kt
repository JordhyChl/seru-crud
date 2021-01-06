package com.seru.serujuragan.mvp.presenter.kabinet

import android.os.Environment
import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.Pdf
import com.seru.serujuragan.data.request.CabinetMandiriReq
import com.seru.serujuragan.mvp.contract.kabinet.DetailTokoTujuanContract
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.SavePDF
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.*
import java.lang.Exception

/**
 * Created by Mahendra Dev on 13/05/2020.
 */
class DetailTokoTujuanPresenter: DetailTokoTujuanContract.Presenter {

    private val TAG = ListStatusCabinetPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private val exportService: ApiService = ApiService.export()
    private lateinit var view: DetailTokoTujuanContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun loadDetailToko(idTokoAsal: String,idTokoTujuan: String) {
        val subscribe = apiService.getDetailOutletTujuan(contentType, token, idTokoAsal,idTokoTujuan)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (it.code == 200){
                    Log.d(TAG, "response code : ${it.code}")
                    view.showProgressDialog(false)
                    view.loadDetailTokoSuccess(it.data)
                }else if (it.code == 401){
                    Log.e(TAG, "response error code : ${it.code}")
                }
            }, {
                when(it){
                    is HttpException -> {
                        val errorCode = it.code()
                        Log.e(TAG,"http error code $errorCode")
                        when (errorCode) {
                            401 -> {
                                view.showProgressDialog(false)
                                view.doLogout()
                            }
                            else -> {
                                view.showProgressDialog(false)
                                view.showErrorMessage(it.message(), it.code())
                            }
                        }
                    }
                    else -> {
                        Log.e(TAG,"other error : ${it.message}")
                        view.showProgressDialog(false)
                        view.showErrorMessage(it.localizedMessage, 0)
                    }
                }
            })

        subscriptions.add(subscribe)
    }

    override fun submitRequestCabinet(idTokoAsal: String,idTokoTujuan: String, cabinetMandiriReq: CabinetMandiriReq) {
        var subscribe = apiService.submitRequestPemindahan(contentType, token, idTokoAsal, idTokoTujuan, cabinetMandiriReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    Log.i(TAG,"response code : ${it.data}")
                    view.submitRequestSuccess(it.data)
                }
            }, {
                when(it){
                    is HttpException -> {
                        val errorCode = it.code()
                        Log.e(TAG,"http error code $errorCode")
                        when (errorCode) {
                            401 -> {
                                view.showProgressDialog(false)
                                view.doLogout()
                            }
                            else -> {
                                view.showProgressDialog(false)
                                view.submitRequestError(it.message(), it.code())
                            }
                        }
                    }
                    else -> {
                        Log.e(TAG,"other error : ${it.message}")
                        view.showProgressDialog(false)
                        view.submitRequestError(it.localizedMessage, 0)
                    }
                }
            })

        subscriptions.add(subscribe)
    }

    override fun downloadPdfShare(idRequest:String, typeRequest:String) {
        Log.e(TAG,"id: $idRequest")
        val pdfName = "${typeRequest}_${idRequest}.pdf"
        var subscribe = apiService.getPDFShare(contentType, token, idRequest, typeRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.isSuccessful){
                    val pdf = it.body()!!
                    if (SavePDF.saveFile(pdf, pdfName)){
                        Log.d(TAG,"${it.isSuccessful}")
                        view.downloadPDFSuccess(pdfName)
                    }else{
                        Log.e(TAG,"error simpan PDF")
                        view.showErrorMessage("gagal simpan",2)
                    }
                }else{
                    Log.e(TAG,"load pdf ${it.errorBody()}")
                    view.showProgressDialog(false)
                }
            }
        subscriptions.add(subscribe)
    }

    /*override fun downloadPDF(pathName: String, idRequest: String) {
        Log.e(TAG,"id: $pathName")
        val pdfName = "${pathName}_${idRequest}.pdf"
        var subscribe = exportService.getPDF(pathName, idRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.isSuccessful){
                    val pdf = it.body()!!
                    if (SavePDF.saveFile(pdf, pdfName)){
                        Log.d(TAG,"${it.isSuccessful}")
                        view.downloadPDFSuccess(pdfName)
                    }else{
                        Log.e(TAG,"error simpan PDF")
                        view.showErrorMessage("gagal simpan",2)
                    }
                }else{
                    Log.e(TAG,"load pdf ${it.errorBody()}")
                    view.showProgressDialog(false)
                }
            }
        subscriptions.add(subscribe)
    }*/

    override fun subscribe() {
        TODO("Not yet implemented")
    }

    override fun unsubcribe() {
        subscriptions.clear()
    }

    override fun attach(view: DetailTokoTujuanContract.View) {
        this.view = view
    }
}