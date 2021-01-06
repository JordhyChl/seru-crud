package com.seru.serujuragan.mvp.contract.toko

import com.seru.serujuragan.data.response.DetailTokoRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 29/12/2019
 */
class DetailTokoContract {
    interface View : BaseContract.View {
        fun loadDetailtokoSuccess(detailTokoRes: DetailTokoRes)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailToko(idToko: String)
    }
}