package com.seru.serujuragan.mvp.contract.lainlain

import com.seru.serujuragan.data.response.ListDbTokoRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class DatabaseTokoContract {
    interface View : BaseContract.View {
        fun loadDbTokoSuccess(dataListToko: MutableList<ListDbTokoRes>)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadAllDbToko()
    }
}