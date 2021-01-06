package com.seru.serujuragan.mvp.contract.lainlain

import com.seru.serujuragan.data.response.DetailTokoRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class DetailTokoDbContract {
    interface View : BaseContract.View {
        fun loadDetailtokoSuccess(detailTokoRes: DetailTokoRes)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailToko(idToko: String)
    }
}