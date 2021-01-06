package com.seru.serujuragan.mvp.presenter

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.mvp.contract.HomeContract
import com.seru.serujuragan.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class HomePresenter: HomeContract.Presenter {

    private val TAG = HomePresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: HomeContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun getTaskCount() {
        var subscribe = apiService.getTaskCount(contentType, token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    view.loadSuccess(it)
                }
            },{
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubcribe() {
        subscriptions.clear()
    }

    override fun attach(view: HomeContract.View) {
        this.view = view
    }
}