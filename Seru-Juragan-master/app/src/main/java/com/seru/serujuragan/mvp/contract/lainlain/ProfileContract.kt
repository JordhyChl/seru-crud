package com.seru.serujuragan.mvp.contract.lainlain

import com.seru.serujuragan.data.response.UserProfileRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 23/12/2019
 */
class ProfileContract {

    interface View : BaseContract.View {
        fun loadProfileSuccess(userProfile: UserProfileRes)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadProfile()
    }
}