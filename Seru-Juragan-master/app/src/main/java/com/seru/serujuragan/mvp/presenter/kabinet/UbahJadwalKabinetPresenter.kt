package com.seru.serujuragan.mvp.presenter.kabinet

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.RequestScheduleReq
import com.seru.serujuragan.mvp.contract.kabinet.UbahJadwalKabinetContract
import com.seru.serujuragan.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class UbahJadwalKabinetPresenter: UbahJadwalKabinetContract.Presenter {

    private val TAG = ListStatusCabinetPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: UbahJadwalKabinetContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun loadDetailSchedule(idRequestCabinet: String) {
        val subscribe = apiService.getScheduleRequest(contentType, token, idRequestCabinet)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (it.code == 200){
                    Log.d(TAG, "response code : ${it.code}")
                    view.showProgressDialog(false)
                    view.loadDetailScheduleSuccess(it.data)
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

    override fun submitScheduleProcess(idRequestCabinet: String, req: RequestScheduleReq) {
        val subscribe = apiService.submitScheduleRequest(contentType, token, idRequestCabinet, req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (it.code == 200){
                    Log.d(TAG, "response code : ${it.code}")
                    view.showProgressDialog(false)
                    view.submitScheduleSuccess(it.data)
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

    override fun subscribe() {
        TODO("Not yet implemented")
    }

    override fun unsubcribe() {
        subscriptions.clear()
    }

    override fun attach(view: UbahJadwalKabinetContract.View) {
        this.view = view
    }
}