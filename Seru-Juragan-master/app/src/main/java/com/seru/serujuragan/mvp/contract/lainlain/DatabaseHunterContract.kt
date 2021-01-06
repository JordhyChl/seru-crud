package com.seru.serujuragan.mvp.contract.lainlain

import com.seru.serujuragan.data.response.ListDbHunterRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class DatabaseHunterContract {
    interface View : BaseContract.View {
        fun loadDbHunterSuccess(dataListHunter: MutableList<ListDbHunterRes>)
        fun loadDbHunterNull()
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadAllDbHunter()
    }
}