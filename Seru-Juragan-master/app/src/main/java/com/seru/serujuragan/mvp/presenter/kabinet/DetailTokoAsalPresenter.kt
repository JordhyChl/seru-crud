package com.seru.serujuragan.mvp.presenter.kabinet

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.mvp.contract.kabinet.DetailTokoAsalContract
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
 * Created by Mahendra Dev on 13/05/2020.
 */
class DetailTokoAsalPresenter: DetailTokoAsalContract.Presenter {

    private val TAG = ListStatusCabinetPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: DetailTokoAsalContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun loadDetailToko(idToko: String) {
        val subscribe = apiService.getDetailOutletAsal(contentType, token, idToko)
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

    override fun submitCheckin(path:String, idRequest: String, latitude: String, longitude: String) {
        val subscribe = apiService.checkinToko(contentType, token,path, idRequest, latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (it.code() == 202){
                    Log.d(TAG, "response code : ${it.code()}")
                    view.showProgressDialog(false)
                    view.checkinSuccess()
                }else if (it.code() == 401){
                    Log.e(TAG, "response error code : ${it.code()}")
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

    override fun uploadFoto(pathName: String, imgFile: File?) {
        val pickImg = imgFile?.absolutePath
        val token = AppPreference.token
        val imgType = imgFile?.extension
        val filename = pathName + AppPreference.tempLocation + ".$imgType"
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
                    view.uploadImgSuccess(pathName, imgFile, it.data)
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

    override fun subscribe() {
        TODO("Not yet implemented")
    }

    override fun unsubcribe() {
        subscriptions.clear()
    }

    override fun attach(view: DetailTokoAsalContract.View) {
        this.view = view
    }
}