package com.seru.serujuragan.mvp.contract

import com.seru.serujuragan.data.model.BaseModelData

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class HomeContract {
    interface View : BaseContract.View {
        fun loadSuccess(countData: BaseModelData)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun getTaskCount()
    }
}