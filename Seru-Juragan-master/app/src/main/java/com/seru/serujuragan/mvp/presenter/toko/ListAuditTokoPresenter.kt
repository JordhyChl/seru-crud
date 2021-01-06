package com.seru.serujuragan.mvp.presenter.toko

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.ListAuditTokoRes
import com.seru.serujuragan.mvp.contract.toko.ListAuditTokoContract
import com.seru.serujuragan.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Mahendra Dev on 27/01/2020
 */
class ListAuditTokoPresenter: ListAuditTokoContract.Presenter {
    private val TAG = ListAuditTokoPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: ListAuditTokoContract.View
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token



    override fun loadListAuditToko() {
        var subscribe = apiService.getListAuditToko(contentType, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200){
                    view.showProgressDialog(false)
                    view.loadListAuditSuccess(it.data as MutableList<ListAuditTokoRes>)
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

    override fun attach(view: ListAuditTokoContract.View) {
        this.view = view
    }
}