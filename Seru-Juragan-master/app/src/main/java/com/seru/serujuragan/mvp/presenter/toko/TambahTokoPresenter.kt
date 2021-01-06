package com.seru.serujuragan.mvp.presenter.toko

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.AddTokoReq
import com.seru.serujuragan.data.request.FilterTokoReq
import com.seru.serujuragan.data.response.DataDistrict
import com.seru.serujuragan.data.response.DataVillages
import com.seru.serujuragan.mvp.contract.toko.TambahTokoContract
import com.seru.serujuragan.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File

/**
 * Created by Mahendra Dev on 29/12/2019
 */
class TambahTokoPresenter : TambahTokoContract.Presenter{

    private val TAG = TambahTokoPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: TambahTokoContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun loadTimeline() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadListDistrict() {
        var subscribe = apiService.getAllDistrict(contentType,token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    view.loadAllDistrictSuccess(it.data as MutableList<DataDistrict>)
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

    override fun loadListVillage(kecId:String) {
        var subscribe = apiService.getAllVillage(contentType,token, kecId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    view.loadAllVilageSuccess(it.data as MutableList<DataVillages>)
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

    override fun filterToko(filterToko: FilterTokoReq) {

        Log.i(TAG, "filter : ${filterToko.id_kec_toko}")
        var subscribe = apiService.filterSemuaToko(contentType,token, filterToko)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.filterTokoSucces(it.data)
                    view.showProgressDialog(false)
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

    override fun uploadFoto(namePrefix:String, imgFile: File?) {
        val pickImg = imgFile?.absolutePath
        val token = AppPreference.token
        val imgType = imgFile?.extension
        val filename = namePrefix + AppPreference.tempLocation + ".$imgType"
        val reqBody = RequestBody.create(MediaType.parse("image/jpeg"), File(pickImg))
        val imgBody = MultipartBody.Part.createFormData("image", filename, reqBody)
        Log.i(TAG,"log image upload name : $filename")

        var subscribe = apiService.uploadFoto(token, imgBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    Log.i(TAG,"response code : ${it.data.imgId}")
                    view.uploadImgSuccess(namePrefix, imgFile, it.data)
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
                                view.showErrorMessage("upimagefail", it.code())
                            }
                        }
                    }
                    else -> {
                        Log.e(TAG,"other error : ${it.message}")
                        view.showProgressDialog(false)
                        view.showErrorMessage("upimagefail", 0)
                    }
                }
            })

        subscriptions.add(subscribe)
    }

    override fun submitForm(dataToko: AddTokoReq) {

        var subscribe = apiService.addToko(contentType, token, dataToko)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    Log.i(TAG,"response code : ${it.data}")
                    view.submitFormSuccess(it.data)
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
                                view.submitFormError(it.message(), it.code())
                            }
                        }
                    }
                    else -> {
                        Log.e(TAG,"other error : ${it.message}")
                        view.showProgressDialog(false)
                        view.submitFormError(it.localizedMessage, 0)
                    }
                }
            })

        subscriptions.add(subscribe)
    }


    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubcribe() {
        subscriptions.clear()
    }

    override fun attach(view: TambahTokoContract.View) {
        this.view = view
    }
}