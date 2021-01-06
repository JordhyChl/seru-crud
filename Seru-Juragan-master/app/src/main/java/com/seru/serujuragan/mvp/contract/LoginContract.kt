package com.seru.serujuragan.mvp.contract

import com.seru.serujuragan.data.response.UserLoginRes

/**
 * Created by Mahendra Dev on 23/12/2019
 */
class LoginContract {

    interface View : BaseContract.View {
        fun loginSuccess(userLoginRes: UserLoginRes)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun loginUser(phone: String, pin:String)
    }
}