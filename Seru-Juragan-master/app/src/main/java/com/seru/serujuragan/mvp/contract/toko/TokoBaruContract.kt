package com.seru.serujuragan.mvp.contract.toko

import com.seru.serujuragan.data.response.ListNewTokoRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 29/12/2019
 */
class TokoBaruContract {
    interface View : BaseContract.View {
        fun loadListNewTokoSucces(listNewToko : MutableList<ListNewTokoRes>)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadListNewToko()
    }
}