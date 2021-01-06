package com.seru.serujuragan.mvp.presenter.lainlain

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.ListDbHunterRes
import com.seru.serujuragan.mvp.contract.lainlain.DatabaseHunterContract
import com.seru.serujuragan.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class DatabaseHunterPresenter: DatabaseHunterContract.Presenter {

    private val TAG = DatabaseHunterPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: DatabaseHunterContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun loadAllDbHunter() {
        var subscribe = apiService.getListDbHunter(contentType,token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    if (it.data.isNullOrEmpty()){
                        view.showProgressDialog(false)
                        view.loadDbHunterNull()
                    }else{
                        view.showProgressDialog(false)
                        view.loadDbHunterSuccess(it.data as MutableList<ListDbHunterRes>)
                    }
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubcribe() {
        subscriptions.clear()
    }

    override fun attach(view: DatabaseHunterContract.View) {
        this.view = view
    }
}