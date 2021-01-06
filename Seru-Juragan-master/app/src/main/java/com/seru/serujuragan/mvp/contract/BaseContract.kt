package com.seru.serujuragan.mvp.contract

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
class BaseContract {

    interface View {
        fun doLogout()
        fun showProgressDialog(show: Boolean)
        fun showErrorMessage(error: String, errorCode: Int)
    }

    interface Presenter<in T>{
        fun subscribe()
        fun unsubcribe()
        fun attach(view: T)
    }
}