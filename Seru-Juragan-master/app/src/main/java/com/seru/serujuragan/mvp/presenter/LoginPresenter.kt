package com.seru.serujuragan.mvp.presenter

import android.util.Log
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.data.request.UserLoginReq
import com.seru.serujuragan.mvp.contract.LoginContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Mahendra Dev on 23/12/2019
 */

class LoginPresenter : LoginContract.Presenter{

    private val TAG = LoginPresenter::class.java.simpleName
    private val subscriptions = CompositeDisposable()
    private val apiService: ApiService = ApiService.create()
    private lateinit var view: LoginContract.View


    override fun loginUser(phoneNumber:String, pin: String) {
        val dataReq = UserLoginReq(phoneNumber, pin)

        Log.i(TAG,"email : $phoneNumber")
        Log.i(TAG,"pin : $pin")

        var subscribe = apiService.login(dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (it.code == 200){
                    Log.d(TAG, "response code : ${it.code}")
                    view.showProgressDialog(false)
                    view.loginSuccess(it.data)
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
                                //view.doLogout()
                                view.showErrorMessage(it.message(), it.code())
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

    override fun attach(view: LoginContract.View) {
        this.view = view
    }
}